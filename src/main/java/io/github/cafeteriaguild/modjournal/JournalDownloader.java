package io.github.cafeteriaguild.modjournal;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.cafeteriaguild.modjournal.model.JournalPost;
import io.github.cafeteriaguild.modjournal.model.schemas.JournalParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class JournalDownloader implements Supplier<List<JournalPost>> {
    private static final HttpClient client = HttpClientBuilder.create()
        .setConnectionManager(new PoolingHttpClientConnectionManager(3, TimeUnit.SECONDS))
        .build();

    private final String modid;
    private final URI uri;

    public JournalDownloader(String modid, URI uri) {
        this.modid = modid;
        this.uri = uri;
    }

    @Override
    public List<JournalPost> get() {
        try {
            HttpResponse response = client.execute(new HttpGet(uri));
            JsonElement element = new JsonParser().parse(new InputStreamReader(response.getEntity().getContent()));
            return JournalParser.parse(element, modid);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
