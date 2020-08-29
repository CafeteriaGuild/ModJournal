package io.github.cafeteriaguild.modjournal.model;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class JournalPost implements Comparable<JournalPost> {
    private String modid;
    private String thumbnail;
    private String postid;
    private List<String> authors;
    private String title;
    private String content;
    private Long timestamp;

    public String getModid() {
        return modid;
    }

    public void setModid(String modid) {
        this.modid = modid;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "JournalPost{" +
            "modid='" + modid + '\'' +
            ", thumbnail='" + thumbnail + '\'' +
            ", postid='" + postid + '\'' +
            ", authors=" + authors +
            ", title='" + title + '\'' +
            ", content='" + content + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JournalPost that = (JournalPost) o;
        return Objects.equals(modid, that.modid) &&
            Objects.equals(thumbnail, that.thumbnail) &&
            Objects.equals(postid, that.postid) &&
            Objects.equals(authors, that.authors) &&
            Objects.equals(title, that.title) &&
            Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modid, thumbnail, postid, authors, title, content);
    }

    public static final Comparator<JournalPost> COMPARATOR = Comparator.comparing(j -> j.modid + ":" + j.postid);

    @Override
    public int compareTo(JournalPost other) {
        return COMPARATOR.compare(this, other);
    }
}
