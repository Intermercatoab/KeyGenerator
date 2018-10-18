package intermercato.com.keygenerator;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import io.realm.Realm;
import io.realm.RealmConfiguration;

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


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (Build.VERSION.SDK_INT >= 24) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("mRealm.realm")
                .build();

       Realm.getInstance(config);

    }
}