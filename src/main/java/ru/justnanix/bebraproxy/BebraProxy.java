package ru.justnanix.bebraproxy;

import lombok.Getter;
import ru.justnanix.bebraproxy.network.ProxyServer;
import ru.justnanix.bebraproxy.network.packet.PacketRegistry;
import ru.justnanix.bebraproxy.player.plan.PlanManager;
import ru.justnanix.bebraproxy.proxy.ProxyManager;
import ru.justnanix.bebraproxy.utils.proxy.FileUtil;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
public class BebraProxy {
    public static final ScheduledExecutorService simpleTasks = Executors.newScheduledThreadPool(8);

    @Getter
    private static BebraProxy instance;

    private final PlanManager planManager = new PlanManager();
    private final File dirFolder = new File("BebraProxy");

    private ProxyServer server;

    public BebraProxy() {
        instance = this;
    }

    public static void main(String[] args) {
        new BebraProxy().run();
    }

    public void run() {
        System.out.println("[ ------------------------------------- ]");
        System.out.println("|           Запуск BebraProxy           |");
        System.out.println("[ ------------------------------------- ]");
        System.out.println();

        System.out.println("> Запуск систем...");
        FileUtil.createMissing();
        FileUtil.loadAccounts();
        PacketRegistry.init();
        // ProxyManager.init();
        planManager.init();
        System.out.println("> Загружено " + ProxyManager.globalProxies.size() + " прокси для ботов!");
        System.out.println("> Загружено " + ProxyManager.joinProxies.size() + " игровых прокси!");
        System.out.println("> Загружено " + planManager.getAccounts().size() + " аккаунтов с подпиской!");
        System.out.println("> Запуск прокси...");
        server = new ProxyServer(dirFolder.toPath().resolve("icon.png").toString());
        server.bind();
        System.out.println("> Успешный запуск.\n");
    }
}