package ly.priv.mobile.gui.drawer;

public class NavDrawerItem {

    private int type;
    private Object object;

    public NavDrawerItem(int type, Object object) {
        this.type = type;
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public int getType() {
        return type;
    }
}
