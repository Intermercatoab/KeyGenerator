package intermercato.com.keygenerator.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import android.os.StrictMode;
import android.print.PrintAttributes;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.zxing.BarcodeFormat;
import com.hp.mss.hpprint.model.ImagePrintItem;
import com.hp.mss.hpprint.model.PrintItem;
import com.hp.mss.hpprint.model.PrintJobData;
import com.hp.mss.hpprint.model.PrintMetricsData;
import com.hp.mss.hpprint.model.asset.ImageAsset;
import com.hp.mss.hpprint.util.PrintUtil;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import intermercato.com.keygenerator.R;
import intermercato.com.keygenerator.utils.AESEnDecryption;

import intermercato.com.keygenerator.utils.Contents;
import intermercato.com.keygenerator.utils.DataHandler;
import intermercato.com.keygenerator.utils.QRCodeEncoder;


public class GenerateKey extends AppCompatActivity implements PrintUtil.PrintMetricsListener {


    static final int PICKFILE_RESULT_CODE = 1;
    private EditText txtScaleId, txtCustomerKey;
    private TextView txtTitleGeneratedKey, txtGeneratedCustomerKey;
    private ImageView qrImage;

    public static String CONTENT_TYPE_PDF = "PDF";
    public static String CONTENT_TYPE_IMAGE = "Image";
    public static String MIME_TYPE_PDF = "application/pdf";
    public static String MIME_TYPE_IMAGE = "image/*";
    public static String MIME_TYPE_IMAGE_PREFIX = "image/";

    private String contentType = CONTENT_TYPE_IMAGE;
    private String error;
    boolean showMetricsDialog;
    private Uri userPickedUri;
    private PrintItem.ScaleType scaleType;
    private PrintAttributes.Margins margins;
    private PrintJobData printJobData;
    private PrintAttributes.MediaSize mediaSize5x7;

    private ImageButton btnPrint;
    private Bitmap theImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_key);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showMetricsDialog = true;
        scaleType = PrintItem.ScaleType.CENTER;
        contentType = "Image";
        margins = new PrintAttributes.Margins(0, 0, 0, 0);


        txtScaleId = findViewById(R.id.txtScaleId);
        txtCustomerKey = findViewById(R.id.txtGeneratedCustomerKey);
        txtTitleGeneratedKey = findViewById(R.id.txtTitleGeneratedKey);
        txtGeneratedCustomerKey = findViewById(R.id.txtGeneratedCustomerKey);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (Build.VERSION.SDK_INT >= 24) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }


        btnPrint = findViewById(R.id.btnTemp);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueButtonClicked(v);
                v.setVisibility(View.INVISIBLE);
            }
        });
        qrImage = findViewById(R.id.qrImage);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            generateCostumerKey();

/*
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(getContentMimeType());
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICKFILE_RESULT_CODE);
*/


        });

        mediaSize5x7 = new PrintAttributes.MediaSize("na_5x7_5x7in", "5 x 7", 5000, 7000);
    }


    private void createUserSelectedImageJobData() {

        Bitmap userPickedBitmap;

        try {
            userPickedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), userPickedUri);
            int width = userPickedBitmap.getWidth();
            int height = userPickedBitmap.getHeight();
            // if user picked bitmap is too big, just reduce the size, so it will not chock the print plugin
            if (width * height > 5000) {
                width = width / 2;
                height = height / 2;
                userPickedBitmap = Bitmap.createScaledBitmap(userPickedBitmap, width, height, true);
            }

            DisplayMetrics mDisplayMetric = getResources().getDisplayMetrics();
            float widthInches = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, width, mDisplayMetric);
            float heightInches = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, height, mDisplayMetric);

            ImageAsset imageAsset = new ImageAsset(this,
                    userPickedBitmap,
                    ImageAsset.MeasurementUnits.INCHES,
                    widthInches, heightInches);

            Log.d("Generate", "width " + width + "   height " + height + "   " + userPickedUri);

            PrintItem printItem4x6 = new ImagePrintItem(PrintAttributes.MediaSize.NA_INDEX_4X6, margins, scaleType, imageAsset);
            PrintItem printItem85x11 = new ImagePrintItem(PrintAttributes.MediaSize.NA_LETTER, margins, scaleType, imageAsset);
            PrintItem printItem5x7 = new ImagePrintItem(mediaSize5x7, margins, scaleType, imageAsset);

            printJobData = new PrintJobData(this, printItem4x6);
            printJobData.addPrintItem(printItem85x11);
            printJobData.addPrintItem(printItem5x7);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private String getMimeType(Uri uri) {
        Uri returnUri = uri;
        return getContentResolver().getType(returnUri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    userPickedUri = data.getData();
                    showFileInfo(userPickedUri);
                }
                break;
        }
    }

    private void showFileInfo(Uri uri) {
        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);

