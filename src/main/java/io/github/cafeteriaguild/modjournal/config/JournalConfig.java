package io.github.cafeteriaguild.modjournal.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.MapConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.StringConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

public class JournalConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final JanksonValueSerializer JANKSON = new JanksonValueSerializer(false);

    private static final StringConfigType<String> TYPE_MODID = ConfigTypes.STRING.withPattern("[a-z0-9_.-]+");
    private static final StringConfigType<URI> TYPE_URI = ConfigTypes.STRING.derive(URI.class, URI::create, URI::toString);
    private static final MapConfigType<Map<String, URI>, String> TYPE_MODID_URI = ConfigTypes.makeMap(TYPE_MODID, TYPE_URI);

    public final PropertyMirror<Map<String, URI>> customJournals = PropertyMirror.create(TYPE_MODID_URI);

    public void load() {
        File folder = new File("config");
        if (folder.isFile() && !folder.delete()) {
            LOGGER.warn("Config folder is a file and couldn't get deleted, ignoring configs.");
            return;
        }
        if (!folder.exists() && !folder.mkdirs()) {
            LOGGER.warn("Config folder is a file and couldn't get deleted, ignoring configs.");
            return;
        }
        File file = new File(folder, "modjournal.json5");
        ConfigTree tree = createConfigTree();
        if (file.isDirectory()) {
            LOGGER.warn("ModJournal's config file is a folder, please delete it.");
            return;
        } else if (file.isFile()) {
            try (InputStream in = new FileInputStream(file)) {
                FiberSerialization.deserialize(tree, in, JANKSON);
            } catch (Exception e) {
                LOGGER.error("Error on deserializing config file", e);
            }
        }
        try (OutputStream out = new FileOutputStream(file)) {
            FiberSerialization.serialize(tree, out, JANKSON);
        } catch (Exception e) {
            LOGGER.error("Error on serializing config file", e);
        }
    }

    private ConfigTree createConfigTree() {
        // @formatter:off
        return ConfigTree.builder()
            .beginValue("customJournals", TYPE_MODID_URI, new LinkedHashMap<>())
            .withComment("Custom ModJournals sources")
            .finishValue(customJournals::mirror)
            .build();
        // @formatter:on
    }
}
