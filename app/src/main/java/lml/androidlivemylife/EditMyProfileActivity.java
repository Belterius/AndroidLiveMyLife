package lml.androidlivemylife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import ClassPackage.GlobalState;

public class EditMyProfileActivity extends AppCompatActivity {

    private EditText editPseudo;
    private ImageView editPhoto;
    EditText editDescription;

    private int nbWaitingUpdating;
    private GlobalState gs;

    final public String TAG = "editMyProfile";

    private Boolean updatePseudo(JSONObject o){
        try {
            if(o.getInt("status") == 200){
                this.gs.getMyAccount().setPseudo(editPseudo.getText().toString());
                nbWaitingUpdating--;
            }else{
                this.gs.toastError(this, o.getString("feedback"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tryToFinish();
    }

    private Boolean updateDescription(JSONObject o){
        try {
            if(o.getInt("status") == 200){
                this.gs.getMyAccount().setDescription(editDescription.getText().toString());
                nbWaitingUpdating--;
            }else{
                this.gs.toastError(this, o.getString("feedback"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tryToFinish();
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

        gs = new GlobalState();

        //TODO : Faire pour la photo
        //this.editPhoto.setText(b.getString("photo"));
    }

    public Boolean updateMyPersonalData(View v){

        String new_pseudo = editPseudo.getText().toString();
        String new_description = editDescription.getText().toString();

        String qs = "";
        String action = "";

        if(! new_pseudo.equals(this.gs.getMyAccount().getPseudo())){
            action = "updatePseudo";

            qs = "action=" + action
                    + "&pseudo=" + new_pseudo;

            this.gs.doRequestWithApi(this.getApplicationContext(), this.TAG, qs, this::updatePseudo);
            nbWaitingUpdating++;
        }

        if(! new_description.equals(this.gs.getMyAccount().getDescription())){
            action = "updateDescriptionUser";

            qs = "action=" + action
                    + "&description=" + new_description;

            this.gs.doRequestWithApi(this.getApplicationContext(), this.TAG, qs, this::updateDescription);
            nbWaitingUpdating++;
        }

        /**TODO : update photo**/
        return tryToFinish();
    }

    public Boolean tryToFinish(){
        if(nbWaitingUpdating == 0){
            this.finish();
            return true;
        }
        return false;
    }

    public void goToChangeMyPasswordActivity(View v){
        Intent nextView = new Intent(this,ChangeMyPasswordActivity.class);
        startActivity(nextView);
    }
}
