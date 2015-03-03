package ly.priv.mobile.gui.drawer;


public class ReadingApplication {

    private String name;
    private Reading_Application_Type type;
    int iconResId;

    public ReadingApplication(String name, Reading_Application_Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Reading_Application_Type getType() {
        return type;
    }

    public void setType(Reading_Application_Type type) {
        this.type = type;
    }
}

enum Reading_Application_Type {
    GMAIL,
    TWITTER,
    FACEBOOK
}
