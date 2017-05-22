package lml.androidlivemylife;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.TextInputEditText;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ClassPackage.Personne;
import asyncRequest.RestActivity;

public class LoginActivity extends RestActivity {

    private TextInputEditText editEmail;
    private TextInputEditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = (TextInputEditText) findViewById(R.id.login_email);
        editPassword = (TextInputEditText) findViewById(R.id.login_password);

        editEmail.setText("email");
        editPassword.setText("test");

        gs = (GlobalState) getApplication();
        checkLocationPermission();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(this.gs.connected){
            goToMainPage();
        }
    }

    public void signIn(View v){

        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String action = "signin";

        String qs = "action=" + action
                + "&email=" + email
                + "&password=" +password;

        //Avoid doing the request
        if(this.gs.connected && email.equals(this.gs.myAccount.getEmail())){
            goToMainPage();
        }else{
            sendRequest(qs,action);
        }

    }

    public void goToRegister(View v){
        Intent nextView = new Intent(this,RegisterActivity.class);
        nextView.putExtra("email",this.editEmail.getText().toString());
        startActivity(nextView);
    }

    @Override
    public void postRequest(JSONObject o, String action) {
        switch (action){
            case "signin" :
                try {

                    JSONObject user = o.getJSONObject("user");
                    if(o.getInt("status") == 200 && user != null){

                        this.gs.connected = true;

                        this.gs.myAccount = new Personne(
                                user.getString("id"),
                                editEmail.getText().toString(),
                                user.getString("pseudo"),
                                user.getString("firstname"),
                                user.getString("lastname"),
                                user.getString("description"),
                                user.getString("photo")
                        );

                        goToMainPage();
                    }else{
                       this.toastError(o.getString("feedback"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void goToMainPage(){
        Intent nextView = new Intent(this,MapsActivity.class);
        startActivity(nextView);
        this.finish();
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }
}
