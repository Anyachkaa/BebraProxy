package ru.justnanix.bebraproxy.bots;

import lombok.Getter;
import ru.justnanix.bebraproxy.bots.chunks.CachedChunk;
import ru.justnanix.bebraproxy.bots.utils.Timer;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
public class BotManager {
    private final Set<Bot> bots = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final ScheduledExecutorService botTaskScheduler = Executors.newSingleThreadScheduledExecutor();
    private final List<CachedChunk> cachedChunks = new CopyOnWriteArrayList<>();
    private final Timer timer = new Timer(20.0F);
}
