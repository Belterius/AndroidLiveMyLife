package lml.androidlivemylife;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import API_request.RequestClass;
import ClassPackage.GlobalState;
import ClassPackage.Story;
import ClassPackage.ToastClass;

public class StartStoryActivity extends AppCompatActivity {

    private ImageView imageViewPicturePreview;
    private ImageButton isLikedButton;

    private final String TAG = "startStory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_story);

        Bundle b = this.getIntent().getExtras();
        GlobalState.myCurrentPlayedStory =  (Story) b.getSerializable("storyToPlay");

        //Title
        TextView txt = (TextView) findViewById(R.id.start_story_title_textView);
        txt.setText(GlobalState.myCurrentPlayedStory.getTitle());

        //Story made by : (and underline the text)
        TextView tx2 = (TextView) findViewById(R.id.start_story_by_textview);
        tx2.append(" " + GlobalState.myCurrentPlayedStory.getAuthor().getPseudo());
        tx2.setPaintFlags(tx2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //description
        TextView txt3 = (TextView) findViewById(R.id.start_story_description_textView);
        txt3.setText(GlobalState.myCurrentPlayedStory.getDescription());

        //Picture - Highlight
        imageViewPicturePreview = (ImageView) findViewById(R.id.start_story_preview_picture);
        Picasso.with(this.getApplicationContext())
                .load(GlobalState.myCurrentPlayedStory.getHighlight())
                .placeholder(R.drawable.loading_gears)
                .error(R.drawable.ic_menu_report_image)
                .into(this.imageViewPicturePreview);

        //Like / unlike
        this.isLikedButton = (ImageButton) findViewById(R.id.start_story_isLike_button);
        if(GlobalState.myCurrentPlayedStory.isLikedByThisUser()){
            setOnClickListenerUnlike();
        }else{
            setOnClickListenerLike();
        }

        //ETA
        //Pedestrian
        TextView txt4 = (TextView) findViewById(R.id.start_story_pedestrian_time_textview);
        txt4.setText("x h");

        //Cyclist
        TextView txt5 = (TextView) findViewById(R.id.start_story_cyclist_time_textview);
        txt5.setText("x h");
    }

    /**
     * Initiliazes the button listener to like the story the next time they hit the button
     */
    private void setOnClickListenerLike(){
        isLikedButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                likeStory();
            }
        });

        isLikedButton.setImageResource(R.drawable.is_not_liked);
    }


    /**
     * Initiliazes the button listener to unlike the story the next time they hit the button
     */
    private void setOnClickListenerUnlike(){
        isLikedButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                unlikeStory();
            }
        });

        isLikedButton.setImageResource(R.drawable.is_liked);
    }

    /**
     * Likes the current story
     */
    private void likeStory(){
        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", "likeStory");
        dataToPass.put("storyId", GlobalState.myCurrentPlayedStory.getIdStory());

        RequestClass.doRequestWithApi(this.getApplicationContext(), this.TAG, dataToPass, this::getResponseFromServerToLikeStory);
    }

    /**
     * Unlike the current story
     */
    private void unlikeStory(){
        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", "unlikeStory");
        dataToPass.put("storyId", GlobalState.myCurrentPlayedStory.getIdStory());

        RequestClass.doRequestWithApi(this.getApplicationContext(), this.TAG, dataToPass, this::getResponseFromServerToUnlikeStory);
    }

    /**
     * Gets the response from the API server : they liked the story
     * @param o
     * @return
     */
    private Boolean getResponseFromServerToLikeStory(JSONObject o){
        try {
            if(o.getInt("status") == 200){
                setOnClickListenerUnlike();
                return true;
            }else{
                ToastClass.toastError(this, o.getString("feedback"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Gets the response from the API server : they unliked the story
     * @param o
     * @return
     */
    private Boolean getResponseFromServerToUnlikeStory(JSONObject o){
        try {
            if(o.getInt("status") == 200){
                setOnClickListenerLike();
                return true;
            }else{
                ToastClass.toastError(this, o.getString("feedback"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Function for previous button listener - back to the previous screen
     * @param v
     */
    public void back(View v){
        this.finish();
    }

    public void playStory(View v){
        //Reset steps of this story
        GlobalState.myCurrentPlayedStory.setIndexCurrentStep(0);
        Intent nextView = new Intent(this.getApplication().getApplicationContext(), PlayStoryActivity.class);
        startActivity(nextView);
    }

}
