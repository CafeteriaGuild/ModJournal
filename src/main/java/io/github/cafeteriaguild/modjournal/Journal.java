package io.github.cafeteriaguild.modjournal;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.cafeteriaguild.modjournal.model.JournalPost;
import io.github.cafeteriaguild.modjournal.model.schemas.JournalParser;
import net.minecraft.util.Identifier;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Journal {
    public static final Journal INSTANCE = new Journal();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final HttpClient HTTP_CLIENT = HttpClientBuilder.create()
        .setConnectionManager(new PoolingHttpClientConnectionManager(3, TimeUnit.SECONDS))
        .build();
    private final Map<URI, String> dejavu = new ConcurrentHashMap<>();
    public Data data = new Data();

    private Journal() {
    }

    public void load(String modid, URI uri) {
        if (modid == null) {
            modid = "modjournal.orphan";
        }
        // avoid duplicates
        String conflict = dejavu.putIfAbsent(uri, modid);
        if (conflict != null) {
            LOGGER.warn(
                "{} tried to register journal URL already associated with {}: {}",
                "modjournal.orphan".equals(modid) ? "Unspecified source" : modid,
                "modjournal.orphan".equals(conflict) ? "Unspecified source" : modid,
                uri
            );
            return;
        }
        ForkJoinPool.commonPool().execute(new JournalDownloader(modid, uri));
    }

    public List<JournalPost> getPosts() {
        return ImmutableList.sortedCopyOf(data.posts.values());
    }

    public void submitAll(List<JournalPost> posts) {
        for (JournalPost post : posts) {
            submit(post);
        }
    }

    public void remove(JournalPost post) {
        Identifier identifier = new Identifier(post.getModid(), post.getPostid());
        data.posts.remove(identifier);
        data.deletedPosts.add(identifier);
        Journal.this.save();
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean validate(JournalPost post) {
        if (post.getModid() == null) {
            return false;
        } else if (post.getTitle() == null) {
            return false;
        } else if (post.getPostid() == null) {
            return false;
        } else if (post.getAuthors() == null) {
            return false;
        } else if (post.getContent() == null) {
            return false;
        } else {
            return true;
        }
    }

    private void submit(JournalPost post) {
        if (!validate(post)) {
            return;
        }

        Identifier id = new Identifier(post.getModid(), post.getPostid());

        if (!data.deletedPosts.contains(id)) {
            data.posts.putIfAbsent(id, post);
        }
    }

    public synchronized void load() {
        File folder = new File("modjournal_cache");
        if (folder.isFile() && !folder.delete()) {
            LOGGER.warn("ModJournal's cache folder is a file and couldn't get deleted, ignoring configs.");
            return;
        }
        if (!folder.exists() && !folder.mkdirs()) {
            LOGGER.warn("ModJournal's cache folder is a file and couldn't get deleted, ignoring configs.");
            return;
        }
        File file = new File(folder, "data.json");
        if (file.isDirectory()) {
            LOGGER.warn("ModJournal's data cache file is a folder, please delete it.");
        } else if (file.isFile()) {
            try (InputStream in = new FileInputStream(file); Reader r = new InputStreamReader(in)) {
                data = ModJournal.GSON.fromJson(r, Data.class);
            } catch (Exception e) {
                LOGGER.error("Error on deserializing data cache file", e);
            }
        }

    }

    public synchronized void save() {
        File folder = new File("modjournal_cache");
        if (folder.isFile() && !folder.delete()) {
            LOGGER.warn("Cache folder is a file and couldn't get deleted, ignoring configs.");
            return;
        }
        if (!folder.exists() && !folder.mkdirs()) {
            LOGGER.warn("Cache folder is a file and couldn't get deleted, ignoring configs.");
            return;
        }
        File file = new File(folder, "data.json");
        try (OutputStream out = new FileOutputStream(file); Writer w = new OutputStreamWriter(out)) {
            ModJournal.GSON.toJson(data, w);
        } catch (Exception e) {
            LOGGER.error("Error on serializing data cache file", e);
        }
    }

    public static class Data {
        private final Map<Identifier, JournalPost> posts = new ConcurrentHashMap<>();
        private final Set<Identifier> deletedPosts = ConcurrentHashMap.newKeySet();
    }

    class JournalDownloader implements Runnable {
        private final String modid;
        private final URI uri;

        public JournalDownloader(String modid, URI uri) {
            this.modid = modid;
            this.uri = uri;
        }

        @Override
        public void run() {
            try {
                HttpResponse response = HTTP_CLIENT.execute(new HttpGet(uri));
                JsonElement element = new JsonParser().parse(new InputStreamReader(response.getEntity().getContent()));
                List<JournalPost> list = JournalParser.parse(element, modid);
                Journal.this.submitAll(list);
            } catch (Exception ignored) {
            }
            Journal.this.save();
        }
    }
}
