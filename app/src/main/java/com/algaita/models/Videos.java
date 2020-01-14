package com.algaita.models;

public class Videos {
    int status;

    String title, description, trailer_url, video_url, price, release_date, poster, videoid;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getTrailer_url() {
        return trailer_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setTrailer_url(String trailer_url) {
        this.trailer_url = trailer_url;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

}
