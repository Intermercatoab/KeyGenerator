package intermercato.com.keygenerator;


import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.View;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import intermercato.com.keygenerator.ui.PermissionsCheckActivity;
import intermercato.com.keygenerator.ui.Storedkeys;
import intermercato.com.keygenerator.ui.generate.GenerateKey;

import intermercato.com.keygenerator.ui.validate.ValidateKey;
import intermercato.com.keygenerator.utils.AESEnDecryption;
import intermercato.com.keygenerator.utils.DataHandler;


public class MainActivity extends PermissionsCheckActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnCreateKey).setOnClickListener(this);
        findViewById(R.id.btnValidate).setOnClickListener(this);
        findViewById(R.id.btnStoredKeys).setOnClickListener(this);
        String str2=getString(R.string.app_name);


        Log.d("Main","testStr locallib "+str2);

        String customerKey="";
        String genKey = "886B0F69E0C5";
        String scaleId = "WE";//"886B0F69E0C5";
        String encrypted="";
        String decrypted="";
        String kund = "886B0F69E0C5:LATIN";
        try {
            // ENCRYPT KEY
            Log.d("Generate", "Before Encrypt: " + kund);

            encrypted = AESEnDecryption.encryptStrAndToBase64(DataHandler.IVSTR, scaleId, kund);
            Log.d("Generate", "After Encrypt: " + encrypted);

            decrypted = AESEnDecryption.decryptStrAndFromBase64(DataHandler.IVSTR, scaleId, encrypted);
            Log.d("Generate", "After decrypt: " + decrypted);

            encrypted = AESEnDecryption.encryptStrAndToBase64(DataHandler.IVSTR, scaleId, decrypted);
            Log.d("Generate", "After Encrypt: " + encrypted);

        } catch (Exception e) {

        }

        String string_to_be_converted_to_MD5 = "65b80";
        String MD5_Hash_String = md5(string_to_be_converted_to_MD5);
        Log.d("md5","1  "+ MD5_Hash_String);
        Log.d("md5","2   "+ md5(string_to_be_converted_to_MD5));
        Log.d("md5","3   "+ md5("65b80"));

    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnCreateKey:

                Intent intentGenerateKey = new Intent(MainActivity.this, GenerateKey.class);
                startActivity(intentGenerateKey);
                break;

            case R.id.btnValidate:

                Intent intentValidateKey = new Intent(MainActivity.this, ValidateKey.class);
                startActivity(intentValidateKey);
                break;

            case R.id.btnStoredKeys:

                Intent intentStoredKeys = new Intent(MainActivity.this, Storedkeys.class);
                startActivity(intentStoredKeys);
                break;
        }
    }
}
