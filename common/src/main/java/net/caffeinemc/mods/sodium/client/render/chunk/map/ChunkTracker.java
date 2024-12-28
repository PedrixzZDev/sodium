package net.caffeinemc.mods.sodium.client.render.chunk.map;

import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.level.ChunkPos;

import java.util.Set;

public class ChunkTracker implements ClientChunkEventListener {
    private final Long2IntOpenHashMap chunkStatus = new Long2IntOpenHashMap();
    private final LongOpenHashSet chunkReady = new LongOpenHashSet();

    private final LongSet unloadQueue = new LongOpenHashSet();
    private final LongSet loadQueue = new LongOpenHashSet();

    // Optimization: Cache neighbors for faster checks in `updateMerged`
    private final long[] neighborKeys = new long[9];
    private final int[] neighborFlags = new int[9];
    private final int[] neighborOffsets = {-1, 0, 1};


    public ChunkTracker() {

    }

    @Override
    public void updateMapCenter(int chunkX, int chunkZ) {

    }

    @Override
    public void updateLoadDistance(int loadDistance) {

    }

    @Override
    public void onChunkStatusAdded(int x, int z, int flags) {
        long key = ChunkPos.asLong(x, z);
        int prev = chunkStatus.get(key);
        int cur = prev | flags;

        if (prev == cur) {
            return;
        }

        chunkStatus.put(key, cur);
        updateNeighbors(x, z);
    }

    @Override
    public void onChunkStatusRemoved(int x, int z, int flags) {
        long key = ChunkPos.asLong(x, z);
        int prev = chunkStatus.get(key);
        int cur = prev & ~flags;

        if (prev == cur) {
            return;
        }

        if (cur == chunkStatus.defaultReturnValue()) {
            chunkStatus.remove(key);
        } else {
            chunkStatus.put(key, cur);
        }

       updateNeighbors(x, z);
    }


    private void updateNeighbors(int x, int z) {
        // Loop through the 3x3 neighbor grid
        for (int ox : neighborOffsets) {
            for (int oz : neighborOffsets) {
               updateMerged(x + ox, z + oz);
            }
        }
    }

    private void updateMerged(int x, int z) {
        long key = ChunkPos.asLong(x, z);
        int index = 0;
        int flags = ChunkStatus.FLAG_ALL;

        // Cache neighbors for lookups
        for (int ox : neighborOffsets) {
            for (int oz : neighborOffsets) {
                long neighborKey = ChunkPos.asLong(x + ox, z + oz);
                neighborKeys[index] = neighborKey;
                neighborFlags[index] = this.chunkStatus.get(neighborKey);
                index++;
                flags &= neighborFlags[index-1];
            }
        }

        if (flags == ChunkStatus.FLAG_ALL) {
            if (this.chunkReady.add(key) && !this.unloadQueue.remove(key)) {
                 this.loadQueue.add(key);
            }
        } else {
            if (this.chunkReady.remove(key) && !this.loadQueue.remove(key)) {
               this.unloadQueue.add(key);
            }
        }
    }


    public LongCollection getReadyChunks() {
        return LongSets.unmodifiable(this.chunkReady);
    }

    public void forEachEvent(ChunkEventHandler loadEventHandler, ChunkEventHandler unloadEventHandler) {
        forEachChunk(this.unloadQueue, unloadEventHandler);
        this.unloadQueue.clear();

        forEachChunk(this.loadQueue, loadEventHandler);
        this.loadQueue.clear();
    }

    public static void forEachChunk(LongCollection queue, ChunkEventHandler handler) {
        var iterator = queue.iterator();
        while (iterator.hasNext()) {
            var pos = iterator.nextLong();

            var x = ChunkPos.getX(pos);
            var z = ChunkPos.getZ(pos);

            handler.apply(x, z);
        }
    }

    public interface ChunkEventHandler {
        void apply(int x, int z);
    }
}
