package modalclass;

public class TheaterModalClass {

    private int poster_image;
    private String poster_name;
    private String language;
    private String like;

    public TheaterModalClass(){


    }

    public TheaterModalClass(int poster_image, String poster_name, String language, String like) {
        this.poster_image = poster_image;
        this.poster_name = poster_name;
        this.language = language;
        this.like = like;
    }

    public int getPoster_image() {
        return poster_image;
    }

    public void setPoster_image(int poster_image) {
        this.poster_image = poster_image;
    }

    public String getPoster_name() {
        return poster_name;
    }

    public void setPoster_name(String poster_name) {
        this.poster_name = poster_name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }
}
