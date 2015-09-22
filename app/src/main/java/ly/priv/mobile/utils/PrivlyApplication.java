package ly.priv.mobile.utils;

import android.app.Application;

/**
 * Created by shivam on 9/22/15.
 */
public class PrivlyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Values.init(this);
    }
}
