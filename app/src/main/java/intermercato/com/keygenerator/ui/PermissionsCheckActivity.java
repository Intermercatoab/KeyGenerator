package intermercato.com.keygenerator.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import intermercato.com.keygenerator.R;


public class PermissionsCheckActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 666;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("PermissionsCheck", "prefs displayInfo " + prefs.getBoolean("displayInfo", true));
        if (Build.VERSION.SDK_INT >= 23) {
            if (prefs.getBoolean("displayInfo", true)) {
                showDialogOK(getString(R.string.txt_message_permissions_needed), dialogClickListener);
            }
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    Log.d("PERMISSIONSCHECK", "Clicked OK");
                    doCheckForPermissions();
                    break;

            }
        }
    };


    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(PermissionsCheckActivity.this)
                .setTitle(R.string.txt_dialog_title_important)
                .setMessage(message)
                .setPositiveButton(getString(R.string.txt_positive_button_title_ok), okListener)
                .create()
                .show();
    }

    private boolean doCheckForPermissions() {
        Log.d("PERMISSIONSCHECK", "DoCheckForPermission");
        int locationPermission = ActivityCompat.checkSelfPermission(PermissionsCheckActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int locationCoarse = ActivityCompat.checkSelfPermission(PermissionsCheckActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int writeExternalStorage = ActivityCompat.checkSelfPermission(PermissionsCheckActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int usesCamera = ActivityCompat.checkSelfPermission(PermissionsCheckActivity.this, Manifest.permission.CAMERA);

        Log.d("PERMISSIONSCHECK", "locationPermission " + locationPermission + " locationCoarse " + locationCoarse + " writeExternalStorage " + writeExternalStorage + " usesCamera " + usesCamera);

        final List<String> listPermissionsNeeded = new ArrayList<>();

        if(ContextCompat.checkSelfPermission(PermissionsCheckActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.d("PERMISSIONSCHECK", "locationPermission not granted");
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (locationCoarse != PackageManager.PERMISSION_GRANTED) {
            Log.d("PERMISSIONSCHECK", "locationCoarse not granted");
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (writeExternalStorage != PackageManager.PERMISSION_GRANTED) {
            Log.d("PERMISSIONSCHECK", "writeExternalStorage not granted");
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (usesCamera != PackageManager.PERMISSION_GRANTED) {
            Log.d("PERMISSIONSCHECK", "usesCamera not granted");
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            Log.d("PERMISSIONSCHECK", "listpermissionsneeded not empty");
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d("PERMISSIONSCHECK", "rationale");
                // Explain to the user why permission is required, then request again
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("We need permissions")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityCompat.requestPermissions(PermissionsCheckActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            } else {
                Log.d("PERMISSIONSCHECK", "request" + Manifest.permission.ACCESS_FINE_LOCATION);
                // If permission has not been denied before, request the permission
                ActivityCompat.requestPermissions(PermissionsCheckActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }

            //ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;

        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("displayInfo", false).apply();




        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.d("Controller", "onRequestPermissionsResult");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                        Log.d("Controller", "ACCESS_COARSE_LOCATION ACCESS_FINE_LOCATION WRITE_EXTERNAL_STORAGE CAMERA permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("displayInfo", false).apply();


                    } else {
                        Log.d("Controller", "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                            showDialogOK(getString(R.string.txt_message_permissions_needed),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    doCheckForPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, getString(R.string.txt_message_permissions_not_granted), Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.

                            startInstalledAppDetailsActivity(PermissionsCheckActivity.this);
                        }
                    }
                }
            }
        }

    }

    public static void startInstalledAppDetailsActivity(final Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }
}
