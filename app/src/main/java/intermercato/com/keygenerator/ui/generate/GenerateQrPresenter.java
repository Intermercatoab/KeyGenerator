package intermercato.com.keygenerator.ui.generate;

import android.graphics.Bitmap;
import android.net.Uri;

import intermercato.com.keygenerator.repository.DatabaseRepository;


public class GenerateQrPresenter implements GenerateQrContract.Presenter {

    private GenerateQrContract.RealmRepository realmRepository;
    private GenerateQrContract.Repository repository;
    private GenerateQrContract.View view;

    public GenerateQrPresenter(GenerateQrContract.View v) {
        view = v;
        repository = new GenerateRepository(this);
        realmRepository = new DatabaseRepository(this);
    }

    @Override
    public void doSaveQr(String cKey, String cBitMapUri, String scaleId, String genKey) {
        realmRepository.saveCustomer(cKey, cBitMapUri, scaleId, genKey);
    }

    @Override
    public void doGenerateQr(String str) {
        repository.GenerateQr(str);
    }

    @Override
    public void doSetMeasure(int w, int h) {
        repository.SetPoint(w, h);
    }

    @Override
    public void doAskUserBeforeSaving(boolean b) {
        repository.collectQrData(b);
    }

    @Override
    public void returnCustomerKey(String str) {
        view.SetCustomerKey(str);
    }

    @Override
    public void returnBitmap(Bitmap bitmap) {
        view.SetBitmap(bitmap);
    }

    @Override
    public void returnBitmapUri(Uri uri) {
        view.SetBitMapUri(uri);
    }

    @Override
    public void keyWasSaved() {
        view.SetKeyWasSaved();
    }

    @Override
    public void keyWasNotSaved() {
        view.SetKeyWasNotSaved();
    }

}
