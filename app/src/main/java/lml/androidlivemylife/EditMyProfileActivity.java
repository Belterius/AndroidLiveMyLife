package lml.androidlivemylife;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

import ClassPackage.Personne;
import asyncRequest.RestActivity;

public class EditMyProfileActivity extends RestActivity {

    private EditText editPseudo;
    private ImageView editPhoto;
    EditText editDescription;

    private int nbWaitingUpdating;

    @Override
    public void postRequest(JSONObject o, String action) {
        switch (action){
            case "updatePseudo" :
                try {
                    if(o.getInt("status") == 200){
                        this.gs.myAccount.setPseudo(editPseudo.getText().toString());
                        nbWaitingUpdating--;
                    }else{
                        this.toastError(o.getString("feedback"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "updateDescriptionUser" :
                try {
                    if(o.getInt("status") == 200){
                        this.gs.myAccount.setDescription(editDescription.getText().toString());
                        nbWaitingUpdating--;
                    }else{
                        this.toastError(o.getString("feedback"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }

        tryToFinish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_profile);

        editPseudo = (EditText) findViewById(R.id.my_profile_pseudo_edit);
        editPhoto = (ImageView) findViewById(R.id.my_profile_picture_edit);
        editDescription = (EditText) findViewById(R.id.my_profile_bio_edit);

        Bundle b = this.getIntent().getExtras();
        this.editPseudo.setText(b.getString("pseudo"));
        this.editDescription.setText(b.getString("description"));

        //TODO : Faire pour la photo
        //this.editPhoto.setText(b.getString("photo"));
    }

    public void updateMyPersonalData(View v){

        String new_pseudo = editPseudo.getText().toString();
        String new_description = editDescription.getText().toString();

        String qs = "";
        String action = "";

        if(! new_pseudo.equals(this.gs.myAccount.getPseudo())){
            action = "updatePseudo";

            qs = "action=" + action
                    + "&pseudo=" + new_pseudo;

            sendRequest(qs, action);
            nbWaitingUpdating++;
        }

        if(! new_description.equals(this.gs.myAccount.getDescription())){
            action = "updateDescriptionUser";

            qs = "action=" + action
                    + "&description=" + new_description;

            sendRequest(qs, action);
            nbWaitingUpdating++;
        }

        /**TODO : update photo**/
        tryToFinish();
    }

    public void tryToFinish(){
        if(nbWaitingUpdating == 0){
            this.finish();
        }
    }
}
