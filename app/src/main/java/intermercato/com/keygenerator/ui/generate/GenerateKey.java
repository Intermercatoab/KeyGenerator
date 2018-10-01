package intermercato.com.keygenerator.ui.generate;

import android.app.Activity;
import android.app.AlertDialog;


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
import android.support.design.widget.CoordinatorLayout;

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


import com.hp.mss.hpprint.model.ImagePrintItem;
import com.hp.mss.hpprint.model.PrintItem;
import com.hp.mss.hpprint.model.PrintJobData;
import com.hp.mss.hpprint.model.PrintMetricsData;
import com.hp.mss.hpprint.model.asset.ImageAsset;
import com.hp.mss.hpprint.util.PrintUtil;



import java.io.IOException;



import intermercato.com.keygenerator.R;


import intermercato.com.keygenerator.models.CustomerKey;
import intermercato.com.keygenerator.utils.Constants;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class GenerateKey extends AppCompatActivity implements GenerateQrContract.View, PrintUtil.PrintMetricsListener, View.OnClickListener {


    static final int PICKFILE_RESULT_CODE = 1;
    private EditText txtScaleId, txtCustomerKey;
    private TextView txtTitleGeneratedKey, txtGeneratedCustomerKey;
    private ImageView qrImage;

    public static String CONTENT_TYPE_PDF = "PDF";
    public static String CONTENT_TYPE_IMAGE = "Image";
    public static String MIME_TYPE_PDF = "application/pdf";
    public static String MIME_TYPE_IMAGE = "image/*";
    public static String MIME_TYPE_IMAGE_PREFIX = "image/";
    public static String STATE_READY_TO_SAVE = "readytosave";

    private String contentType = CONTENT_TYPE_IMAGE;
    private String error;
    boolean showMetricsDialog;
    private Uri bitmapUri;
    private PrintItem.ScaleType scaleType;
    private PrintAttributes.Margins margins;
    private PrintJobData printJobData;
    private PrintAttributes.MediaSize mediaSize5x7;

    private Button btnGenerateQR;
    private ImageButton btnPrint;
    private Bitmap theImage;
    private CoordinatorLayout coordinator;
    private GenerateQrContract.Presenter presenter;

    Realm realm;
    RealmChangeListener<RealmResults<CustomerKey>> listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_key);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //btnGenerateQR.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_help_icon));

        realm = Realm.getDefaultInstance();
        RealmResults<CustomerKey> customerKeys = realm.where(CustomerKey.class).findAllAsync();
        listener = c -> {
            for(CustomerKey ck : c) {
                Log.d("DataBase", "size " +ck.getCreated());
                Log.d("DataBase", "size " +ck.getCustomerKey());
                Log.d("DataBase", "size " +ck.getScaleId());
                Log.d("DataBase", "size " );
            }
        };


        customerKeys.addChangeListener(listener);
        presenter = new GenerateQrPresenter(this);

        /* Get Windows manager for screen width and height */
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        Log.d("Generate", "w " + width + "   h " + height);
        int smallerDimension = width < height ? width : height;

        smallerDimension = smallerDimension * 3 / 4;
        Log.d("Generate", "smallerDimension " + smallerDimension);
        presenter.doSetMeasure(width,height);

        btnGenerateQR = findViewById(R.id.btnGenerateQR);
        btnGenerateQR.setOnClickListener(this);
        coordinator = findViewById(R.id.coordinator);

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


        btnPrint = findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(this);

        qrImage = findViewById(R.id.qrImage);

