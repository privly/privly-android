package ly.priv.mobile.gui.drawer;

import com.joanzapata.android.iconify.IconDrawable;

public class PrivlyApplication {

    public static String MESSAGE_APP = "Message";
    public static String PLAINPOST_APP = "PlainPost";
    public static String HISTORY_APP = "History";
    String name, path;
    IconDrawable drawable;

    public PrivlyApplication(String name, String path, IconDrawable drawable) {
        this.name = name;
        this.path = path;
        this.drawable = drawable;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public IconDrawable getDrawable() {
        return drawable;
    }
}
