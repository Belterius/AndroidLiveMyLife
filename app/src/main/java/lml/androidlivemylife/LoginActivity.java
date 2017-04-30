package lml.androidlivemylife;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.os.Bundle;
import android.view.View;

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
        Intent nextView = new Intent(this,LocalStoriesActivity.class);
        startActivity(nextView);
        this.finish();
    }

}
