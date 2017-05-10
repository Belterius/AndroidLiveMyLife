package lml.androidlivemylife;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

}
