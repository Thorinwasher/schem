package dev.thorinwasher.schem;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Represents a schematic file which can be manipulated in the world.
 */
public record Schematic(
        Vector3i size,
        Vector3i offset,
        BlockData[] palette,
        byte[] blocks
) {
    private static final System.Logger logger = System.getLogger(Schematic.class.getName());

    public Schematic {
        palette = Arrays.copyOf(palette, palette.length);
        blocks = Arrays.copyOf(blocks, blocks.length);
    }

    @Override
    public BlockData @NotNull [] palette() {
        return Arrays.copyOf(palette, palette.length);
    }

    @Override
    public byte @NotNull [] blocks() {
        return Arrays.copyOf(blocks, blocks.length);
    }

    public @NotNull Vector3i size(@NotNull Matrix3d transformation) {
        return transform(size, transformation);
    }

    public @NotNull Vector3i offset(@NotNull Matrix3d transformation) {
        return transform(offset, transformation);
    }

    private Vector3i transform(Vector3i vector3i, Matrix3d transformation) {
        Vector3d transformed = transformation.transform(new Vector3d(vector3i)).ceil();
        return new Vector3i((int) transformed.x, (int) transformed.y, (int) transformed.z);
    }

    /**
     * Apply the schematic directly given a rotation. The applicator function will be called for each block in the schematic.
     *
     * @param transformation The transformation to apply before placement.
     * @param applicator     The function to call for each block in the schematic.
     */
    public void apply(@NotNull Matrix3d transformation, @NotNull BiConsumer<Vector3i, BlockData> applicator) {
        var blocks = ByteBuffer.wrap(this.blocks);
        for (int y = 0; y < size().y(); y++) {
            for (int z = 0; z < size().z(); z++) {
                for (int x = 0; x < size().x(); x++) {
                    BlockData block = palette[ReadUtils.readVarInt(blocks)];
                    if (block == null) {
                        logger.log(System.Logger.Level.WARNING, "Missing palette entry at {0}, {1}, {2}", x, y, z);
                        block = Material.AIR.createBlockData();
                    }

                    applicator.accept(
                            transform(new Vector3i(x, y, z).add(offset), transformation),
                            BlockUtil.transformBlockData(block, transformation));
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schematic schematic = (Schematic) o;
        return size.equals(schematic.size) &&
                offset.equals(schematic.offset) &&
                Arrays.equals(palette, schematic.palette) &&
                Arrays.equals(blocks, schematic.blocks);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(size, offset);
        result = 31 * result + Arrays.hashCode(palette);
        result = 31 * result + Arrays.hashCode(blocks);
        return result;
    }

    @Override
    public String toString() {
        return String.format(
                "Schematic[size=%s, offset=%s, palette=%s, blocks=%s]",
                size, offset, Arrays.toString(palette), Arrays.toString(blocks)
        );
    }
}
