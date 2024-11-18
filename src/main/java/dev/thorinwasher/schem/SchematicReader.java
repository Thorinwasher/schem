package dev.thorinwasher.schem;


import com.google.common.base.Preconditions;
import dev.thorinwasher.schem.blockpalette.BlockPaletteParser;
import dev.thorinwasher.schem.blockpalette.CommandBlockPaletteParser;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.IntBinaryTag;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

/**
 * Simple schematic file reader.
 */
public final class SchematicReader {
    private static final BinaryTagIO.Reader NBT_READER = BinaryTagIO.unlimitedReader();

    private BlockPaletteParser paletteParser = new CommandBlockPaletteParser();

    public SchematicReader withBlockPaletteParser(BlockPaletteParser parser) {
        this.paletteParser = parser;
        return this;
    }

    public @NotNull Schematic read(@NotNull InputStream stream) {
        try {
            return read(NBT_READER.readNamed(stream, BinaryTagIO.Compression.GZIP));
        } catch (Exception e) {
            throw new SchematicReadException("failed to read schematic NBT", e);
        }
    }

    public @NotNull Schematic read(@NotNull Path path) {
        try {
            return read(NBT_READER.readNamed(path, BinaryTagIO.Compression.GZIP));
        } catch (Exception e) {
            throw new SchematicReadException("failed to read schematic NBT", e);
        }
    }

    public @NotNull Schematic read(@NotNull Map.Entry<String, CompoundBinaryTag> namedTag) {
        try {
            // If it has a Schematic tag is sponge v2 or 3
            var schematicTag = namedTag.getValue().get("Schematic");
            if (schematicTag instanceof CompoundBinaryTag schematicCompound) {
                return read(schematicCompound, schematicCompound.getInt("Version"));
            }

            // Otherwise it is hopefully v1
            return read(namedTag.getValue(), 1);
        } catch (Exception e) {
            throw new SchematicReadException("Invalid schematic file", e);
        }
    }

    private @NotNull Schematic read(@NotNull CompoundBinaryTag tag, int version) {
        short width = tag.getShort("Width");
        short height = tag.getShort("Height");
        short length = tag.getShort("Length");

        CompoundBinaryTag metadata = tag.getCompound("Metadata");

        Vector3i offset = new Vector3i();
        if (metadata.keySet().contains("WEOffsetX")) {
            int offsetX = metadata.getInt("WEOffsetX");
            int offsetY = metadata.getInt("WEOffsetY");
            int offsetZ = metadata.getInt("WEOffsetZ");

            offset.set(offsetX, offsetY, offsetZ);
        } //todo handle sponge Offset

        CompoundBinaryTag palette;
        byte[] blockArray;
        Integer paletteSize;
        if (version == 3) {
            var blockEntries = tag.getCompound("Blocks");
            Preconditions.checkNotNull(blockEntries, "Missing required field 'Blocks'");

            palette = blockEntries.getCompound("Palette");
            Preconditions.checkNotNull(palette, "Missing required field 'Blocks.Palette'");
            blockArray = blockEntries.getByteArray("Data");
            Preconditions.checkNotNull(blockArray, "Missing required field 'Blocks.Data'");
            paletteSize = palette.size();
        } else {
            palette = tag.getCompound("Palette");
            Preconditions.checkNotNull(palette, "Missing required field 'Palette'");
            blockArray = tag.getByteArray("BlockData");
            Preconditions.checkNotNull(blockArray, "Missing required field 'BlockData'");
            paletteSize = tag.getInt("PaletteMax");
            Preconditions.checkNotNull(paletteSize, "Missing required field 'PaletteMax'");
        }

        BlockData[] paletteBlocks = new BlockData[paletteSize];

        palette.forEach((entry) -> {
            try {
                int assigned = ((IntBinaryTag) entry.getValue()).value();
                BlockData block = paletteParser.parse(entry.getKey());
                paletteBlocks[assigned] = block;
            } catch (Exception e) {
                throw new SchematicReadException("Failed to parse block state: " + entry.getKey(), e);
            }
        });

        return new Schematic(
                new Vector3i(width, height, length),
                offset,
                paletteBlocks,
                blockArray
        );
    }

}
