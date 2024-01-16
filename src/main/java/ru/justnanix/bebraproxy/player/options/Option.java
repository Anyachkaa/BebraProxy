package ru.justnanix.bebraproxy.player.options;

import lombok.Getter;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.player.options.impl.BooleanOption;
import ru.justnanix.bebraproxy.player.options.impl.SwitchOption;
import ru.justnanix.bebraproxy.player.options.impl.ValueOption;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

@Getter
public class Option {
    protected final ProxiedPlayer player;
    protected final String name;
    protected final String description;
    protected final Category category;

    public Option(ProxiedPlayer player, String name, String description, Category category) {
        this.player = player;
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public BooleanOption asBooleanOption() {
        return (BooleanOption) this;
    }

    public SwitchOption asSwitchOption() {
        return (SwitchOption) this;
    }

    public ValueOption asValueOption() {
        return (ValueOption) this;
    }

    public void onEnable() {
        ChatUtil.sendChatMessage("&c>> &7Была &aвключена &7настройка &c" + name, player, false);
    }

    public void onDisable() {
        ChatUtil.sendChatMessage("&c>> &7Была &cотключена &7настройка &c" + name, player, false);
    }

    public enum Category {
        PROXY,
        BOTS
    }
}