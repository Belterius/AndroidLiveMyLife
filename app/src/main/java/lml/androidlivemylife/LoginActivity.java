package lml.androidlivemylife;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import API_request.MySingletonRequestApi;
import ClassPackage.GlobalState;
import ClassPackage.MyUser;
import API_request.RequestClass;
import ClassPackage.Step;
import ClassPackage.Story;
import ClassPackage.ToastClass;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editEmail;
    private TextInputEditText editPassword;
    final public String TAG = "login";
    private GlobalState gs;//Loading
    private AVLoadingIndicatorView loader;

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

        this.loader = (AVLoadingIndicatorView) findViewById(R.id.login_gif);
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (MySingletonRequestApi.getInstance(this).getRequestQueue() != null) {
            MySingletonRequestApi.getInstance(this).getRequestQueue().cancelAll(TAG);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(gs.getConnected()){
            goToMainPage();
        }
    }

    /**
     * Tries to sign in
     * @param v
     * @return success or failure
     */
    public Boolean signIn(View v){

        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        if(email.equals("") || password.equals("")){
            ToastClass.toastError(this, getString(R.string.error_empty_field));
            return false;
        }

        String action = "signin";

        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", action);
        dataToPass.put("email", email);
        dataToPass.put("password", password);

        //Avoid doing the request
        if(this.gs.getConnected() && email.equals(this.gs.getMyAccount().getEmail())){
            goToMainPage();
        }else{

            findViewById(R.id.login_background_gif).setVisibility(View.VISIBLE);
            loader.smoothToShow();
            loader.bringToFront();

            RequestClass.doRequestWithApi(this.getApplicationContext(), this.TAG, dataToPass, this::getMyAccount);
        }

        return true;
    }

    /**
     * Creates a new Activity to register
     * @param v
     */
    public void goToRegister(View v){
        Intent nextView = new Intent(this,RegisterActivity.class);
        nextView.putExtra("email",this.editEmail.getText().toString());
        startActivity(nextView);
    }

    /**
     * Gets the response from the API server to login
     * @param o
     * @return
     */
    public Boolean getMyAccount(JSONObject o){

        try {
            loader.smoothToHide();
            findViewById(R.id.login_background_gif).setVisibility(View.GONE);

            if(o.getInt("status") == 200 && o.getJSONObject("user") != null){

                JSONObject user = o.getJSONObject("user");

                if(user != null){
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

                    JSONObject myCurrentStory = user.getJSONObject("myCurrentStory");
                    if(myCurrentStory != null){
                        this.gs.getMyAccount().setMyCurrentStory(new Story(
                                myCurrentStory.getString("id")
                        ));

                        JSONArray mySteps = myCurrentStory.getJSONArray("steps");
                        if(mySteps != null){

                            for (int i = 0; i < mySteps.length(); i++) {
                                JSONObject step = mySteps.getJSONObject(i);
                                this.gs.getMyAccount().getMyCurrentStory().addStep(
                                        new Step(
                                                step.getString("stepId"),
                                                step.getString("stepPicture"),
                                                step.getString("stepGpsLongitude"),
                                                step.getString("stepGpsLatitude"),
                                                step.getString("stepDescription")
                                        )
                                );
                            }

                        }
                    }

                    goToMainPage();
                    return true;
                }

            }else{
                ToastClass.toastError(this, o.getString("feedback"));
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
