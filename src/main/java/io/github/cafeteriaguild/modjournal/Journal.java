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

import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Journal {
    public static final Journal INSTANCE = new Journal();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final HttpClient HTTP_CLIENT = HttpClientBuilder.create()
        .setConnectionManager(new PoolingHttpClientConnectionManager(3, TimeUnit.SECONDS))
        .build();
    private final Map<Identifier, JournalPost> posts = new ConcurrentHashMap<>();
    private final Map<URI, String> dejavu = new ConcurrentHashMap<>();

    private Journal() {
    }

    public List<JournalPost> getPosts() {
        return ImmutableList.sortedCopyOf(posts.values());
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

    public void submitAll(List<JournalPost> posts) {
        for (JournalPost post : posts) {
            submit(post);
        }
    }

    private void submit(JournalPost post) {
        if (!validate(post)) {
            return;
        }

        Identifier id = new Identifier(post.getModid(), post.getPostid());

        JournalPost conflict = posts.putIfAbsent(id, post);
        if (conflict != null) {
            LOGGER.warn("Duplicate posts for identifier '{}', discarding the latter.", id);
        }
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
        }
    }
}
