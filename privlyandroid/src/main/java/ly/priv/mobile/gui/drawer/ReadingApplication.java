package ly.priv.mobile.gui.drawer;


import com.joanzapata.android.iconify.IconDrawable;

public class ReadingApplication {

    private String name;
    final public static String GMAIL = "GMail";
    final public static String FACEBOOK = "Facebook";
    final public static String TWITTER = "Twitter";
    private IconDrawable drawable;

    public ReadingApplication(String name, IconDrawable drawable) {
        this.name = name;
        this.drawable = drawable;
    }

    public String getName() {
        return name;
    }

    public IconDrawable getDrawable() {
        return drawable;
    }
}
