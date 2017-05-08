package lml.androidlivemylife;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import ClassPackage.GlobalState;
import ClassPackage.Personne;

public class RegisterActivity extends AppCompatActivity {

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


        Bundle b = this.getIntent().getExtras();
        this.editEmail.setText(b.getString("email"));

        gs = new GlobalState();
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

        this.gs.doRequestWithApi(this.getApplicationContext(), this.TAG, qs, this::postRequest);

        return true;
    }

    public Boolean postRequest(JSONObject o) {
        try {

            JSONObject user = o.getJSONObject("user");
            if( o.getInt("status") == 200 && user != null ){

                this.gs.setMyAccount(new Personne(
                        user.getString("id"),
                        user.getString("email"),
                        user.getString("pseudo"),
                        user.getString("firstname"),
                        user.getString("lastname"),
                        user.getString("description"),
                        user.getString("photo")
                ));

                this.gs.setConnected(true);
                finish();
                return true;

            }else{
                this.gs.toastError(this, o.getString("feedback"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }
}
