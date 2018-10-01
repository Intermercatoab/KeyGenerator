package intermercato.com.keygenerator.ui.validate;

import android.graphics.Color;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;


import eu.livotov.labs.android.camview.ScannerLiveView;

import intermercato.com.keygenerator.adapters.BankOnClickCallBack;
import intermercato.com.keygenerator.adapters.DividerDecoration;
import intermercato.com.keygenerator.adapters.mValidateRecyclerViewAdapter;
import intermercato.com.keygenerator.utils.AESEnDecryption;
import intermercato.com.keygenerator.R;
import intermercato.com.keygenerator.utils.DataHandler;
import io.realm.Realm;


public class ValidateKey extends AppCompatActivity implements ScannerLiveView.ScannerViewEventListener, BankOnClickCallBack, ValidateContract.View {

    private String fakeKey = "+kbqe40jQhs+CR5AKmAuRBYFQ6NB/YauOnZNIlXik+1RYHRYA2Pg4O2GLFeMbIi0";
    private ScannerLiveView scannerLiveView;
    private TextView txtValid;
    private TextView txt_info;
    private RecyclerView rv;
    private mValidateRecyclerViewAdapter mAdapter;
    private ValidateContract.Presenter presenter;
    private Realm realm;
    private String SCALE_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_validate_key);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        txt_info = findViewById(R.id.txt_info);
        // CHECK TO SEE THAT SCALE ID HAS BEEN SET

        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            scannerLiveView.startScanner();
        });

        fab.setBackgroundColor(Color.parseColor("#f3f3f3"));
        presenter = new ValidatePresenter(this);

        // init recyclerView and adapter

        int[] metrics = getScreenWidthAndHeight();
        int width = metrics[0];
        int height = metrics[1];
        int minusVal = 0;
        if (height > 1000) {
            minusVal = 280;
        } else {
            minusVal = 200;
        }
        int layoutHeight = (int) (height * 0.40) - minusVal;
        Log.d("Grider", " height: " + height + " width: " + width);


        rv = findViewById(R.id.rvValidate);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mAdapter = new mValidateRecyclerViewAdapter(presenter.getRealmResult(), this);
        mAdapter.setClickListener(this);
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new DividerDecoration(this, R.drawable.list_divider));

        rv.setAdapter(mAdapter);
        rv.setHasFixedSize(true);


        txtValid = findViewById(R.id.txtValid);
        scannerLiveView = findViewById(R.id.scanner);
        scannerLiveView.setScannerViewEventListener(this);
    }

    private int[] getScreenWidthAndHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return new int[]{width, height};
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onScannerStarted(ScannerLiveView scanner) {
        Log.d("Validate", "onScannerStarted");
    }

    @Override
    public void onScannerStopped(ScannerLiveView scanner) {
        Log.d("Validate", "onScannerStopped");
    }

    @Override
    public void onScannerError(Throwable err) {
        Log.d("Validate", "onScannerError");
    }

    @Override
    public void onCodeScanned(String data) {
        Log.d("Validate", "onCodeScanned " + data);
        validateData(data);

    }

    private void validateData(String data) {

        String QrKey = data;
        String customerKey = "";

        String s = "\nValid with scaleId ";

        if (TextUtils.isEmpty(SCALE_ID)) {
            txt_info.setText("Key is missing - Select ");
            return;
        }

        String scaleId = s+getScaleId();

        try {

            String deansBase64 = AESEnDecryption.decryptStrAndFromBase64(DataHandler.IVSTR, getScaleId(), QrKey);
            Log.d("Validate", "After Decrypt & From Base64: " + deansBase64);
            Log.d("Valida", "                                 ");
            customerKey = AESEnDecryption.encryptStrAndToBase64(DataHandler.IVSTR, getScaleId(), deansBase64);
            Log.d("Validate", "QrKey: " + QrKey + "   ==   " + customerKey);

        } catch (Exception e) {

        }

        if (customerKey.toUpperCase().trim().equals(QrKey.trim().toUpperCase())) {
            txtValid.setText(customerKey + scaleId);
        } else {
            txtValid.setText("Invalid key!");
        }
    }

    @Override
    public void onClick(String id) {
        Log.d("Validate", "onClick " + id);
        setScaleId(id);
        txt_info.setText("Scale id loaded");
    }

    private void setScaleId(String id) {
        SCALE_ID = id;
    }

    private String getScaleId() {
        return SCALE_ID;
    }
}
