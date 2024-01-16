package ru.justnanix.bebraproxy.bots.network;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class BotConnectionManager {
    private static final EventLoopGroup globalBotGroup = Epoll.isAvailable() ?
            new EpollEventLoopGroup(4) :
            new NioEventLoopGroup(4);

    private Channel channel;
}
