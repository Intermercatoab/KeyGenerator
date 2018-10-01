package intermercato.com.keygenerator.ui.validate;

import intermercato.com.keygenerator.models.CustomerKey;

import io.realm.RealmResults;

public class ValidateContract {

    public interface View {


    }

    public interface Presenter {
        RealmResults<CustomerKey> getRealmResult();
    }

    public interface RealmRepository {
        RealmResults<CustomerKey> getResult();
    }
}