//        Log.d("Generate", " "+returnCursor.getString(nameIndex) +"  "+ Long.toString(returnCursor.getLong(sizeIndex)));
        returnCursor.moveToFirst();
        Log.d("Generate", "userPickedUri " + userPickedUri);
        Log.d("Generate", "File " + returnCursor.getString(nameIndex) + "(" + Long.toString(returnCursor.getLong(sizeIndex)) + "0");

    }

    private String getContentMimeType() {
        String mimeType = MIME_TYPE_IMAGE;

        if (contentType == CONTENT_TYPE_PDF)
            mimeType = MIME_TYPE_PDF;

        return mimeType;
    }

    private void generateCostumerKey() {

        String genKey = "e6980bc3-7378-4e95-8e92-3ffd18a8a41d"; //generateKey();
        String scaleId = txtScaleId.getText().toString();
        String customerKey = "";

        Log.d("Generate", "genKey: " + genKey + "\nScaleId: " + scaleId + "IvStr: " + DataHandler.IVSTR);

        if (scaleId.length() < 2) {
            error = "Scale ID can't be empty or less that four characters";
            Log.d("Generate", "error " + error);
            return;
        }

        hideTextFields(true);

        // ENCRYPT KEY

        try {
            Log.d("Generate", "Before Encrypt: " + genKey);
            customerKey = AESEnDecryption.encryptStrAndToBase64(DataHandler.IVSTR, scaleId, genKey);
            Log.d("Generate", "After Encrypt: " + customerKey);

            generateBitMapQrImage(customerKey);

        } catch (Exception e) {

        }

        txtCustomerKey.setText(customerKey);
    }

    private String generateKey() {
        return UUID.randomUUID().toString();
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void generateBitMapQrImage(String str) {

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        Log.d("Generate","w "+width+"   h "+height);
        int smallerDimension = width < height ? width : height;

        smallerDimension = smallerDimension * 3 / 4;
        Log.d("Generate","smallerDimension "+smallerDimension);
        //Encode with a QR Code image
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(str,
                null,
                Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(),
                smallerDimension);

        Bitmap bitmap = null;
        try {
            bitmap = qrCodeEncoder.encodeAsBitmap();
            qrImage.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }

        userPickedUri = getImageUri(this, bitmap);

        Log.d("Generate", "getUri " + userPickedUri);

        if (userPickedUri != null) {
            btnPrint.setVisibility(View.VISIBLE);
        }
    }

    public void continueButtonClicked(View v) {


        createPrintJobData();
        PrintUtil.setPrintJobData(printJobData);

        PrintUtil.sendPrintMetrics = showMetricsDialog;
        PrintUtil.print(this);

    }

    private void createPrintJobData() {

        Log.d("Generate", "1 --------> " + userPickedUri);


        if (userPickedUri != null && getMimeType(userPickedUri).startsWith(MIME_TYPE_IMAGE_PREFIX) && contentType == CONTENT_TYPE_IMAGE) {
            Log.d("Generate", "1 -------->");

        } else if (userPickedUri != null && getMimeType(userPickedUri).equals(MIME_TYPE_PDF) && contentType == CONTENT_TYPE_PDF) {
            Log.d("Generate", "2 -------->");


        } else {
            Log.d("Generate", "3 -------->");

        }
        createUserSelectedImageJobData();
        //Giving the print job a name.
        printJobData.setJobName("Example");

        //Optionally include print attributes.
        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.NA_LETTER)
                .build();
        printJobData.setPrintDialogOptions(printAttributes);

    }

    private void hideTextFields(boolean b) {
        txtTitleGeneratedKey.setVisibility(b == true ? View.VISIBLE : View.INVISIBLE);
        txtGeneratedCustomerKey.setVisibility(b == true ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        hideTextFields(false);
        super.onBackPressed();
    }

    @Override
    public void onPrintMetricsDataPosted(PrintMetricsData printMetricsData) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(printMetricsData.toMap().toString());
        builder.setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        builder.create().show();

    }
}
