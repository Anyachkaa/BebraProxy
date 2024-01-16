package ru.justnanix.bebraproxy.bots.chunks;

import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.justnanix.bebraproxy.bots.Bot;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@RequiredArgsConstructor
public class CachedChunk {
    private final List<Bot> usages = new CopyOnWriteArrayList<>();
    private final Column chunk;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CachedChunk that = (CachedChunk) o;
        return Objects.equals(chunk, that.chunk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunk);
    }
}
