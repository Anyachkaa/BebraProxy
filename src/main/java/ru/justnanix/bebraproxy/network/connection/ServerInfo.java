package ru.justnanix.bebraproxy.network.connection;

import lombok.Data;

@Data
public class ServerInfo {
    private final String host;
    private final int port;
}
