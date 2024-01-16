package ru.justnanix.bebraproxy.player;

import lombok.Data;
import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.bots.BotManager;
import ru.justnanix.bebraproxy.commands.CommandManager;
import ru.justnanix.bebraproxy.network.data.playerlist.PlayerListEntry;
import ru.justnanix.bebraproxy.network.connection.ConnectionManagerProxy;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.network.connection.ConnectionManagerRemote;
import ru.justnanix.bebraproxy.network.connection.handler.lobby.ui.Gui;
import ru.justnanix.bebraproxy.player.options.OptionsManager;
import ru.justnanix.bebraproxy.player.options.impl.ValueOption;
import ru.justnanix.bebraproxy.player.plan.PlanAccount;
import ru.justnanix.bebraproxy.network.data.Dimension;
import ru.justnanix.bebraproxy.network.data.GameProfile;
import ru.justnanix.bebraproxy.network.data.Position;
import ru.justnanix.bebraproxy.proxy.ProxyManager;
import ru.justnanix.bebraproxy.utils.minecraft.SkinUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ProxiedPlayer {
    private final Set<PlayerListEntry> tabList = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final OptionsManager optionsManager = new OptionsManager(this);
    private final CommandManager commandManager = new CommandManager(this);
    private final BotManager botManager = new BotManager();
    private final ProxyManager proxyManager = new ProxyManager();

    private final GameProfile gameProfile;
    private final PlanAccount account;

    private ConnectionManagerProxy connectMgr;
    private ConnectionManagerRemote remoteConnectMgr;
    private ConnectionInfo connectionInfo = ConnectionInfo.AUTH;

    private Gui currentGui;
    private ValueOption currentValueOption;
    private Dimension dimension;
    private Position position, prevPosition;
    private int entityId;
    private int currentSlot = 0;

    private boolean freeCam = false;

    public void init() {
        SkinUtil.setSkin(this);
        commandManager.init();
        optionsManager.loadOptions();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProxiedPlayer that = (ProxiedPlayer) o;
        return Objects.equals(account, that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account);
    }

    public boolean isConnectedToRemote() {
        return connectionInfo == ConnectionInfo.REMOTE;
    }

    public boolean isConnectedToProxy() {
        return connectMgr.isChannelOpen() && BebraProxy.getInstance().getServer().getPlayers().contains(this);
    }

    public boolean isAuthorized() {
        return connectionInfo != ConnectionInfo.AUTH;
    }
}
