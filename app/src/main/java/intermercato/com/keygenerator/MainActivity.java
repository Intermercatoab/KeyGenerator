package intermercato.com.keygenerator;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import intermercato.com.keygenerator.ui.GenerateKey;
import intermercato.com.keygenerator.ui.Storedkeys;
import intermercato.com.keygenerator.ui.ValidateKey;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
}
