package lml.androidlivemylife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import API_request.RequestClass;
import ClassPackage.GlobalState;
import ClassPackage.ToastClass;

public class FinishedStoryActivity extends AppCompatActivity {

    private ImageButton isLikedButton;
    private final String TAG = "finishedStory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_story);

        //Picture - Highlight
        ImageView imageViewPicturePreview = (ImageView) findViewById(R.id.story_done_image);
        Picasso.with(this.getApplicationContext())
                .load(GlobalState.myCurrentPlayedStory.getHighlight())
                .placeholder(R.drawable.loading_gears)
                .error(R.drawable.ic_menu_report_image)
                .into(imageViewPicturePreview);

        //Like / unlike
        this.isLikedButton = (ImageButton) findViewById(R.id.story_done_isLike_button);
        if(GlobalState.myCurrentPlayedStory.isLikedByThisUser()){
            setOnClickListenerUnlike();
        }else{
            setOnClickListenerLike();
        }
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
                GlobalState.myCurrentPlayedStory.setLikedByThisUser(true);
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
                GlobalState.myCurrentPlayedStory.setLikedByThisUser(false);
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
     * Back to the current story display
     * @param v
     */
    public void backToSelectStory(View v){
        this.finish();
    }

}
