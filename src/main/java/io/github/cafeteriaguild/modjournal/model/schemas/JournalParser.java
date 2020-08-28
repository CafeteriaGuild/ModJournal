package io.github.cafeteriaguild.modjournal.model.schemas;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.cafeteriaguild.modjournal.model.JournalPost;

import java.util.List;
import java.util.Map;

public class JournalParser {
    private static final Map<Integer, JournalSchema> SCHEMAS = ImmutableMap.of(
        0, new JournalSchemaV0()
    );

    public static List<JournalPost> parse(JsonElement element, String modid) {
        ImmutableList.Builder<JournalPost> builder = ImmutableList.builder();
        collectPosts(element, modid, builder);
        return builder.build();
    }

    private static void collectPosts(JsonElement element, String modid, ImmutableList.Builder<JournalPost> builder) {
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                collectPosts(child, modid, builder);
            }
        } else if (element.isJsonObject()) {
            builder.add(parsePost(element.getAsJsonObject(), modid));
        } else {
            throw new IllegalStateException("root object must be a json object or array");
        }
    }

    private static JournalPost parsePost(JsonObject object, String modid) {
        if (!object.has("schemaVersion")) {
            throw new IllegalStateException("post must contain a schemaVersion");
        }
        JsonElement schemaVersion = object.get("schemaVersion");
        if (schemaVersion.isJsonPrimitive()) {
            JsonPrimitive value = schemaVersion.getAsJsonPrimitive();
            if (value.isNumber()) {
                JournalSchema schema = SCHEMAS.get(value.getAsInt());
                if (schema != null) {
                    return schema.parse(object, modid);
                }
                throw new IllegalStateException("schemaVersion is unsupported");
            }
        }
        throw new IllegalStateException("schemaVersion must be a number");
    }

}
