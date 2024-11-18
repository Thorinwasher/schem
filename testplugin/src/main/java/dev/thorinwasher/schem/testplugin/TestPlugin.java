package dev.thorinwasher.schem.testplugin;

import dev.thorinwasher.schem.Schematic;
import dev.thorinwasher.schem.SchematicReader;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            List<Path> schemToTest = listFilesOfInternalDirectory("/schem");
            for (Path path : schemToTest) {
                if (!path.getFileName().toString().endsWith(".schem")) {
                    continue;
                }
                try (InputStream inputStream = TestPlugin.class.getResourceAsStream("/schem/" + path.getFileName().toString())) {
                    Schematic schematic = new SchematicReader().read(inputStream);
                    boolean containsSomething = false;
                    for (BlockData blockData : schematic.palette()) {
                        if (!blockData.getMaterial().isAir()) {
                            containsSomething = true;
                        }
                    }
                    if (!containsSomething) {
                        getLogger().severe("schematic " + path.getFileName() + " did not contain any data.");
                    }
                }
            }
            getLogger().info("Successfully read all schematics.");
        } catch (Exception e) {
            getLogger().severe("ERROR");
            e.printStackTrace();
        } finally {
            Bukkit.getServer().shutdown();
        }
    }

    public static List<Path> listFilesOfInternalDirectory(String directory) throws IOException, URISyntaxException {
        URL directoryURL = TestPlugin.class.getResource(directory);
        if (directoryURL == null) {
            return new ArrayList<>();
        }
        URI uri = directoryURL.toURI();
        FileSystem fileSystem = null;
        List<Path> walk;
        try {
            Path path;
            if (uri.getScheme().equals("jar")) {
                fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                path = fileSystem.getPath(directory);
            } else {
                path = Paths.get(uri);
            }
            try (Stream<Path> paths = Files.walk(path, 1)) {
                walk = paths.toList();
            }
        } finally {
            if (fileSystem != null) {
                fileSystem.close();
            }
        }
        return walk;
    }
}