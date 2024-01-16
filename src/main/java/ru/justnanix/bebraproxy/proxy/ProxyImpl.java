package ru.justnanix.bebraproxy.proxy;

import lombok.Data;

import java.net.InetSocketAddress;

@Data
public class ProxyImpl {
    private final ProxyType type;
    private final InetSocketAddress address;
    private final String username, password;
}
