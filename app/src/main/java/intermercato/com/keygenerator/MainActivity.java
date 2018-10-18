package intermercato.com.keygenerator;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;



import intermercato.com.keygenerator.ui.GenerateKey;
import intermercato.com.keygenerator.ui.Storedkeys;
import intermercato.com.keygenerator.ui.ValidateKey;

import static intermercato.com.keygenerator.ui.GenerateKey.CONTENT_TYPE_PDF;
import static intermercato.com.keygenerator.ui.GenerateKey.MIME_TYPE_IMAGE;
import static intermercato.com.keygenerator.ui.GenerateKey.MIME_TYPE_PDF;

public class   MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnCreateKey).setOnClickListener(this);
        findViewById(R.id.btnValidate).setOnClickListener(this);
        findViewById(R.id.btnStoredKeys).setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnCreateKey :

                Intent intentGenerateKey = new Intent(MainActivity.this, GenerateKey.class);
                startActivity(intentGenerateKey);

                break;

            case R.id.btnValidate :

                Intent intentValidateKey = new Intent(MainActivity.this, ValidateKey.class);
                startActivity(intentValidateKey);
                break;

            case R.id.btnStoredKeys :

                Intent intentStoredKeys = new Intent(MainActivity.this, Storedkeys.class);
                startActivity(intentStoredKeys);

                break;

        }
    }

    Uri userPickedUri;
    String contentType = "";

    static final int PICKFILE_RESULT_CODE = 1;
    private String getContentMimeType() {
        String mimeType = MIME_TYPE_IMAGE;

        if (contentType == CONTENT_TYPE_PDF)
            mimeType = MIME_TYPE_PDF;

        return mimeType;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Log.d("Main"," "+requestCode+"    "+resultCode);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    userPickedUri = data.getData();
                    Log.d("Generate", "userPickedUri " + userPickedUri);
                    showFileInfo(userPickedUri);

                }
                break;
        }
    }


    private void showFileInfo(Uri uri) {
        Cursor returnCursor = this.getContentResolver().query(uri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);

//        Log.d("Generate", " "+returnCursor.getString(nameIndex) +"  "+ Long.toString(returnCursor.getLong(sizeIndex)));
        returnCursor.moveToFirst();

        Toast.makeText(this,
                "File " + returnCursor.getString(nameIndex) + "(" + Long.toString(returnCursor.getLong(sizeIndex)) + "0",
                Toast.LENGTH_LONG).show();

    }
}
