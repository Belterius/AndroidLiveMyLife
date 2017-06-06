package lml.androidlivemylife;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import API_request.RequestClass;
import ClassPackage.GlobalState;
import ClassPackage.MyUser;
import ClassPackage.Step;
import ClassPackage.Story;
import ClassPackage.ToastClass;
import ExtendedPackage.UploadPictureActivity;

public class EditYourStepActivity extends UploadPictureActivity {

    private String TAG = "EditYourStep";
    private EditText stepDescription;
    private GlobalState gs;
    private boolean goToPicture;
    private AVLoadingIndicatorView loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_your_step);

        this.stepDescription = (EditText) findViewById(R.id.edit_step_description);
        this.setImageViewForUploadClass(R.id.edit_step_preview_picture);

        gs = new GlobalState();

        //False la première fois : car on passe dans le onResume à la création de la page
        //On veut éviter de passer aussi dans le this.finish()
        goToPicture = false;
        this.takeNewPicture(this.getCurrentFocus());

        this.loader = (AVLoadingIndicatorView) findViewById(R.id.edit_step_gif);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //On a fait précédent, et on a donc pas d'image
        // Retour direct aux steps
        if(this.bitmap == null && goToPicture == true){
            this.finish();
        }

        //On indique qu'au retour de la prise de photo ou non, on aura été sur l'activité camera
        //On regardera donc si on doit continuer la création de la step ou non
        if(goToPicture == false){
            goToPicture = true;
        }

    }

    /**
     * Take picture using camera
     * @param v
     */
    public void changePicture (View v){
        //Take picture
//        this.showFileChooser(this.findViewById(android.R.id.content));
        this.takeNewPicture(v);
    }

    /**
     * On click the next button : will publish the step
     * @param v
     */
    public void publishStepAndGoToMyStory (View v){

        String stepDescription = this.stepDescription.getText().toString();

        if(stepDescription.equals("") || bitmap == null){
            ToastClass.toastError(this, getString(R.string.error_empty_field));
            return;
        }

        String action = "publishStep";

        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", action);
        dataToPass.put("idf_story", this.gs.getMyAccount().getMyCurrentStory().getIdStory());
        dataToPass.put("stepDescription", stepDescription);
        dataToPass.put("stepGpsData", "1;2;3");
        dataToPass.put("stepPicture", getImageToPassForRequest());

        loader.smoothToShow();
        loader.bringToFront();

        RequestClass.doRequestWithApi(this.getApplicationContext(), this.TAG, dataToPass, this::getReturnFromPublishStep);
    }

    /**
     * Be sure that the request was a success before finishing this activity
     * @param o
     * @return
     */
    public Boolean getReturnFromPublishStep(JSONObject o){

        loader.smoothToHide();
        try {

            if(o.getInt("status") == 200 && o.getJSONObject("newStep") != null){

                JSONObject newStep = o.getJSONObject("newStep");

                this.gs.getMyAccount().getMyCurrentStory().addStep(
                        new Step(
                                newStep.getString("id"),
                                newStep.getString("pictureUrl"),
                                newStep.getString("gpsData"),
                                newStep.getString("description")
                        )
                );

                this.finish();
                return true;

            }else{
                ToastClass.toastError(this, o.getString("feedback"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }
}
