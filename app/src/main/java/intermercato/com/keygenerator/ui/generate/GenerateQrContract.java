package intermercato.com.keygenerator.ui.generate;


import android.graphics.Bitmap;
import android.net.Uri;

public class GenerateQrContract {


    public interface View {
        void Message(int t, String s);
        void SetBitmap(Bitmap bitmap);
        void SetBitMapUri(Uri uri);
    }

    public interface Presenter {
        void doSaveQr(String str);
        void doGenerateQr(String str);
        void doSetMeasure(int w , int h);
        void returnBitmap(Bitmap bitmap);
        void returnBitmapUri(Uri uri);
    }

    public interface Repository {
        void GenerateQr(String s);
        void GenerateBitMapQrImage(String s);
        void SetPoint(int w, int h);
    }

    public interface RealmRepository {
        void SaveRepository();
    }
}
