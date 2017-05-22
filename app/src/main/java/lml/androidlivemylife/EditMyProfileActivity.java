package lml.androidlivemylife;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ClassPackage.GlobalState;
import API_request.RequestClass;
import ClassPackage.ToastClass;
import ExtendedPackage.UploadPictureActivity;

public class EditMyProfileActivity extends UploadPictureActivity {

    private EditText editPseudo;
    private EditText editDescription;

    private GlobalState gs;

    //Allow to start simultaneously 2 different call with the server
    //Then they wait their response to finish this activity
    private int requestDoing = 0;

    final public String TAG = "editMyProfile";

    /**
     * Reponse from the server : Updates the local attributes
     * @param o JSONObject : the response
     * @return
     */
    private Boolean updateUser(JSONObject o){
        try {
            if(o.getInt("status") == 200 && o.getJSONObject("user") != null) {

                if(o.getString("action").equals("updatePhotoUser")){
                    if(! o.getJSONObject("user").get("photo").equals(null)){
                        this.gs.getMyAccount().setPicture(o.getJSONObject("user").getString("photo"));
                    }
                }

                if(o.getString("action").equals("updatePseudoAndDescriptionUser")){
                        this.gs.getMyAccount().setDescription(o.getJSONObject("user").getString("description"));
                        this.gs.getMyAccount().setPseudo(o.getJSONObject("user").getString("pseudo"));
                }

                if((--requestDoing) == 0 ){
                    this.finish();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_profile);

        editPseudo = (EditText) findViewById(R.id.my_profile_pseudo_edit);
        imageViewPicturePreview = (ImageView) findViewById(R.id.my_profile_picture_edit);
        editDescription = (EditText) findViewById(R.id.my_profile_bio_edit);

        gs = new GlobalState();

        this.editPseudo.setText(this.gs.getMyAccount().getPseudo());
        this.editDescription.setText(this.gs.getMyAccount().getDescription());
        Picasso.with(this.getApplicationContext())
            .load(this.gs.getMyAccount().getPicture())
            .placeholder(R.drawable.loading_gears)
            .error(R.drawable.ic_menu_report_image)
            .into(imageViewPicturePreview);

    }

    /**
     * Clicks on the update button -> Sends the request(s)
     * @param v
     * @return
     */
    public Boolean updateMyPersonalData(View v){

        String new_pseudo = editPseudo.getText().toString();
        String new_description = editDescription.getText().toString();

        if(!new_pseudo.equals(this.gs.getMyAccount().getPseudo()) || !new_description.equals(this.gs.getMyAccount().getDescription())){

            Map<String, String> dataToPass = new HashMap<>();
            dataToPass.put("action", "updatePseudoAndDescriptionUser");
            dataToPass.put("pseudo", new_pseudo);
            dataToPass.put("description",new_description);
            requestDoing++;

            RequestClass.doRequestWithApi(this.getApplicationContext(), this.TAG, dataToPass, this::updateUser);
        }

        if(bitmap != null){

            Map<String, String> dataToPass = new HashMap<>();
            dataToPass.put("action", "updatePhotoUser");
            dataToPass.put("photo", getStringImage(bitmap));
            requestDoing++;

            RequestClass.doRequestWithApi(this.getApplicationContext(), this.TAG, dataToPass, this::updateUser);
        }

        //If we do not send a request, so we close this activity
        if(requestDoing > 0) {
            return true;
        }else{
            this.finish();
            return false;
        }
    }

    /**
     * Will display the next intent to change the password
     * @param v
     */
    public void goToChangeMyPasswordActivity(View v){
        Intent nextView = new Intent(this,ChangeMyPasswordActivity.class);
        startActivity(nextView);
    }

}
