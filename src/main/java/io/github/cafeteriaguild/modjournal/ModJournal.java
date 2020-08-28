package io.github.cafeteriaguild.modjournal;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.cafeteriaguild.modjournal.model.JournalPost;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModJournal implements ClientModInitializer {
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
    public static final List<JournalPost> POSTS = new CopyOnWriteArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitializeClient() {
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
                        LOGGER.info("Mod '" + metadata.getId() + "' have a invalid modjournal:url custom value, as it must be a URL.");
                    }
                } else {
                    LOGGER.info("Mod '" + metadata.getId() + "' have a invalid modjournal:url custom value, as it must be a URL.");
                }
            }
        }
    }
}
