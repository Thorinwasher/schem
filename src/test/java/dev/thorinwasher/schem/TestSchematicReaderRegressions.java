package dev.thorinwasher.schem;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestSchematicReaderRegressions {

    @Test
    public void testReadFail1_20_1() {
        Schematic schem = assertReadSchematic("/regression/1_20_1_read_fail.schem");
        assertEquals(new Vector3i(15, 16, 20), schem.size());
    }

//    @Test
//    public void testSpongeV1() {
//        var schem = assertReadSchematic("/regression/sponge_1.schem");
//        assertEquals(new Vec(217, 70, 173), schem.size());
//    }

    private @NotNull Schematic assertReadSchematic(@NotNull String path) {
        try (var is = getClass().getResourceAsStream(path)) {
            assertNotNull(is, "Failed to load resource: " + path);
            return new SchematicReader().read(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
