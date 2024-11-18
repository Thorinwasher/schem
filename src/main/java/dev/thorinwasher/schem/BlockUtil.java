package dev.thorinwasher.schem;

import org.bukkit.Axis;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.Orientable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3d;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class BlockUtil {
    private static final Map<Vector3d, BlockFace> DIRECTION_TO_BLOCK_FACE_MAP = compileFaces();
    private static final Map<Vector3d, Axis> DIRECTION_TO_AXIS_MAP = compileAxis();

    private BlockUtil() {
    }

    public static BlockData transformBlockData(BlockData blockData, Matrix3d transformation) {
        BlockData copiedData = blockData.clone();
        if (copiedData instanceof Directional directional) {
            BlockFace newFacing = transformBlockFace(directional.getFacing(), transformation);
            if (directional.getFaces().contains(newFacing)) {
                directional.setFacing(newFacing);
            }
        }
        if (copiedData instanceof MultipleFacing multipleFacing) {
            Set<BlockFace> newFaces = new HashSet<>();
            for (BlockFace face : multipleFacing.getFaces()) {
                newFaces.add(transformBlockFace(face, transformation));
            }
            for (BlockFace allowedFace : multipleFacing.getAllowedFaces()) {
                multipleFacing.setFace(allowedFace, newFaces.contains(allowedFace));
            }
        }
        if (copiedData instanceof Orientable orientable) {
            Axis newAxis = transformAxis(orientable.getAxis(), transformation);
            if (orientable.getAxes().contains(newAxis)) {
                orientable.setAxis(newAxis);
            }
        }
        return copiedData;
    }

    private static BlockFace transformBlockFace(BlockFace blockFace, Matrix3d transformation) {
        Vector direction = blockFace.getDirection();
        return transform(new Vector3d(direction.getX(), direction.getY(), direction.getZ()), transformation, DIRECTION_TO_BLOCK_FACE_MAP);
    }

    private static Axis transformAxis(Axis axis, Matrix3d transformation) {
        if (axis == Axis.X) {
            return transform(new Vector3d(1, 0, 0), transformation, DIRECTION_TO_AXIS_MAP);
        }
        if (axis == Axis.Y) {
            return transform(new Vector3d(0, 1, 0), transformation, DIRECTION_TO_AXIS_MAP);
        }
        return transform(new Vector3d(0, 0, 1), transformation, DIRECTION_TO_AXIS_MAP);
    }

    private static <T> T transform(Vector3d vector, Matrix3d transformation, Map<Vector3d, T> possibleOutcomes) {
        Vector3d transformed = transformation.transform(vector).normalize();
        @Nullable T output = possibleOutcomes.get(transformed);
        if (output != null) {
            return output;
        }
        double closest = Double.MAX_VALUE;
        for (Map.Entry<Vector3d, T> entry : possibleOutcomes.entrySet()) {
            double distance = entry.getKey().distance(transformed);
            if (distance < closest) {
                closest = distance;
                output = entry.getValue();
            }
        }
        return output;
    }

    private static Map<Vector3d, BlockFace> compileFaces() {
        Map<Vector3d, BlockFace> output = new HashMap<>();
        for (BlockFace blockFace : BlockFace.values()) {
            Vector direction = blockFace.getDirection();
            output.put(new Vector3d(direction.getX(), direction.getY(), direction.getZ()).normalize(), blockFace);
        }
        return output;
    }

    private static Map<Vector3d, Axis> compileAxis() {
        Map<Vector3d, Axis> output = new HashMap<>();
        output.put(new Vector3d(1, 0, 0), Axis.X);
        output.put(new Vector3d(-1, 0, 0), Axis.X);
        output.put(new Vector3d(0, 1, 0), Axis.Y);
        output.put(new Vector3d(0, -1, 0), Axis.Y);
        output.put(new Vector3d(0, 0, 1), Axis.Z);
        output.put(new Vector3d(0, 0, -1), Axis.Z);
        return output;
    }

}
