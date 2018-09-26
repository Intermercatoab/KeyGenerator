package intermercato.com.keygenerator.ui.generate;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.google.zxing.BarcodeFormat;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import intermercato.com.keygenerator.mApplication;
import intermercato.com.keygenerator.utils.AESEnDecryption;
import intermercato.com.keygenerator.utils.Contents;
import intermercato.com.keygenerator.utils.DataHandler;
import intermercato.com.keygenerator.utils.QRCodeEncoder;

public class GenerateRepository implements GenerateQrContract.Repository {

    private int width;
    private int height;
    private int smallerDimension;
    private GenerateQrContract.Presenter presenter;
    private String customerKey;
    private Uri bitmapUri;
    private String scaleId;
    private String genKey;


    public GenerateRepository(GenerateQrContract.Presenter p) {
        presenter = p;
    }

    @Override
    public void GenerateQr(String strId) {

        genKey = "e6980bc3-7378-4e95-8e92-3ffd18a8a41d"; //generateKey();
        scaleId = strId;
        customerKey = "";
        String errorMessage;

        Log.d("Generate", "genKey: " + genKey + "\nScaleId: " + scaleId + "IvStr: " + DataHandler.IVSTR);

        if (scaleId.length() < 2) {
            errorMessage = "Scale ID can't be empty or less that four characters";
            Log.d("Generate", "error " + errorMessage);
            return;
        }

        try {
            // ENCRYPT KEY
            Log.d("Generate", "Before Encrypt: " + genKey);
            customerKey = AESEnDecryption.encryptStrAndToBase64(DataHandler.IVSTR, scaleId, genKey);
            Log.d("Generate", "After Encrypt: " + customerKey);

        } catch (Exception e) {

        }

        if (width != 0 && height != 0)
            GenerateBitMapQrImage(customerKey);

        presenter.returnCustomerKey(customerKey);
    }

    @Override
    public void GenerateBitMapQrImage(String str) {
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(str,
                null,
                Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(), width);

        Bitmap bitmap = null;
        try {
            bitmap = qrCodeEncoder.encodeAsBitmap();

        } catch (Exception e) {
            e.printStackTrace();
        }
        presenter.returnBitmap(bitmap);

        bitmapUri = getImageUri(bitmap);

        presenter.returnBitmapUri(bitmapUri);


    }

    @Override
    public void SetPoint(int w, int h) {

        width = w;
        height = h;
        smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;
        Log.d("Generate", "SetPoint w " + width + "   h " + height);


    }

    @Override
    public void collectQrData(boolean b) {
        if(b) {
            presenter.doSaveQr(customerKey, bitmapUri.toString(), scaleId, genKey);
        } else {
            customerKey = ""; bitmapUri=null; scaleId=""; genKey = "";
        }
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(mApplication.getContext().getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String generateKey() {
        return UUID.randomUUID().toString();
    }
}
