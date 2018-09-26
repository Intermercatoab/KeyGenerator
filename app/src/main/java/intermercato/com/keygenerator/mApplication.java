package intermercato.com.keygenerator;

import android.app.Application;
import android.content.Context;

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

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("mRealm.realm")
                .build();

       Realm.getInstance(config);

    }
}