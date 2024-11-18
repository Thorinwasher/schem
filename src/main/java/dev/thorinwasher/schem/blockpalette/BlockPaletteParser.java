package dev.thorinwasher.schem.blockpalette;

import org.bukkit.block.data.BlockData;

public interface BlockPaletteParser {
    BlockData parse(String key);
}
