package ru.justnanix.bebraproxy.player.options.impl;

import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.player.options.Option;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

public class BooleanOption extends Option {
    private boolean enabled;

    public BooleanOption(Option.Category category, ProxiedPlayer player, boolean enabled, String name, String description) {
        super(player, name, description, category);
        this.enabled = enabled;
    }

    public void toggle() {
        enabled = !enabled;

        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void setEnabled(boolean enabled) {
        // TODO: Recode that shit (задержки)

        if (player.isAuthorized()) {
            if (this.enabled != enabled) {
                toggle();
            } else {
                ChatUtil.sendChatMessage("&c>> &7Опция &c" + name + " &7уже " + (enabled ? "&a" : "&c") + (enabled ? "включена" : "выключена"), player, false);
            }
        } else {
            this.enabled = enabled;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}