package ru.justnanix.bebraproxy.utils.proxy;

public class ThreadUtils {
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Throwable ignored) {}
    }
}
