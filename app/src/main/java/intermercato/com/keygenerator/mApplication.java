package intermercato.com.keygenerator;

import android.app.Application;
import android.content.Context;

public class mApplication extends Application {

    /* Used to get context from AnyWhere! */
    private static mApplication instance;

    public static mApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
        // or return instance.getApplicationContext();
    }

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

    }
}