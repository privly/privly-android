package ly.priv.mobile.gui.drawer;

public class PrivlyApplication {

    public static String MESSAGE_APP = "Message";
    public static String PLAINPOST_APP = "PlainPost";
    public static String HISTORY_APP = "History";
    String name, path;
    int iconResId;

    public PrivlyApplication(String name, String path, int iconResId) {
        this.name = name;
        this.path = path;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getIconResId() {
        return iconResId;
    }
}
