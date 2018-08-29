package be.softec.decisiontable.data;

import java.awt.*;

@SuppressWarnings("unused")
public class Colour {

    private final String name;
    private final Color color;

    private Colour(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public static Colour c(String name, Color color) {
        return new Colour(name, color);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Colour colour = (Colour) o;

        if (name != null ? !name.equals(colour.name) : colour.name != null) return false;
        return color != null ? color.equals(colour.color) : colour.color == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }
}
