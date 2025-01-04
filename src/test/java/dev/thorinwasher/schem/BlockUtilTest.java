package dev.thorinwasher.schem;


import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.joml.AxisAngle4d;
import org.joml.Matrix3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkitExtension;
import org.mockbukkit.mockbukkit.block.data.BlockDataMock;
import org.mockbukkit.mockbukkit.block.data.StairsDataMock;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockBukkitExtension.class)
class BlockUtilTest {

    @Test
    void transformBlockData() {
        BlockDataMock blockDataMock = BlockDataMock.mock(Material.OAK_STAIRS);
        assertEquals(blockDataMock, BlockUtil.transformBlockData(blockDataMock, new Matrix3d()));
    }

    @Test
    void transformBlockData_west() {
        BlockDataMock blockDataMock = BlockDataMock.mock(Material.OAK_STAIRS);
        StairsDataMock stairsDataMock = (StairsDataMock) BlockUtil.transformBlockData(blockDataMock, new AxisAngle4d(Math.PI / 2, 0, 1, 0).get(new Matrix3d()));
        assertEquals(BlockFace.WEST, stairsDataMock.getFacing());
    }

    @Test
    void transformBlockData_south() {
        BlockDataMock blockDataMock = BlockDataMock.mock(Material.OAK_STAIRS);
        StairsDataMock stairsDataMock = (StairsDataMock) BlockUtil.transformBlockData(blockDataMock, new AxisAngle4d(Math.PI, 0, 1, 0).get(new Matrix3d()));
        assertEquals(BlockFace.SOUTH, stairsDataMock.getFacing());
    }

    @Test
    void transformBlockData_east() {
        BlockDataMock blockDataMock = BlockDataMock.mock(Material.OAK_STAIRS);
        StairsDataMock stairsDataMock = (StairsDataMock) BlockUtil.transformBlockData(blockDataMock, new AxisAngle4d(3 * Math.PI / 2, 0, 1, 0).get(new Matrix3d()));
        assertEquals(BlockFace.EAST, stairsDataMock.getFacing());
    }
}