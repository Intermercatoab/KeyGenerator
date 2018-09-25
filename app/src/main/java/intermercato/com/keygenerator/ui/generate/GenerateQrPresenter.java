package intermercato.com.keygenerator.ui.generate;

import android.graphics.Bitmap;
import android.net.Uri;


public class GenerateQrPresenter implements GenerateQrContract.Presenter {

    private GenerateQrContract.Repository repository;
    private GenerateQrContract.View view;
    public GenerateQrPresenter(GenerateQrContract.View v) {
        view = v;
        repository = new GenerateRepository(this);
    }

    @Override
    public void doSaveQr(String str) {

    }

    @Override
    public void doGenerateQr(String str) {
        repository.GenerateQr( str );
    }

    @Override
    public void doSetMeasure(int w, int h) {
        repository.SetPoint(w, h);
    }

    @Override
    public void returnBitmap(Bitmap bitmap) {
        view.SetBitmap(bitmap);
    }

    @Override
    public void returnBitmapUri(Uri uri) {
        view.SetBitMapUri(uri);
    }

}
