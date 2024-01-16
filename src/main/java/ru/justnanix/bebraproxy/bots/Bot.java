package ru.justnanix.bebraproxy.bots;

import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import lombok.Getter;
import lombok.Setter;
import ru.justnanix.bebraproxy.bots.chunks.CachedChunk;
import ru.justnanix.bebraproxy.bots.inventory.InventoryContainer;
import ru.justnanix.bebraproxy.bots.macro.MacroRecord;
import ru.justnanix.bebraproxy.bots.mirror.MirrorRecord;
import ru.justnanix.bebraproxy.bots.network.protocol.data.Session;
import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.commands.impl.bot.move.CommandMacro;
import ru.justnanix.bebraproxy.commands.impl.bot.move.CommandMirror;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.data.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter @Setter
public class Bot {
    public final List<CachedChunk> ownChunks = new ArrayList<>();

    private Session session;
    private final String name;
    private final ProxiedPlayer player;

    private double x, y, z, lastX, lastY, lastZ;
    private float yaw = 0, pitch = 0, lastYaw, lastPitch;
    private double motionX = 0, motionY = 0, motionZ = 0;
    private boolean onGround = false;

    private int mirrorIndex = 0;
    private boolean mirror = false;
    private int macroIndex = 0;
    private boolean macro = false;
    private boolean macroComplete = false;

    private boolean registered = false;
    public int captchaTries = 3;

    private int entityID;

    private InventoryContainer openContainer;
    private InventoryContainer inventory = new InventoryContainer(0, new ArrayList<>(Collections.nCopies(46, null)), "inventory");
    
    private CommandMacro commandMacro;
    private CommandMirror commandMirror;

    public Bot(ProxiedPlayer player, String name) {
        this.name = name;
        this.player = player;
        this.commandMacro = player.getCommandManager().getCommandByClass(CommandMacro.class);
        this.commandMirror = player.getCommandManager().getCommandByClass(CommandMirror.class);
    }

    public void onUpdate() {
        try {
            if (!mirror && !macro) {
                mirrorIndex = 0;
                macroIndex = 0;
                macroComplete = false;
            }

            if (!mirror && !macro && this.isAreaLoaded(getFloorX(), getFloorY(), getFloorZ())) {
                x += motionX;
                y += motionY;
                z += motionZ;

                this.fall();

                motionY *= 0.98D;
            } else if (mirror && commandMirror.getRecords().size() > mirrorIndex) {
                MirrorRecord record = commandMirror.getRecords().get(mirrorIndex);
                Position pos = record.getRecordPosition();

                x = pos.getX();
                y = pos.getY();
                z = pos.getZ();
                yaw = pos.getYaw();
                pitch = pos.getPitch();

                for (Packet packet : record.getRecordPackets()) {
                    this.getSession().sendPacket(packet);
                }

                mirrorIndex++;
            } else if (macro && commandMacro.getCurrentMacro().getRecords().size() > macroIndex) {
                MacroRecord record = commandMacro.getCurrentMacro().getRecords().get(macroIndex);

                x += record.getPosChange().getXChange();
                y += record.getPosChange().getYChange();
                z += record.getPosChange().getZChange();

                for (Packet packet : record.getPackets()) {
                    this.getSession().sendPacket(packet);
                }

                macroIndex++;
            } else if (macro) {
                this.macroComplete = true;
            }
        } catch (Throwable ignored) {}
    }

    public void fall() {
        if (y < 256 || y > 0) {
            BlockState state = this.getBlockAtPos(getFloorX(), (int) Math.floor(getY() - 0.0001D), getFloorZ());

            if (state != null && state.getId() != 0 && !isFlower(state)) {
                if (getY() - getFloorY() > 0.5D) {
                    y = getFloorY() + 1;
                }

                motionY = 0D;
                onGround = true;

                return;
            }
        }

        motionY -= 0.08D;
        onGround = false;
    }

    public void jump() {
        if (onGround) {
            motionY += 0.42F;
        }
    }

    public boolean isFlower(BlockState state) {
        return state.getId() == 31 || state.getId() == 38 || state.getId() == 37 || state.getId() == 175 || state.getId() == 6;
    }

    public boolean isAreaLoaded(int x, int y, int z) {
        if (y > 256 || y < 0) {
            return true;
        } else {
            Column chunk;
            if ((chunk = getChunkAtPos(x >> 4, z >> 4)) != null) {
                return chunk.getChunks().length >= (y >> 4);
            } else {
                return false;
            }
        }
    }

    public BlockState getBlockAtPos(int x, int y, int z) {
        Column current = this.getChunkAtPos(x >> 4, z >> 4);

        if (current != null && current.getChunks() != null && current.getChunks().length > (y >> 4) && current.getChunks()[y >> 4] != null && current.getChunks()[y >> 4].getBlocks() != null) {
            return current.getChunks()[y >> 4].getBlocks().get(x & 15, y & 15, z & 15);
        } else {
            return null;
        }
    }

    public void setBlockAtPos(int x, int y, int z, BlockState state) {
        Chunk current = this.getChunkAtPos(x >> 4, z >> 4).getChunks()[y >> 4];
        current.getBlocks().set(x & 15, y & 15, z & 15, state);
    }

    public Column getChunkAtPos(int x, int z) {
        for (CachedChunk column : ownChunks) {
            if (column.getChunk().getX() == x && column.getChunk().getZ() == z) {
                return column.getChunk();
            }
        }

        return null;
    }

    public int getFloorX() {
        return (int) Math.floor(x);
    }

    public int getFloorY() {
        return (int) Math.floor(y);
    }

    public int getFloorZ() {
        return (int) Math.floor(z);
    }
}
