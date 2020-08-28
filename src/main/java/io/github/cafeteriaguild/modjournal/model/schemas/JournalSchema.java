package io.github.cafeteriaguild.modjournal.model.schemas;

import com.google.gson.JsonObject;
import io.github.cafeteriaguild.modjournal.model.JournalPost;

public interface JournalSchema {
    JournalPost parse(JsonObject element, String modid);
}
