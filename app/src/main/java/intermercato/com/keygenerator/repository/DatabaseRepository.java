package intermercato.com.keygenerator.repository;

import android.util.Log;

import java.util.UUID;

import intermercato.com.keygenerator.models.CustomerKey;
import intermercato.com.keygenerator.ui.generate.GenerateQrContract;
import intermercato.com.keygenerator.utils.mTime;
import io.realm.Realm;

public class DatabaseRepository implements GenerateQrContract.RealmRepository{


    GenerateQrContract.Presenter presenter;
    public DatabaseRepository(GenerateQrContract.Presenter p) {
        presenter = p;
    }

    @Override
    public void saveCustomer(String cKey, String cBitMapUri, String scaleId, String genKey) {

            Log.d("DataBase","cKey: "+cKey+"\nbitMapUri "+cBitMapUri+"\nscaleId "+scaleId+"\ngeneratedKey "+genKey);

            Realm.getDefaultInstance().executeTransactionAsync(realm -> {

                CustomerKey c = realm.createObject(CustomerKey.class, UUID.randomUUID().toString());
                c.setCustomerKey(cKey);
                c.setCreated(mTime.getCurrentTime("yyyy/MM/dd HH:mm:ss"));
                c.setScaleId(scaleId);

            },() -> {

                Log.d("DataBase","Success ");
                presenter.keyWasSaved();

            }, error ->{

                Log.d("DataBase","error");
                presenter.keyWasNotSaved();
            });
    }
}
