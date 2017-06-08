package lml.androidlivemylife;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import API_request.MySingletonRequestApi;
import API_request.RequestClass;
import ClassPackage.GlobalState;
import ClassPackage.Story;
import ClassPackage.ToastClass;
import ExtendedPackage.UploadPictureActivity;

public class EditStoryActivity extends UploadPictureActivity {

    final public String TAG = "publishStory";
    private GlobalState gs;
    private String storyId;
    private TextView pageTitle;
    private TextView title_to_edit;
    private TextView description_to_edit;

    /**
     * If false : it is edition
     * If true : it is creation
     */
    private Boolean isCreation;

    private String old_title;
    private String old_description;
    private String old_highlight;

//    private ImageView highlight_to_edit;

    private ImageButton previousarrow;
    private ImageButton validate;

    //Loading
    private AVLoadingIndicatorView loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);

        pageTitle = (TextView) findViewById(R.id.edit_story_titleStory_text);
        title_to_edit = (TextView) findViewById(R.id.edit_story_title_edit);
        description_to_edit = (TextView) findViewById(R.id.edit_story_description_edit);

        this.setImageViewForUploadClass(R.id.edit_story_picture_edit);
        setChoosePictureFromGalleryButton(R.id.edit_story_button_galery);
        setTakePictureButton(R.id.edit_story_button_take_new_picture);

        previousarrow = (ImageButton) findViewById(R.id.edit_story_previous_button);
        validate = (ImageButton) findViewById(R.id.edit_story_validate_button);

        Bundle b = this.getIntent().getExtras();

        isCreation = b.getBoolean("isCreation");

        if(! isCreation){
            //It is edition => Pre-fill labels
            storyId = b.getString("storyId");
            pageTitle.setText(getString(R.string.title_edit_and_other_name) + b.getString("storyTitle").toString());
            title_to_edit.setText(b.getString("storyTitle").toString());
            old_title = b.getString("storyTitle").toString();
            description_to_edit.setText(b.getString("storyDescription").toString());
            old_description = b.getString("storyDescription").toString();
            old_highlight = b.getString("storyHighlight").toString();

            Picasso.with(this.getApplicationContext())
                    .load(b.getString("storyHighlight").toString())
//                .placeholder(R.drawable.loading_gears)
                    .error(R.drawable.ic_menu_report_image)
//                .error(R.drawable.error_triangle)
                    .into(((ImageView)this.imageViewPicturePreview));

            validate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editStory();
                }
            });

        }else{

            pageTitle.setText(R.string.title_your_story);

            validate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createStory();
                }
            });
        }

        previousarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gs = new GlobalState();

        this.loader = (AVLoadingIndicatorView) findViewById(R.id.edit_story_gif);
        requestEveryPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(! isCreation){
            //Edition : normal use case
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        //Creation : specific use case
        if( requestCode == this.PERMISSION_ALL){
            int returnPermissions = verificatePermissions(permissions, grantResults, false);
            if(returnPermissions == 1){
                //Do nothing
            }else if(returnPermissions == -1){
                Toast.makeText(this, R.string.permissions_remember_edit_story, Toast.LENGTH_LONG).show();
                finish();
            }else{ //0
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.alert_confirm);
                alert.setMessage(R.string.permissions_edit_story_cant_create);
                alert.setPositiveButton(R.string.alert_choice_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

                alert.setNegativeButton(R.string.alert_permissions_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestEveryPermission();
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        }

    }

    @Override
    protected void onStop () {
        super.onStop();
        if (MySingletonRequestApi.getInstance(this).getRequestQueue() != null) {
            MySingletonRequestApi.getInstance(this).getRequestQueue().cancelAll(TAG);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Sends the request to the API to edit the given story
     */
    private void editStory(){

        if(description_to_edit.getText().toString().equals("") || title_to_edit.getText().toString().equals("")){
            ToastClass.toastError(this, getString(R.string.error_empty_field));
            return;
        }

        if(title_to_edit.getText().toString().length() > 20){
            ToastClass.toastError(this, getString(R.string.error_title_too_long));
            return;
        }

        if(!old_description.equals(description_to_edit.getText().toString()) || ! old_title.equals(title_to_edit.getText().toString()) || bitmap != null){

            loader.smoothToShow();
            loader.bringToFront();

            Map<String, String> dataToPass = new HashMap<>();
            dataToPass.put("action", "editStory");
            dataToPass.put("storyId", storyId);
            dataToPass.put("storyTitle", title_to_edit.getText().toString());
            dataToPass.put("storyDescription", description_to_edit.getText().toString());
            dataToPass.put("storyPicture", bitmap != null ? this.getImageToPassForRequest() : "");

            RequestClass.doRequestWithApi(this, this.TAG,dataToPass, this::resultEditStory);
        }else{
            ToastClass.toastError(this, getString(R.string.error_any_modifications));
        }
    }

    /**
     * Gets the results from the API request to edit the given story
     */
    public boolean resultEditStory(JSONObject o){
        try {
            loader.smoothToHide();
            if(o.getInt("status") == 200){

                JSONObject newCurrentStory = o.getJSONObject("story");

                if(newCurrentStory != null){
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("newTitle", newCurrentStory.getString("storyTitle"));
                    resultIntent.putExtra("newDescription", newCurrentStory.getString("storyDescription"));
                    resultIntent.putExtra("newHighlight", newCurrentStory.getString("pictureUrl"));
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();

                    return true;
                }
            }
            else{
                ToastClass.toastError(this, o.getString("feedback"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sends the request to the API to create the current story
     * @return success or failure
     */
    private Boolean createStory(){

        String descriptionToPass = this.description_to_edit.getText().toString();
        String titleToPass = this.title_to_edit.getText().toString();

        if(descriptionToPass.equals("") || titleToPass.equals("") || bitmap == null){
            ToastClass.toastError(this, getString(R.string.error_fill_field));
            return false;
        }

        loader.smoothToShow();
        loader.bringToFront();

        String action = "createStory";

        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", action);
        dataToPass.put("storyId", this.gs.getMyAccount().getMyCurrentStory().getIdStory());
        dataToPass.put("storyTitle", titleToPass);
        dataToPass.put("storyDescription", descriptionToPass);
        dataToPass.put("storyPicture", getImageToPassForRequest());


        RequestClass.doRequestWithApi(this.getApplicationContext(), this.TAG, dataToPass, this::finalizeCreateStory);

        return true;
    }

    /**
     * Gets the results from the API request to create the current story
     */
    private Boolean finalizeCreateStory(JSONObject o){

        try {

            loader.smoothToHide();
            if(o.getInt("status") == 200){

                JSONObject newCurrentStory = o.getJSONObject("newCurrentStory");

                if(newCurrentStory != null){

                    this.gs.getMyAccount().setMyCurrentStory(new Story(newCurrentStory.getString("id")));

                    Intent resultIntent = new Intent();
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
