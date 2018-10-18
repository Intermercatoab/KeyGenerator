package intermercato.com.keygenerator.ui.validate;

import intermercato.com.keygenerator.models.CustomerKey;
import io.realm.RealmResults;

public class ValidatePresenter implements ValidateContract.Presenter {

    private ValidateContract.View view;
    private ValidateContract.RealmRepository realmRepository;

    public ValidatePresenter(ValidateContract.View v) {
        view = v;
        realmRepository = new ValidateRealmRepository();

    }



    @Override
    public RealmResults<CustomerKey> getRealmResult() {
        return realmRepository.getResult();
    }
}
