package lml.androidlivemylife;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

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
                + "&password=" +password
                + "&firstname=" +password
                + "&lastname=" +password
                + "&description=" +password
                + "&photo=" + "defaultPicture.png";

        sendRequest(qs,action);

        return true;
    }

    @Override
    public void postRequest(JSONObject o, String action) {
        switch (action){
            case "register" :
                try {
                    if( Integer.toString(o.getInt("idUser")) !=  null){

                        this.gs.idUser =  o.getInt("idUser");
                        this.gs.connected = true;

                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
