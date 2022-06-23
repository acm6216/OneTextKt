package cn.guo.onetext.widget.SvgAnimationView;

public class PathModel {
    private String path;
    private String color;

    public PathModel() {
        super();
    }

    public PathModel(String path, String color) {
        this.path = path;
        this.color = color;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "PathModel{" +
                "path='" + path + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}