/*

            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();


            showMetricsDialog = true;
            scaleType = PrintItem.ScaleType.CENTER;
            contentType = "Image";
            margins = new PrintAttributes.Margins(0, 0, 0, 0);

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(getContentMimeType());
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICKFILE_RESULT_CODE);
*/

        mediaSize5x7 = new PrintAttributes.MediaSize("na_5x7_5x7in", "5 x 7", 5000, 7000);
    }

    private void DoSnack(String msg){
        if(coordinator == null) return;

        Snackbar.make(coordinator, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void createUserSelectedImageJobData() {

        Bitmap userPickedBitmap;

        try {
            userPickedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), bitmapUri);
            int width = userPickedBitmap.getWidth();
            int height = userPickedBitmap.getHeight();
            // if user picked bitmap is too big, just reduce the size, so it will not chock the print plugin
/*
            if (width * height > 5000) {
                width = width / 2;
                height = height / 2;
                userPickedBitmap = Bitmap.createScaledBitmap(userPickedBitmap, width, height, true);
                Log.d("Generate","_>  w: "+width+"\nh: "+height);
            }
*/
            Log.d("Generate", "width " + width + "   height " + height + "   ");

            DisplayMetrics mDisplayMetric = getResources().getDisplayMetrics();
            float widthInches =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, width, mDisplayMetric);
            float heightInches = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, height, mDisplayMetric);

            ImageAsset imageAsset = new ImageAsset(this, userPickedBitmap, ImageAsset.MeasurementUnits.INCHES, widthInches, heightInches);

            Log.d("Generate", "width " + widthInches + "   height " + heightInches + "   " + bitmapUri);

            PrintItem printItem4x6   = new ImagePrintItem(PrintAttributes.MediaSize.NA_INDEX_4X6, margins, scaleType, imageAsset);
            PrintItem printItem85x11 = new ImagePrintItem(PrintAttributes.MediaSize.NA_LETTER, margins, scaleType, imageAsset);
            PrintItem printItem5x7   = new ImagePrintItem(mediaSize5x7, margins, scaleType, imageAsset);


            printJobData = new PrintJobData(this, printItem4x6);
            printJobData.addPrintItem(printItem85x11);
            printJobData.addPrintItem(printItem5x7);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* PRINT API RELATED */
    private String getMimeType(Uri uri) {
        Uri returnUri = uri;
        return getContentResolver().getType(returnUri);
    }

    /* PRINT API RELATED */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    bitmapUri = data.getData();
                    showFileInfo(bitmapUri);
                }
                break;
        }
    }

    /* PRINT API RELATED */
    private void showFileInfo(Uri uri) {

        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);

        returnCursor.moveToFirst();

        Log.d("Generate", "              " + returnCursor.getString(nameIndex) +"  "+ Long.toString(returnCursor.getLong(sizeIndex)));
        Log.d("Generate", "bitmapUri " + bitmapUri);
        Log.d("Generate", "File          " + returnCursor.getString(nameIndex) + "(" + Long.toString(returnCursor.getLong(sizeIndex)) + "0");

    }

    /* PRINT API RELATED */
    private String getContentMimeType() {
        String mimeType = MIME_TYPE_IMAGE;

        if (contentType == CONTENT_TYPE_PDF)
            mimeType = MIME_TYPE_PDF;

        return mimeType;
    }

    public void continueButtonClicked(View v) {


        createPrintJobData();
        PrintUtil.setPrintJobData(printJobData);

        PrintUtil.sendPrintMetrics = showMetricsDialog;
        PrintUtil.print(this);

    }

    private void createPrintJobData() {

        Log.d("Generate", "1 --------> " + bitmapUri);


        if (bitmapUri != null && getMimeType(bitmapUri).startsWith(MIME_TYPE_IMAGE_PREFIX) && contentType == CONTENT_TYPE_IMAGE) {
            Log.d("Generate", "1 -------->");
        } else if (bitmapUri != null && getMimeType(bitmapUri).equals(MIME_TYPE_PDF) && contentType == CONTENT_TYPE_PDF) {
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

        if(!b){
            txtTitleGeneratedKey.setText("");
            txtGeneratedCustomerKey.setText("");
        }

        txtTitleGeneratedKey.setVisibility(b == true ? View.VISIBLE : View.INVISIBLE);
        txtGeneratedCustomerKey.setVisibility(b == true ? View.VISIBLE : View.INVISIBLE);
        btnPrint.setVisibility(b == true ? View.VISIBLE : View.INVISIBLE);

    }

    @Override
    public void onBackPressed() {
        hideTextFields(false);
        super.onBackPressed();
    }

    /* PRINT API RELATED */
    @Override
    public void onPrintMetricsDataPosted(PrintMetricsData printMetricsData) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(printMetricsData.toMap().toString());
        builder.setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        builder.create().show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){


            case R.id.btnGenerateQR :

                    Log.d("Generate", "state " + v.getTag());
                    if (v.getTag() == null || !v.getTag().toString().equalsIgnoreCase(STATE_READY_TO_SAVE)) {

                        presenter.doGenerateQr(txtScaleId.getText().toString());
                        //generateCostumerKey();
                        v.setTag(STATE_READY_TO_SAVE);

                        hideTextFields(true);

                        btnGenerateQR.setVisibility(View.INVISIBLE);


                        new AlertDialog.Builder(this)
                                .setTitle(getString(R.string.txt_save_qr))
                                .setMessage(getString(R.string.txt_save_customer_qr_msg))
                                .setPositiveButton("YES", (dialog, which) -> {
                                    presenter.doAskUserBeforeSaving(true);
                                    v.setTag(null);

                                    qrImage.setImageBitmap(null);
                                    hideTextFields(false);
                                })
                                .setNegativeButton("NO", (dialog, which) -> {
                                    v.setTag(null);
                                    DoSnack(getString(R.string.txt_qr_didnt_get_saved));
                                    qrImage.setImageBitmap(null);
                                    hideTextFields(false);
                                })
                                .show();

                    }

                break;

            case R.id.btnPrint :

                continueButtonClicked(v);
                v.setVisibility(View.INVISIBLE);

                break;

        }
    }

    @Override
    public void Message(int t, String s) {

        switch (t){
            case Constants.SUCCESS_MSG :


                break;

            case Constants.ERROR_MSG :


                break;
        }

    }

    @Override
    public void SetBitmap(Bitmap bitmap) {
        qrImage.setImageBitmap(bitmap);
    }

    @Override
    public void SetBitMapUri(Uri uri) {
        bitmapUri = uri;
    }

    @Override
    public void SetCustomerKey(String key) {
        txtCustomerKey.setText(key);
    }

    @Override
    public void SetKeyWasSaved() {

        btnGenerateQR.setVisibility(View.VISIBLE);
        DoSnack(getString(R.string.txt_saving_customer_qr_to_database));

    }

    @Override
    public void SetKeyWasNotSaved() {
        btnGenerateQR.setVisibility(View.VISIBLE);
    }
}
