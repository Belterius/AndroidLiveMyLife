package lml.androidlivemylife;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import API_request.RequestClass;
import ClassPackage.GlobalState;
import ClassPackage.Story;
import ClassPackage.ToastClass;
import ExtendedPackage.UploadPictureActivity;

public class CreateStoryActivity extends UploadPictureActivity {

    private String TAG = "EditYourStep";
    private EditText stepDescription;
    private EditText title;
    private GlobalState gs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        //Initializes the uploadPictureActivity
        setImageViewForUploadClass(R.id.create_story_picture_edit);

        //GlobalState
        gs = new GlobalState();

        stepDescription = (EditText) findViewById(R.id.create_stoy_description_edit);
        title = (EditText) findViewById(R.id.create_story_title_edit);
    }

    /**
     * Back to the steps corresponding to the current Story
     * @param v
     */
    public void goToListSteps(View v){
        this.finish();
    }

    public void createStory(View v){

        String descriptionToPass = this.stepDescription.getText().toString();
        String titleToPass = this.title.getText().toString();
        String action = "createStory";

        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", action);
        dataToPass.put("idf_story", this.gs.getMyAccount().getMyCurrentStory().getIdStory());
        dataToPass.put("storyTitle", titleToPass);
        dataToPass.put("storyDescription", descriptionToPass);
        dataToPass.put("storyPicture", getImageToPassForRequest());


        RequestClass.doRequestWithApi(this.getApplicationContext(), this.TAG, dataToPass, this::finalizeCreateStory);
    }

    private Boolean finalizeCreateStory(JSONObject o){

        try {

            if(o.getInt("status") == 200){

                JSONObject newCurrentStory = o.getJSONObject("newCurrentStory");

                if(newCurrentStory != null){

                    this.gs.getMyAccount().setMyCurrentStory(new Story(newCurrentStory.getString("id")));

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("isCreated", true);
                    setResult(Activity.RESULT_OK, resultIntent);

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

}
