package dev.thorinwasher.schem.blockpalette;

import net.minestom.server.instance.block.Block;

public interface BlockPaletteParser {
    Block parse(String key);
}
