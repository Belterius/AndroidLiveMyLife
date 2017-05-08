package lml.androidlivemylife;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Function;

import ClassPackage.Personne;
import asyncRequest.RestActivity;

public class LoginActivity extends RestActivity {

    private TextInputEditText editEmail;
    private TextInputEditText editPassword;
    final public String TAG = "login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = (TextInputEditText) findViewById(R.id.login_email);
        editPassword = (TextInputEditText) findViewById(R.id.login_password);

        editEmail.setText("email");
        editPassword.setText("test");

        gs = (GlobalState) getApplication();

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
            this.gs.doRequestWithApi(this.TAG, qs, this::getMyAccount);
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

    public Boolean getMyAccount(JSONObject o){

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
                return true;
            }else{
                this.toastError(o.getString("feedback"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void goToMainPage(){
        Intent nextView = new Intent(this,LocalStoriesActivity.class);
        startActivity(nextView);
        this.finish();
    }

}
