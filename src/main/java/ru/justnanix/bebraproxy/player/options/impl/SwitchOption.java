package ru.justnanix.bebraproxy.player.options.impl;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.player.options.Option;

public class SwitchOption extends Option {
    @Getter @Setter
    private String currentVal;
    private final String[] values;
    private int index;

    public SwitchOption(Option.Category category, ProxiedPlayer player, String currentVal, String[] values, String name, String description) {
        super(player, name, description, category);
        this.values = values;
        this.currentVal = currentVal;
        this.index = ArrayUtils.indexOf(values, currentVal);
    }

    public void switchOpt() {
        // TODO: Recode that shit (задержки)
        index++;

        if (index >= values.length) {
            index = 0;
        }

        currentVal = values[index];
    }
}
