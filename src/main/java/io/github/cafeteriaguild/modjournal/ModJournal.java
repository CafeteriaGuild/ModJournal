package io.github.cafeteriaguild.modjournal;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.cafeteriaguild.modjournal.config.JournalConfig;
import io.github.cafeteriaguild.modjournal.gson.IdentifierDeserializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class ModJournal implements ClientModInitializer {
    public static final Gson GSON = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .registerTypeAdapter(Identifier.class, new IdentifierDeserializer())
        .create();
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitializeClient() {
        Journal.INSTANCE.load();
        loadFromConfig();
        loadFromMetadata();
    }

    private void loadFromConfig() {
        JournalConfig config = new JournalConfig();
        config.load();
        for (Map.Entry<String, URI> entry : config.customJournals.getValue().entrySet()) {
            Journal.INSTANCE.load(entry.getKey(), entry.getValue());
        }
    }

    private void loadFromMetadata() {
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            ModMetadata metadata = mod.getMetadata();
            String modid = metadata.getId();
            if ("minecraft".equals(modid)) {
                continue;
            }

            if (metadata.containsCustomValue("modjournal:url")) {
                CustomValue value = metadata.getCustomValue("modjournal:url");
                if (value.getType() == CustomValue.CvType.STRING) {
                    String uri = value.getAsString();
                    try {
                        Journal.INSTANCE.load(modid, new URI(uri));
                    } catch (URISyntaxException e) {
                        LOGGER.info("Mod '{}' have a invalid modjournal:url custom value, as it must be a URL.", modid);
                    }
                } else {
                    LOGGER.info("Mod '{}' have a invalid modjournal:url custom value, as it must be a URL.", modid);
                }
            }
        }
    }
}
