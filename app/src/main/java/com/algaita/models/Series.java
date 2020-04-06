package com.algaita.models;

public class Series {
    String id, title, poster, cover, trailer_url, release_date, description, total_episode, size;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public void setTrailer_url(String trailer_url) {
        this.trailer_url = trailer_url;
    }

    public String getPoster() {
        return poster;
    }

    public String getId() {
        return id;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getTrailer_url() {
        return trailer_url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getTotal_episode() {
        return total_episode;
    }

    public void setTotal_episode(String total_episode) {
        this.total_episode = total_episode;
    }


    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
