package com.photosviewer.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

public class Photo {
    private String title;
    private String url;
    private List<String> tags;
    private ZonedDateTime dateTaken;
    private ZonedDateTime published;

    public Photo(String title, String url, List<String> tags, ZonedDateTime dateTaken, ZonedDateTime published) {
        this.title = title;
        this.url = url;
        this.tags = tags;
        this.dateTaken = dateTaken;
        this.published = published;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getTags() {
        return tags;
    }

    public ZonedDateTime getDateTaken() {
        return dateTaken;
    }

    public ZonedDateTime getPublished() {
        return published;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return Objects.equals(getTitle(), photo.getTitle()) &&
                Objects.equals(getUrl(), photo.getUrl()) &&
                Objects.equals(getTags(), photo.getTags()) &&
                Objects.equals(getDateTaken(), photo.getDateTaken()) &&
                Objects.equals(getPublished(), photo.getPublished());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getUrl(), getTags(), getDateTaken(), getPublished());
    }
}
