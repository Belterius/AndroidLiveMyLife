package lml.androidlivemylife;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

    public void signIn(View v){

        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String action = "signin";

        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", action);
        dataToPass.put("email", email);
        dataToPass.put("password", password);

        //Avoid doing the request
        if(this.gs.getConnected() && email.equals(this.gs.getMyAccount().getEmail())){
            goToMainPage();
        }else{
            RequestClass.doRequestWithApi(this.getApplicationContext(), this.TAG, dataToPass, this::getMyAccount);
        }
    }

    public void goToRegister(View v){
        Intent nextView = new Intent(this,RegisterActivity.class);
        nextView.putExtra("email",this.editEmail.getText().toString());
        startActivity(nextView);
    }

    public Boolean getMyAccount(JSONObject o){

        try {

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
                                                step.getString("stepGpsData"),
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

}
