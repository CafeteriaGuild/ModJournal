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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModJournal implements ClientModInitializer {
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
    public static final List<JournalPost> POSTS = new CopyOnWriteArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitializeClient() {
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            ModMetadata metadata = mod.getMetadata();
            String id = metadata.getId();
            if ("minecraft".equals(id)) {
                continue;
            }

            List<CompletableFuture<List<JournalPost>>> awaiting = new ArrayList<>();
            if (metadata.containsCustomValue("modjournal:source")) {
                CustomValue value = metadata.getCustomValue("modjournal:source");
                if (value.getType() == CustomValue.CvType.STRING) {
                    String http = value.getAsString();
                    try {
                        CompletableFuture.supplyAsync(new JournalDownloader(id, new URI(http))).thenAccept(POSTS::addAll);
                    } catch (URISyntaxException e) {
                        LOGGER.info("Mod '" + metadata.getId() + "' have a invalid modjournal:source custom value, as it must be a URL.");
                    }
                } else {
                    LOGGER.info("Mod '" + metadata.getId() + "' have a invalid modjournal:source custom value, as it must be a URL.");
                }
            }
        }
    }
}
