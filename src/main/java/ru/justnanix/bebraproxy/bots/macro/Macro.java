package ru.justnanix.bebraproxy.bots.macro;

import lombok.Data;

import java.util.List;

@Data
public class Macro {
    private final String name;
    private final List<MacroRecord> records;
}