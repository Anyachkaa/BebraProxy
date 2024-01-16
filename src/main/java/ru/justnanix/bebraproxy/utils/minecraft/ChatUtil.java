package ru.justnanix.bebraproxy.utils.minecraft;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerChatPacket;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;

import java.util.stream.IntStream;

public class ChatUtil {
    public static String fixColor(String text) {
        return text.replace('&', '§')
                .replace(">>", "»")
                .replace("<<", "«")
                .replace("(o)", "●")
                .replace("(*)", "•");
    }

    public static void sendHoverMessage(ProxiedPlayer player, String s1, String s2) {
        player.getConnectMgr().sendPacket(new ServerChatPacket(new TextComponent(fixColor(s1))
                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(fixColor(s2))))));
    }

    public static void sendChatMessage(String message, ProxiedPlayer player, boolean prefix) {
        player.getConnectMgr().sendPacket(new ServerChatPacket(
                new ComponentBuilder(fixColor(prefix ? "&cBebra&rProxy &c>> " : ""))
                        .append(parseComponent("&7" + message))
                        .create()));
    }

    public static void sendChatMessage(BaseComponent[] text, ProxiedPlayer player, boolean prefix) {
        player.getConnectMgr().sendPacket(new ServerChatPacket(new ComponentBuilder(fixColor(
                (prefix ?  "&cBebra&rProxy &c>> " : "") + "&7")).append(text).create()));
    }

    public static void sendBroadcastMessage(String message, boolean prefix) {
        BebraProxy.getInstance().getServer().getPlayers().forEach(p -> sendChatMessage(message, p, prefix));
    }

    public static void clearChat(int x, ProxiedPlayer player) {
        IntStream.range(0, x).forEach(i -> sendChatMessage(" ", player, false));
    }

    public static BaseComponent parseComponent(String message) {
        BaseComponent component = null;

        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);

            if (c == '&' && i+1 < message.length() && ChatColor.getByChar(message.charAt(i+1)) != null) {
                ComponentBuilder builder = new ComponentBuilder(
                        message.substring(i+2, message.substring(i+2).contains("&") ? message.indexOf("&", i+2) : message.length())
                ).color(ChatColor.getByChar(message.charAt(i+1)));

                if (component == null)
                    component = builder.create()[0];
                else component.addExtra(builder.create()[0]);
            } else if (c == '&') {
                if (component == null)
                    component = new ComponentBuilder().append(message.substring(i)).create()[0];
                else component.addExtra(message.substring(i));
            }
        }

        return component == null ? new ComponentBuilder(message).create()[0] : component;
    }
    
    public static String formatSymbols(String message) {
        return message.replace("§", "")
                .replace("&", "")
                .replaceAll("(?i)jndi:", "");
    }
}