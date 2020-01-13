package modalclass;

public class CastModalClass {

    int cast_image;
    String cast_name;

    public CastModalClass(){


    }

    public CastModalClass(int cast_image, String cast_name) {
        this.cast_image = cast_image;
        this.cast_name = cast_name;
    }

    public int getCast_image() {
        return cast_image;
    }

    public void setCast_image(int cast_image) {
        this.cast_image = cast_image;
    }

    public String getCast_name() {
        return cast_name;
    }

    public void setCast_name(String cast_name) {
        this.cast_name = cast_name;
    }
}
