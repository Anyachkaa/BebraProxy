package ru.justnanix.bebraproxy.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProxyManager {
    private static File proxyFile = new File("BebraProxy" + File.separator + "proxies.txt");
    private static File joinProxyFile = new File("BebraProxy" + File.separator + "joinproxies.txt");

    public static final List<ProxyImpl> globalProxies = new CopyOnWriteArrayList<>();
    public static final List<ProxyImpl> joinProxies = new CopyOnWriteArrayList<>();

    private final Random random = new Random(System.currentTimeMillis());
    private final List<ProxyImpl> proxies = new CopyOnWriteArrayList<>(globalProxies);
    private int number = 0;

    public static void init() {
        // TODO: Recode that shit
        try (BufferedReader reader = new BufferedReader(new FileReader(proxyFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String ip = line.split("@")[1];

                String host = ip.split(":")[0];
                int port = Integer.parseInt(ip.split(":")[1]);
                String username = line.split(":")[0];
                String password = line.split("@")[0].split(":")[1];

                globalProxies.add(new ProxyImpl(ProxyType.SOCKS5, new InetSocketAddress(host, port), username, password));
            }
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(joinProxyFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String ip = line.split("@")[1];

                String host = ip.split(":")[0];
                int port = Integer.parseInt(ip.split(":")[1]);
                String username = line.split(":")[0];
                String password = line.split("@")[0].split(":")[1];

                joinProxies.add(new ProxyImpl(ProxyType.SOCKS5, new InetSocketAddress(host, port), username, password));
            }
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public ProxyImpl getProxy() {
        ++number;

        if (number >= proxies.size())
            number = 0;

        return proxies.get(number);
    }

    // TODO: Recode that shit
    public ProxyImpl getJoinProxy() {
        return joinProxies.get(random.nextInt(joinProxies.size()));
    }
}
