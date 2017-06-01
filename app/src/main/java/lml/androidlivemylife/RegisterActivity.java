package lml.androidlivemylife;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ClassPackage.GlobalState;
import ClassPackage.MyUser;
import API_request.RequestClass;
import ClassPackage.Story;
import ClassPackage.ToastClass;
import ExtendedPackage.UploadPictureActivity;

public class RegisterActivity extends UploadPictureActivity {

    private TextInputEditText editEmail;
    private TextInputEditText editPseudo;
    private TextInputEditText editFirstname;
    private TextInputEditText editLastname;
    private EditText editDescription;
    private TextInputEditText editPassword;
    private TextInputEditText editPasswordConfirm;
    final public String TAG = "register";
    private GlobalState gs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editEmail = (TextInputEditText) findViewById(R.id.register_email);
        editPseudo = (TextInputEditText) findViewById(R.id.register_pseudo);
        editFirstname = (TextInputEditText) findViewById(R.id.register_firstname);
        editLastname = (TextInputEditText) findViewById(R.id.register_lastname);
        editDescription = (EditText) findViewById(R.id.register_description);
        editPassword = (TextInputEditText) findViewById(R.id.register_password);
        editPasswordConfirm = (TextInputEditText) findViewById(R.id.register_password_confirm);

        //For extended class
        setImageViewForUploadClass(R.id.register_imageView);

        Bundle b = this.getIntent().getExtras();
        this.editEmail.setText(b.getString("email"));

        gs = new GlobalState();
    }

    /**
     * Click on register
     * @param v
     * @return
     */
    public boolean register(View v){

        String email = editEmail.getText().toString();
        String pseudo = editPseudo.getText().toString();
        String firstname = editFirstname.getText().toString();
        String lastname = editLastname.getText().toString();
        String description = editDescription.getText().toString();
        String password = editPassword.getText().toString();
        String passwordConfirm = editPasswordConfirm.getText().toString();

        if(! password.equals(passwordConfirm)){
            ToastClass.toastError(this, "Passwords are not the same !");
            return false;
        }

        String action = "register";

        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", action);
        dataToPass.put("email", email);
        dataToPass.put("pseudo", pseudo);
        dataToPass.put("password", password);
        dataToPass.put("firstname", firstname);
        dataToPass.put("lastname", lastname);
        dataToPass.put("description", description);
        dataToPass.put("photo", getImageToPassForRequest());

        RequestClass.doRequestWithApi(this.getApplicationContext(), this.TAG, dataToPass, this::postRequest);

        return true;
    }

    /**
     * Get the response from the server
     * @param o JSONObject
     * @return
     */
    public Boolean postRequest(JSONObject o) {
        try {

            JSONObject user = o.getJSONObject("user");
            if( o.getInt("status") == 200 && user != null ){

                this.gs.setMyAccount(new MyUser(
                        user.getString("id"),
                        user.getString("email"),
                        user.getString("pseudo"),
                        user.getString("firstname"),
                        user.getString("lastname"),
                        user.getString("description"),
                        user.getString("photo"),
                        new Story(user.getJSONObject("myCurrentStory").getString("id"))
                ));

                this.gs.setConnected(true);
                finish();
                return true;

            }else {
                ToastClass.toastError(this, o.getString("feedback"));
            }

            //Account created but problem with the picture
            if (o.getInt("status") == 202 && user != null){

                    this.gs.setMyAccount(new MyUser(
                            user.getString("id"),
                            user.getString("email"),
                            user.getString("pseudo"),
                            user.getString("firstname"),
                            user.getString("lastname"),
                            user.getString("description"),
                            user.getString(""),
                            new Story(user.getJSONObject("myCurrentStory").getString("id"))
                    ));

                    ToastClass.toastError(this, o.getString("feedback"));
                    this.gs.setConnected(true);
                    finish();
                    return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }
}
