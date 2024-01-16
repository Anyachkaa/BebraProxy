package ru.justnanix.bebraproxy.network.connection.handler.proxy;

import ru.justnanix.bebraproxy.commands.impl.bot.move.CommandMacro;
import ru.justnanix.bebraproxy.commands.impl.bot.move.CommandMirror;
import ru.justnanix.bebraproxy.network.ProtocolType;
import ru.justnanix.bebraproxy.network.data.Position;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerJoinGamePacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerTabCompletePacket;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.network.connection.handler.NetHandler;
import ru.justnanix.bebraproxy.network.connection.handler.lobby.ui.gui.GuiOptions;
import ru.justnanix.bebraproxy.network.data.WindowAction;
import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.network.packet.impl.client.play.*;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;
import ru.justnanix.bebraproxy.utils.minecraft.TabUtil;
import ru.justnanix.bebraproxy.utils.minecraft.ScoreboardUtil;

import java.util.concurrent.TimeUnit;

public class NetHandlerPlayProxy extends NetHandler {
    public NetHandlerPlayProxy(ProxiedPlayer player) {
        super(player);
        this.initHandler();
    }

    @Override
    public void initHandler() {
        player.getConnectMgr().getChannel().eventLoop()
                .scheduleAtFixedRate(() -> ScoreboardUtil.updateScoreboard(player), 0L, 5L, TimeUnit.SECONDS);
        player.getConnectMgr().getChannel().eventLoop()
                .scheduleAtFixedRate(() -> TabUtil.updateTab(player), 0L, 1L, TimeUnit.SECONDS);
    }

    @Override
    public void onRemoteServerPacket(Packet packet) {
    }

    @Override
    public void onClientPacket(Packet packet) {
        if (packet instanceof ClientTabCompletePacket) {
            ClientTabCompletePacket tabCompletePacket = (ClientTabCompletePacket) packet;
            String prefix = player.getOptionsManager().getCmdPrefix();

            if (tabCompletePacket.getText().startsWith(prefix)) {
                String formatPrefix = tabCompletePacket.getText().startsWith(prefix + "bots") ? prefix + "bots" : prefix;
                if (!tabCompletePacket.getText().substring(formatPrefix.length()).trim().isEmpty()) {
                    (tabCompletePacket.getText().startsWith(prefix + "bots") ?
                            player.getCommandManager().getBotCommands() :
                            player.getCommandManager().getCommands()).stream()
                            .filter(cmd -> (formatPrefix.trim() + " " + cmd.getName()).startsWith(tabCompletePacket.getText()))
                            .filter(cmd -> cmd.getAllowedPlans().contains(player.getAccount().getPlan()))
                            .findFirst().ifPresent(cmd ->
                                    player.getConnectMgr().sendPacket(new ServerTabCompletePacket(new String[]{cmd.getName()})));
                }

                return;
            }
        }

        if (packet instanceof ClientChatPacket) {
            ClientChatPacket p = (ClientChatPacket) packet;

            if (player.getCurrentValueOption() != null) {
                player.getCurrentValueOption().setValue(p.getMessage());
                ChatUtil.sendChatMessage("Значение установлено на &c" + p.getMessage(), player, true);
                player.setCurrentValueOption(null);
                return;
            }

            if (p.getMessage().startsWith(player.getOptionsManager().getCmdPrefix())) {
                player.getCommandManager().onCommand(p.getMessage());
                return;
            } else if (p.getMessage().startsWith(player.getOptionsManager().getOptionByName("Префикс чата").asValueOption().getValue())) {
                String msg = p.getMessage().substring(player.getOptionsManager().getOptionByName("Префикс чата").asValueOption().getValue().length()).trim();
                while (msg.contains("  "))
                    msg = msg.replace("  ", " ");
                msg = ChatUtil.formatSymbols(msg);
                if (msg.trim().isEmpty()) return;

                ChatUtil.sendBroadcastMessage(
                        "&7(&c" + ProtocolType.getByProtocolID(player.getConnectMgr().getPacketCodec().getProtocol()).getPrefix() + "&7) " +
                                "&7[" + player.getAccount().getPlan().getPrefix() + "&7] &c" + player.getAccount().getKeyName() + " &c>> &7" + msg, false);
                return;
            }
        }

        // UI HOOK
        // TODO: Recode that shit (по приколу)
        if (player.getCurrentGui() != null) {
            if (packet instanceof ClientPlayerWindowActionPacket) {
                ClientPlayerWindowActionPacket window = (ClientPlayerWindowActionPacket) packet;
                if (window.getMode() == WindowAction.SHIFT_CLICK_ITEM) {
                    player.getCurrentGui().onClose();
                    player.setCurrentGui(null);
                } else {
                    try {
                        player.getCurrentGui().onAction(window.getMode(), window.getItem(), window.getSlot(), window.getButton());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } else if (packet instanceof ClientCloseWindowPacket) {
                if (!(player.getCurrentGui() instanceof GuiOptions)) {
                    player.getCurrentGui().onClose();
                    player.setCurrentGui(new GuiOptions(player));
                    player.getCurrentGui().onOpen();
                } else {
                    player.getCurrentGui().onClose();
                    player.setCurrentGui(null);
                }
            }

            if (packet instanceof ClientPlayerWindowActionPacket
                    || packet instanceof ClientCloseWindowPacket
                    || packet instanceof ClientPlayerTryUseItemPacket) return;
        }

        if (player.isConnectedToRemote() && player.isFreeCam())
            return;

        CommandMirror mirror = player.getCommandManager().getCommandByClass(CommandMirror.class);
        CommandMacro macro = player.getCommandManager().getCommandByClass(CommandMacro.class);
        if (packet instanceof ClientChatPacket
                || packet instanceof ClientPlayerWindowActionPacket
                || packet instanceof ClientHeldItemChangePacket
                || packet instanceof ClientPlayerTryUseItemPacket
                || packet instanceof ClientCloseWindowPacket) {
            if (mirror.isActive() && mirror.getCurrentRecord() != null) {
                mirror.getCurrentRecord().getRecordPackets().add(packet);
            } else if (macro.isRecording()) {
                macro.getCurrentRecord().getPackets().add(packet);
            }
        }

        if (packet instanceof ClientHeldItemChangePacket) {
            player.setCurrentSlot(((ClientHeldItemChangePacket) packet).getSlotId());
        } else if (packet instanceof ClientPlayerPositionPacket) {
            ClientPlayerPositionPacket p = (ClientPlayerPositionPacket) packet;
            player.setPosition(new Position(p.getX(), p.getY(), p.getZ(), player.getPosition().getYaw(), player.getPosition().getPitch()));
        } else if (packet instanceof ClientPlayerPositionRotationPacket) {
            ClientPlayerPositionRotationPacket p = (ClientPlayerPositionRotationPacket) packet;
            player.setPosition(new Position(p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch()));
        }


        if (player.getRemoteConnectMgr() != null
                && player.getRemoteConnectMgr().isChannelOpen()
                && player.getConnectionInfo() != ConnectionInfo.CONNECTING
                && player.getRemoteConnectMgr().getNetHandler() != null) {
            player.getRemoteConnectMgr().getNetHandler().onClientPacket(packet);
        }
    }
}
