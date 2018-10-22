package intermercato.com.keygenerator.ui.generate;


import android.graphics.Bitmap;
import android.net.Uri;

public class GenerateQrContract {


    public interface View {
        void Message(int t, String s);
        void SetBitmap(Bitmap bitmap);
        void SetBitMapUri(Uri uri);
        void SetCustomerKey(String str);
        void SetKeyWasSaved();
        void SetKeyWasNotSaved();
    }

    public interface Presenter {
        void doSaveQr(String cKey, String cBitMapUri, String scaleId, String genKey);
        void doGenerateQr(String str);
        void doSetMeasure(int w , int h);
        void doAskUserBeforeSaving(boolean b);
        void returnCustomerKey(String str);
        void returnBitmap(Bitmap bitmap);
        void returnBitmapUri(Uri uri);
        void keyWasSaved();
        void keyWasNotSaved();
    }

    public interface Repository {
        void GenerateQr(String s);
        void GenerateBitMapQrImage(String s);
        void SetPoint(int w, int h);
        void collectQrData(boolean b);
    }

    public interface RealmRepository {
        void saveCustomer(String cKey, String cBitMapUri,  String genKey, String scaleId);
    }
}
