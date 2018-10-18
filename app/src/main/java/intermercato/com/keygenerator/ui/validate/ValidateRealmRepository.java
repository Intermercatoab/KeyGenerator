package intermercato.com.keygenerator.ui.validate;

import android.util.Log;

import intermercato.com.keygenerator.models.CustomerKey;
import io.realm.Realm;
import io.realm.RealmResults;

public class ValidateRealmRepository implements ValidateContract.RealmRepository {

    Realm realm;

    public ValidateRealmRepository() {
        Log.d("ValidateRealmRepository","init");

        realm = Realm.getDefaultInstance();
    }

    @Override
    public RealmResults<CustomerKey> getResult() {

         RealmResults<CustomerKey>  ck = realm.where(CustomerKey.class).findAll();
        Log.d("Validate","ck "+ck.size());
        return ck;
    }
}
