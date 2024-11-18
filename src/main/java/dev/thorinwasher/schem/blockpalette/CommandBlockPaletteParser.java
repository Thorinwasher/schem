package dev.thorinwasher.schem.blockpalette;

import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;

public class CommandBlockPaletteParser implements BlockPaletteParser {
    @Override
    public BlockData parse(String serializedBlockData) {
        return Bukkit.createBlockData(serializedBlockData);
    }
}
