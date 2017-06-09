package lml.androidlivemylife;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import API_request.MySingletonRequestApi;
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
        setChoosePictureFromGalleryButton(R.id.register_button_picture_from_gallery);
        setTakePictureButton(R.id.register_button_take_new_picture);

        Bundle b = this.getIntent().getExtras();
        this.editEmail.setText(b.getString("email"));

        gs = new GlobalState();

        requestEveryPermission();
    }

    /**
     * Click on register
     * @param v
     * @return success or failure
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
            ToastClass.toastError(this, getString(R.string.error_passwords_and_confirm));
            return false;
        }

        if(email.equals("") || pseudo.equals("") || firstname.equals("") || lastname.equals("") || description.equals("") || password.equals("") || passwordConfirm.equals("")){
            ToastClass.toastError(this, getString(R.string.error_fill_field));
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
        dataToPass.put("photo", bitmap != null ? getImageToPassForRequest() : "");

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
            if( o.getInt("status") == 200){

                if(user != null ){
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
                }

            }
            //Account created but problem with the picture
            else if (o.getInt("status") == 202){

                if(user != null){
                    this.gs.setMyAccount(new MyUser(
                            user.getString("id"),
                            user.getString("email"),
                            user.getString("pseudo"),
                            user.getString("firstname"),
                            user.getString("lastname"),
                            user.getString("description"),
                            "",
                            new Story(user.getJSONObject("myCurrentStory").getString("id"))
                    ));

                    ToastClass.toastError(this, o.getString("feedback"));
                    this.gs.setConnected(true);
                    finish();
                    return true;
                }
            }else {
                ToastClass.toastError(this, o.getString("feedback"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (MySingletonRequestApi.getInstance(this).getRequestQueue() != null) {
            MySingletonRequestApi.getInstance(this).getRequestQueue().cancelAll(TAG);
        }
    }
}
