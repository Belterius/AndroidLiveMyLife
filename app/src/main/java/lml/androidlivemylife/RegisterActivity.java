package lml.androidlivemylife;

import android.support.design.widget.TextInputEditText;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import ClassPackage.Personne;
import asyncRequest.RestActivity;

public class RegisterActivity extends RestActivity {

    private TextInputEditText editEmail;
    private TextInputEditText editPseudo;
    private TextInputEditText editFirstname;
    private TextInputEditText editLastname;
    private EditText editDescription;
    private TextInputEditText editPassword;
    private TextInputEditText editPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editEmail = (TextInputEditText) findViewById(R.id.register_email);
        editFirstname = (TextInputEditText) findViewById(R.id.register_firstname);
        editLastname = (TextInputEditText) findViewById(R.id.register_lastname);
        editDescription = (EditText) findViewById(R.id.register_description);
        editPassword = (TextInputEditText) findViewById(R.id.register_password);
        editPasswordConfirm = (TextInputEditText) findViewById(R.id.register_password_confirm);


        Bundle b = this.getIntent().getExtras();
        this.editEmail.setText(b.getString("email"));
    }

    public boolean register(View v){

        String email = editEmail.getText().toString();
        String pseudo = editPseudo.getText().toString();
        String firstname = editFirstname.getText().toString();
        String lastname = editLastname.getText().toString();
        String description = editDescription.getText().toString();
        String password = editPassword.getText().toString();
        String passwordConfirm = editPasswordConfirm.getText().toString();

        if(! password.equals(passwordConfirm)){
            return false;
        }

        String action = "register";

        String qs = "action=" + action
                + "&email=" + email
                + "&pseudo=" + pseudo
                + "&password=" +password
                + "&firstname=" +firstname
                + "&lastname=" +lastname
                + "&description=" +description
                + "&photo=" + "defaultPicture.png";

        sendRequest(qs,action);

        return true;
    }

    @Override
    public void postRequest(JSONObject o, String action) {
        switch (action){
            case "register" :
                try {

                    JSONObject user = o.getJSONObject("user");
                    if( o.getInt("status") == 200 && user != null ){

                        this.gs.myAccount = new Personne(
                                user.getString("id"),
                                editEmail.getText().toString(),
                                user.getString("pseudo"),
                                user.getString("firstname"),
                                user.getString("lastname"),
                                user.getString("description"),
                                user.getString("photo")
                        );

                        this.gs.connected = true;

                        finish();
                    }else{
                        this.toastError(o.getString("feedback"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
