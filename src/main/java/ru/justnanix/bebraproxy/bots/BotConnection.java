package ru.justnanix.bebraproxy.bots;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
//import com.twocaptcha.TwoCaptcha;
//import com.twocaptcha.captcha.Normal;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.md_5.bungee.api.chat.ComponentBuilder;
import ru.justnanix.bebraproxy.bots.chunks.CachedChunk;
import ru.justnanix.bebraproxy.bots.inventory.InventoryContainer;
import ru.justnanix.bebraproxy.bots.network.protocol.codec.PacketCodec;
import ru.justnanix.bebraproxy.bots.network.protocol.data.Session;
import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.client.HandshakePacket;
import ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.client.login.ClientLoginStartPacket;
import ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.client.play.*;
import ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.login.ServerLoginDisconnectPacket;
import ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.login.ServerLoginSetCompressionPacket;
import ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.login.ServerLoginSuccessPacket;
import ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.play.*;
import ru.justnanix.bebraproxy.network.codec.VarInt21FrameCodec;
import ru.justnanix.bebraproxy.network.data.EnumConnectionState;
import ru.justnanix.bebraproxy.network.data.ItemStack;
import ru.justnanix.bebraproxy.network.packet.PacketDirection;
import ru.justnanix.bebraproxy.proxy.ProxyImpl;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;
import ru.justnanix.bebraproxy.utils.minecraft.BasicColor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BotConnection {
    private static final ExecutorService captchaSolver = Executors.newFixedThreadPool(200);

    private static final EventLoopGroup group = Epoll.isAvailable() ?
            new EpollEventLoopGroup(6) :
            new NioEventLoopGroup(6);
    private static final Class<? extends Channel> channelClass = Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class;

    private final Bot bot;
    private boolean active = false;

    public BotConnection(Bot bot) {
        this.bot = bot;
    }

    public void connect(String ip, int port, ProxyImpl proxy) {
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(channelClass)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();

                        pipeline.addFirst(new Socks5ProxyHandler(proxy.getAddress(), proxy.getUsername(), proxy.getPassword()));
                        pipeline.addFirst("timer", new ReadTimeoutHandler(30));
                        pipeline.addLast("frameCodec", new VarInt21FrameCodec());
                        pipeline.addLast("packetCodec", new PacketCodec(EnumConnectionState.LOGIN, PacketDirection.CLIENTBOUND));
                        pipeline.addLast("handler", new SimpleChannelInboundHandler<Packet>() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) {
                                if (!bot.getPlayer().isConnectedToProxy()) {
                                    ctx.close();
                                    return;
                                }

                                Session session = new Session(ctx.channel());
                                session.setConnectionState(EnumConnectionState.LOGIN);
                                session.setUsername(bot.getName());
                                session.setProtocolID(bot.getPlayer().getConnectMgr().getPacketCodec().getProtocol());

                                bot.setSession(session);
                                bot.getPlayer().getBotManager().getBots().add(bot);

                                bot.getSession().sendPackets(
                                        new HandshakePacket(bot.getSession().getProtocolID(), ip, port, 2),
                                        new ClientLoginStartPacket(bot.getName())
                                );
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, Packet p) {
                                if (!bot.getPlayer().isConnectedToProxy()) {
                                    ctx.close();
                                    return;
                                }

                                if (p instanceof ServerJoinGamePacket || p instanceof ServerRespawnPacket) {
                                    bot.getOwnChunks().clear();
                                }

                                if (p instanceof ServerJoinGamePacket) {
                                    bot.setEntityID(((ServerJoinGamePacket) p).getEntityId());
                                    bot.getSession().sendPackets(
                                            new ClientSettingsPacket("ru_ru", (byte) 8, (byte) 0, true, (byte) 127),
                                            new ClientCustomPayloadPacket("MC|Brand", "vanilla".getBytes())
                                    );
                                } else if (p instanceof ServerPlayerPosLookPacket) {
                                    ServerPlayerPosLookPacket positionRotation = (ServerPlayerPosLookPacket) p;

                                    bot.setX(positionRotation.getX());
                                    bot.setY(positionRotation.getY());
                                    bot.setZ(positionRotation.getZ());

                                    bot.setPitch(positionRotation.getPitch());
                                    bot.setYaw(positionRotation.getYaw());

                                    if (bot.isAreaLoaded(bot.getFloorX(), bot.getFloorY(), bot.getFloorZ())) {
                                        bot.getSession().sendPacket(new ClientPacketTeleportConfirm(positionRotation.getTeleport()));

                                        if (!active) {
                                            active = true;

                                            bot.getSession().sendPacket(new ClientPlayerPositionPacket(bot.getX(), bot.getY(), bot.getZ(), bot.isOnGround()));
                                            bot.getPlayer().getBotManager().getBots().add(bot);

                                            ChatUtil.sendChatMessage("&7[&cBOT&7] [&c" + bot.getName() + "&7] Подключился.", bot.getPlayer(), true);
                                        }
                                    }
                                } else if (p instanceof ServerPacketChunkData) {
                                    ServerPacketChunkData packet = (ServerPacketChunkData) p;
                                    CachedChunk chunk = new CachedChunk(packet.getColumn());

                                    if (!bot.getPlayer().getBotManager().getCachedChunks().contains(chunk)) {
                                        bot.getPlayer().getBotManager().getCachedChunks().add(chunk);
                                    }

                                    if (!bot.ownChunks.contains(chunk)) {
                                        bot.ownChunks.add(bot.getPlayer().getBotManager().getCachedChunks()
                                                .get(bot.getPlayer().getBotManager().getCachedChunks().indexOf(chunk)));
                                        bot.getPlayer().getBotManager().getCachedChunks()
                                                .get(bot.getPlayer().getBotManager().getCachedChunks().indexOf(chunk)).getUsages().add(bot);
                                    }
                                } else if (p instanceof ServerPacketBlockChange) {
                                    ServerPacketBlockChange packet = (ServerPacketBlockChange) p;
                                    Position pos = packet.getRecord().getPosition();

                                    bot.setBlockAtPos(pos.getX(), pos.getY(), pos.getZ(), packet.getRecord().getBlock());
                                } else if (p instanceof ServerPacketMultiBlockChange) {
                                    ServerPacketMultiBlockChange packet = (ServerPacketMultiBlockChange) p;

                                    for (BlockChangeRecord record : packet.getRecords()) {
                                        Position pos = record.getPosition();
                                        bot.setBlockAtPos(pos.getX(), pos.getY(), pos.getZ(), record.getBlock());
                                    }
                                } else if (p instanceof ServerPacketChunkUnload) {
                                    ServerPacketChunkUnload unload = (ServerPacketChunkUnload) p;
                                    CachedChunk chunk = new CachedChunk(bot.getChunkAtPos(unload.getX(), unload.getZ()));

                                    bot.ownChunks.remove(chunk);
                                    bot.getPlayer().getBotManager().getCachedChunks()
                                            .get(bot.getPlayer().getBotManager().getCachedChunks().indexOf(chunk)).getUsages().remove(bot);
                                } else if (p instanceof ServerPacketPlayerHealth) {
                                    ServerPacketPlayerHealth packet = (ServerPacketPlayerHealth) p;

                                    if (packet.getHealth() == 0.0D) {
                                        bot.setMirror(false);
                                        bot.getSession().sendPacket(new ClientStatusPacket(0));
                                    }
                                } else if (p instanceof ServerChatPacket) {
                                    if (bot.getPlayer().getOptionsManager().getOptionByName("Авторег").asBooleanOption().isEnabled()) {
                                        String message = ((ServerChatPacket) p).getMsg().getFullText();

                                        if (!bot.isRegistered() && (message.contains("/reg") || message.contains("/lobby")
                                                || message.contains("авторизируйтесь") || message.contains("зарегистрируйтесь") || message.contains("войдите"))) {
                                            ctx.executor().schedule(() -> {
                                                bot.getSession().sendPacket(new ClientChatPacket(bot.getPlayer().getOptionsManager()
                                                        .getOptionByName("Авторег - команда регистрации").asValueOption().getValue()));
                                                bot.getSession().sendPacket(new ClientChatPacket(bot.getPlayer().getOptionsManager()
                                                        .getOptionByName("Авторег - команда авторизации").asValueOption().getValue()));
                                                bot.setRegistered(true);
                                            }, 1L, TimeUnit.SECONDS);

                                            return;
                                        }

                                        if (message.toLowerCase().contains("проверка пройдена") || message.toLowerCase().contains("прошли проверку")) {
                                            bot.captchaTries = 0;
                                        }
                                    }

                                    if (bot.getPlayer().getOptionsManager().getOptionByName("Чат").asBooleanOption().isEnabled()) {
                                        ChatUtil.sendChatMessage(new ComponentBuilder(ChatUtil.fixColor("&7[&cBOT&7] [&c" + bot.getName() + "&7] Чат: &r"))
                                                .append(((ServerChatPacket) p).getMessage()).create(), bot.getPlayer(), true);
                                    }
                                } else if (p instanceof ServerMapDataPacket) {
                                    if (bot.getPlayer().getOptionsManager().getOptionByName("Автокарта").asBooleanOption().isEnabled() &&
                                            bot.getInventory().getItems().contains(new ItemStack(358)) && bot.captchaTries != 0) {
                                        captchaSolver.execute(() -> {
                                            try {
                                                ServerMapDataPacket packet = (ServerMapDataPacket) p;
                                                BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);

                                                for (int x = 0; x < 128; x++) {
                                                    for (int y = 0; y < 128; y++) {
                                                        byte input = packet.getData().getData()[x + y * 128];
                                                        int colId = (input >>> 2) & 0b11111;
                                                        byte shader = (byte) (input & 0b11);

                                                        BasicColor col = BasicColor.colors.get(colId);
                                                        if (col == null) {
                                                            col = BasicColor.TRANSPARENT;
                                                        }

                                                        image.setRGB(x, y, col.shaded(shader));
                                                    }
                                                }

                                                File captchaFile = new File("BebraProxy" + File.separator + "cache" + File.separator +
                                                        "captcha" + File.separator + new Random(System.currentTimeMillis()).nextInt(999999999) + ".png");
                                                captchaFile.getParentFile().mkdirs();

                                                ImageIO.write(image, "PNG", captchaFile);

                                                // TODO Captcha solver
//                                                try {
//                                                    TwoCaptcha solver = new TwoCaptcha();
//                                                    Normal normal = new Normal();
//
//                                                    normal.setFile(captchaFile);
//                                                    normal.setNumeric(1);
//
//                                                    solver.solve(normal);
//
//                                                    if (bot.getSession().isChannelOpen()) {
//                                                        bot.getSession().sendPacket(new ClientChatPacket(normal.getCode()));
//                                                    }
//                                                } catch (Throwable e) {
//                                                    if (e.getMessage().contains("UNSOLVABLE")) {
//                                                        if (bot.getSession().isChannelOpen()) {
//                                                            bot.getSession().sendPacket(new ClientChatPacket("null"));
//                                                        }
//                                                    }
//                                                }

                                                bot.captchaTries--;
                                            } catch (Throwable ignored) {}
                                        });
                                    }
                                } else if (p instanceof ServerLoginSetCompressionPacket) {
                                    bot.getSession().setCompressionThreshold(((ServerLoginSetCompressionPacket) p).getThreshold());
                                } else if (p instanceof ServerLoginSuccessPacket) {
                                    bot.getSession().setConnectionState(EnumConnectionState.PLAY);
                                } else if (p instanceof ServerDisconnectPacket) {
                                    ChatUtil.sendChatMessage("&7[&cBOT&7] [&c" + bot.getName() + "&7] Отключён: &r"
                                            + ((ServerDisconnectPacket) p).getMessage().getFullText(), bot.getPlayer(), true);
                                    disconnect();
                                } else if (p instanceof ServerLoginDisconnectPacket) {
                                    ChatUtil.sendChatMessage("&7[&cBOT&7] [&c" + bot.getName() + "&7] Отключён: &r"
                                            + ((ServerLoginDisconnectPacket) p).getMessage().getFullText(), bot.getPlayer(), true);
                                    disconnect();
                                } else if (p instanceof ServerKeepAlivePacket) {
                                    bot.getSession().sendPacket(new ClientKeepAlivePacket(((ServerKeepAlivePacket) p).getKeepaliveId()));
                                } else if (p instanceof ServerOpenWindowPacket) {
                                    ServerOpenWindowPacket packet = (ServerOpenWindowPacket) p;
                                    bot.setOpenContainer(new InventoryContainer(packet.getWindowId(), new ArrayList<>(packet.getSlots()), packet.getName()));
                                } else if (p instanceof ServerWindowItemsPacket) {
                                    ServerWindowItemsPacket packet = (ServerWindowItemsPacket) p;

                                    if (packet.getWindowId() == 0) {
                                        bot.setInventory(new InventoryContainer(0, Arrays.stream(packet.getItemStacks()).collect(Collectors.toList()), "inventory"));
                                    }

                                    if (bot.getOpenContainer() != null && packet.getWindowId() == bot.getOpenContainer().getWindowID()) {
                                        bot.getOpenContainer().getItems().addAll(Arrays.stream(packet.getItemStacks()).collect(Collectors.toList()));
                                    }
                                } else if (p instanceof ServerSetSlotPacket) {
                                    ServerSetSlotPacket packet = (ServerSetSlotPacket) p;

                                    if (packet.getWindowId() == 0) {
                                        bot.getInventory().getItems().set(packet.getSlot(), packet.getItem());
                                    }

                                    if (bot.getOpenContainer() != null && bot.getOpenContainer().getItems().size() < packet.getSlot() && packet.getWindowId() == bot.getOpenContainer().getWindowID()) {
                                        bot.getOpenContainer().getItems().set(packet.getSlot(), packet.getItem());
                                    }
                                } else if (p instanceof ServerCloseWindowPacket) {
                                    ServerCloseWindowPacket packet = (ServerCloseWindowPacket) p;

                                    if (bot.getOpenContainer() != null && packet.getWindowId() == bot.getOpenContainer().getWindowID()) {
                                        bot.setOpenContainer(null);
                                    }
                                } else if (p instanceof ServerExplosionPacket) {
                                    ServerExplosionPacket packet = (ServerExplosionPacket) p;
                                    bot.setMotionX(bot.getMotionX() + packet.getMotionX());
                                    bot.setMotionY(bot.getMotionY() + packet.getMotionY());
                                    bot.setMotionZ(bot.getMotionZ() + packet.getMotionZ());
                                } else if (p instanceof ServerEntityVelocityPacket) {
                                    ServerEntityVelocityPacket packet = (ServerEntityVelocityPacket) p;
                                    if (packet.getEntityID() == bot.getEntityID()) {
                                        bot.setMotionX(packet.getMotionX() / 8000D);
                                        bot.setMotionY(packet.getMotionY() / 8000D);
                                        bot.setMotionZ(packet.getMotionZ() / 8000D);
                                    }
                                }
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) {
                                if (bot.getPlayer().getBotManager().getBots().contains(bot)) {
                                    ChatUtil.sendChatMessage("&7[&cBOT&7] [&c" + bot.getName() + "&7] Отключён: &cСервер закрыл соединение", bot.getPlayer(), true);
                                    disconnect();
                                }
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                if (bot.getPlayer().getBotManager().getBots().contains(bot)) {
                                    ChatUtil.sendChatMessage("&7[&cBOT&7] [&c" + bot.getName() + "&7] Отключён: &c" + cause.getMessage().replace(" ", " &c"), bot.getPlayer(), true);
                                    disconnect();
                                }

                                ctx.close();
                            }
                        });
                    }
                });

        bootstrap.connect(ip, port);
    }

    private void disconnect() {
        bot.getPlayer().getBotManager().getBots().remove(bot);
    }
}