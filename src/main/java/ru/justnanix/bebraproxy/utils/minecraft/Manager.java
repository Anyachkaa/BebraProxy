package ru.justnanix.bebraproxy.utils.minecraft;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Manager<T> {
    public final List<T> elements = new CopyOnWriteArrayList<>();
}