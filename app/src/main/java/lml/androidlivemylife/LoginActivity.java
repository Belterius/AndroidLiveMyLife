package lml.androidlivemylife;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.TextInputEditText;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import API_request.MySingleton;
import ClassPackage.GlobalState;
import ClassPackage.MyUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editEmail;
    private TextInputEditText editPassword;
    final public String TAG = "login";
    private GlobalState gs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = (TextInputEditText) findViewById(R.id.login_email);
        editPassword = (TextInputEditText) findViewById(R.id.login_password);

        editEmail.setText("email");
        editPassword.setText("test");
        checkLocationPermission();
        gs = new GlobalState();
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (MySingleton.getInstance(this).getRequestQueue() != null) {
            MySingleton.getInstance(this).getRequestQueue().cancelAll(TAG);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(gs.getConnected()){
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
        if(this.gs.getConnected() && email.equals(this.gs.getMyAccount().getEmail())){
            goToMainPage();
        }else{
            this.gs.doRequestWithApi(this.getApplicationContext(), this.TAG, qs, this::getMyAccount);
        }

    }

    public void goToRegister(View v){
        Intent nextView = new Intent(this,RegisterActivity.class);
        nextView.putExtra("email",this.editEmail.getText().toString());
        startActivity(nextView);
    }

    public Boolean getMyAccount(JSONObject o){

        try {
            JSONObject user = o.getJSONObject("user");

            if(o.getInt("status") == 200 && user != null){

                this.gs.setConnected(true);

                this.gs.setMyAccount(new MyUser(
                        user.getString("id"),
                        editEmail.getText().toString(),
                        user.getString("pseudo"),
                        user.getString("firstname"),
                        user.getString("lastname"),
                        user.getString("description"),
                        user.getString("photo")
                ));

                goToMainPage();
                return true;
            }else{
                this.gs.toastError(this, o.getString("feedback"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void goToMainPage(){
        Intent nextView = new Intent(this,MainActivity.class);
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
