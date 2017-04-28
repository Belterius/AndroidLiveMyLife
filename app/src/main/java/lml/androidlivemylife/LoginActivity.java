package lml.androidlivemylife;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

public class LoginActivity extends RestActivity {

    private EditText editEmail;
    private EditText editPasse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = (EditText) findViewById(R.id.email);
        editPasse = (EditText) findViewById(R.id.password);

        gs = (GlobalState) getApplication();


    }

    public void signIn(View v){

        String email = editEmail.getText().toString();
        String password = editPasse.getText().toString();
        String qs = "action=connect&email="
                + email + "&password=" +password;

        // On utilise la méthode envoiRequete
        // proposée par la classe RestActivity
        envoiRequete(qs,"connect");

    }

    @Override
    public void traiteReponse(JSONObject o, String action) {
        switch (action){
            case "signin" :
                //if(o.)
                //this.gs.alerter(o.toString());
                Log.i("login", o.toString());
            break;
        }
    }

}
