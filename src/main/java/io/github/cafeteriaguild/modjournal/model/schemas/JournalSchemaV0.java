package io.github.cafeteriaguild.modjournal.model.schemas;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.cafeteriaguild.modjournal.ModJournal;
import io.github.cafeteriaguild.modjournal.model.JournalPost;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

class JournalSchemaV0 implements JournalSchema {
    @Override
    public JournalPost parse(JsonObject object, String modid) {
        // modifications to the object before deserialization

        // remove the schemaVersion
        object.remove("schemaVersion");

        // set "modid" if absent
        if (modid != null) {
            if (object.has("modid")) {
                JsonElement value = object.get("modid");
                if (!value.isJsonPrimitive()) {
                    object.add("modid", new JsonPrimitive(modid));
                }
            } else {
                object.add("modid", new JsonPrimitive(modid));
            }
        }

        // if "authors" is not a array, box it inside a array
        if (object.has("authors")) {
            JsonElement authors = object.get("authors");
            if (authors.isJsonNull()) {
                object.add("authors", new JsonArray());
            } else if (authors.isJsonPrimitive()) {
                JsonArray array = new JsonArray();
                array.add(authors);
                object.add("authors", array);
            }
        }

        // if "content" is a array, join it to a string
        if (object.has("content")) {
            JsonElement content = object.get("content");
            if (content.isJsonArray()) {
                StringJoiner j = new StringJoiner("\n");
                joinToString(content.getAsJsonArray(), j);
                object.add("content", new JsonPrimitive(j.toString()));
            }
        }

        if (object.has("type")) {
            JsonElement type = object.get("type");
            if (type.isJsonPrimitive() && !type.getAsJsonPrimitive().isString()) {
                object.add("type", new JsonPrimitive("unspecified"));
            }
        } else {
            object.add("type", new JsonPrimitive("unspecified"));
        }

        if (object.has("timestamp")) {
            JsonElement timestamp = object.get("timestamp");
            if (timestamp.isJsonPrimitive()) {
                JsonPrimitive value = timestamp.getAsJsonPrimitive();
                if (value.isString()) {
                    object.add(
                        "timestamp",
                        new JsonPrimitive(
                            LocalDate.parse(value.getAsString(), DateTimeFormatter.ISO_DATE)
                                .atStartOfDay(ZoneOffset.UTC).toEpochSecond()
                        )
                    );
                }
            }
        }

        return ModJournal.GSON.fromJson(object, JournalPost.class);
    }

    private void joinToString(JsonArray array, StringJoiner j) {
        for (JsonElement element : array) {
            if (element.isJsonArray()) {
                joinToString(element.getAsJsonArray(), j);
            } else if (element.isJsonPrimitive()) {
                j.add(element.getAsJsonPrimitive().getAsString());
            }
        }
    }

}
