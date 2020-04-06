package com.algaita.models;

public class Videos {
    int status;

    String title, description, info, trailer_url, video_url, price, release_date, poster, cover, videoid, watch, downloads, size;

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

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCover() {
        return cover;
    }


    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public String getDownloads() {
        return downloads;
    }

    public String getWatch() {
        return watch;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

    public void setWatch(String watch) {
        this.watch = watch;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
    }
}
