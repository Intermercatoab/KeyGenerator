package intermercato.com.keygenerator.ui;

import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import eu.livotov.labs.android.camview.ScannerLiveView;

import intermercato.com.keygenerator.utils.AESEnDecryption;
import intermercato.com.keygenerator.R;
import intermercato.com.keygenerator.utils.DataHandler;


public class ValidateKey extends AppCompatActivity implements ScannerLiveView.ScannerViewEventListener {

    private String fakeKey = "+kbqe40jQhs+CR5AKmAuRBYFQ6NB/YauOnZNIlXik+1RYHRYA2Pg4O2GLFeMbIi0";
    private ScannerLiveView scannerLiveView;
    private TextView txtValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_key);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        // CHECK TO SEE THAT SCALE ID HAS BEEN SET

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                scannerLiveView.startScanner();
            }
        });
        txtValid = findViewById(R.id.txtValid);
        scannerLiveView = findViewById(R.id.scanner);
        scannerLiveView.setScannerViewEventListener(this);
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
        String s = "\n\nKey is valid with scale id 886B0F69E0C5 ";
        try {
            String deansBase64 = AESEnDecryption.decryptStrAndFromBase64(DataHandler.IVSTR, "886B0F69E0C5", data);
            Log.d("Validate", "After Decrypt & From Base64: " + deansBase64);

            Log.d("Valida", "                                 ");
            customerKey = AESEnDecryption.encryptStrAndToBase64(DataHandler.IVSTR, "886B0F69E0C5", deansBase64);

            Log.d("Validate", "QrKey: " + QrKey + "   ==   " + customerKey);

        } catch (Exception e) {

        }
        if (customerKey.toUpperCase().trim().equals(QrKey.trim().toUpperCase())) {

            txtValid.setText("CustomerKey\n\n" + customerKey + s);

        } else {

            txtValid.setText("Invalid key!");

        }
    }
}
