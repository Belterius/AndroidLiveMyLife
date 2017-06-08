package lml.androidlivemylife;

import android.location.Location;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import API_request.MySingletonRequestApi;
import API_request.RequestClass;
import ClassPackage.GlobalState;
import ClassPackage.MyUser;
import ClassPackage.Step;
import ClassPackage.Story;
import ClassPackage.ToastClass;
import Fragments.SimpleMapFragment;

public class StartStoryActivity extends AppCompatActivity {

    private ImageView imageViewPicturePreview;
    private ImageButton isLikedButton;

    private final String TAG = "startStory";

    //Loading
    private AVLoadingIndicatorView loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_story);

        this.loader = (AVLoadingIndicatorView) findViewById(R.id.start_story_gif);


        this.loader = (AVLoadingIndicatorView) findViewById(R.id.start_story_gif);

        Bundle b = this.getIntent().getExtras();
        getStoryToPlay(b.getString("storyId"));
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(GlobalState.myCurrentPlayedStory != null){
            initLikeUnlike();
        }
    }

    /**
     * Sends the request to the API server - gets the data about the story to play
     */
    public void getStoryToPlay(String idToPass){
        loader.smoothToShow();
        loader.bringToFront();

        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", "getStoryToPlayWithSteps");
        dataToPass.put("storyId", idToPass);

        RequestClass.doRequestWithApi(this.getApplicationContext(), this.TAG,dataToPass, this::initializeStoryToPlay);
    }

    /**
     * Gets the data from the API server about the story to play
     * @param o
     * @return
     */
    public Boolean initializeStoryToPlay(JSONObject o) {

        try {
            loader.smoothToHide();
            if (o.getInt("status") == 200 && o.getJSONObject("story") != null) {
                JSONObject myStoryToPlay = o.getJSONObject("story");
                if (myStoryToPlay != null) {

                    GlobalState.myCurrentPlayedStory = new Story(
                            myStoryToPlay.getString("id"),
                            myStoryToPlay.getString("storyTitle"),
                            myStoryToPlay.getString("storyDescription"),
                            myStoryToPlay.getString("storyPicture")
                    );

                    JSONArray mySteps = myStoryToPlay.getJSONArray("steps");
                    if (mySteps != null) {

                        for (int i = 0; i < mySteps.length(); i++) {
                            JSONObject step = mySteps.getJSONObject(i);
                            GlobalState.myCurrentPlayedStory.addStep(
                                    new Step(
                                            step.getString("stepId"),
                                            step.getString("stepPicture"),
                                            step.getString("stepGpsLongitude"),
                                            step.getString("stepGpsLatitude"),
                                            step.getString("stepDescription")
                                    )
                            );
                        }
                    }

                    JSONObject myAuthor = myStoryToPlay.getJSONObject("user");
                    if (myAuthor != null) {
                        GlobalState.myCurrentPlayedStory.setAuthor(new MyUser(
                                myAuthor.getString("id"),
                                myAuthor.getString("email"),
                                myAuthor.getString("pseudo"),
                                myAuthor.getString("firstname"),
                                myAuthor.getString("lastname"),
                                myAuthor.getString("description"),
                                myAuthor.getString("photo")
                        ));
                    }

                    GlobalState.myCurrentPlayedStory.setLikedByThisUser(myStoryToPlay.getBoolean("isLikedByMe"));
                    displayWithData();


                    FragmentManager fm = getSupportFragmentManager();
                    Location gareLocation = new Location("");
                    gareLocation.setLatitude(50.4275348d);//your coords of course
                    gareLocation.setLongitude(2.8252978d);
                    List<Step> mySteps2 =  GlobalState.myCurrentPlayedStory.getSteps();
                    Step myStep = GlobalState.myCurrentPlayedStory.getSteps().get(0);


                    Location targetLocation = new Location("");
                    targetLocation.setLatitude(Float.parseFloat(GlobalState.myCurrentPlayedStory.getSteps().get(0).getGpsLatitude()));//your coords of course
                    targetLocation.setLongitude(Float.parseFloat(GlobalState.myCurrentPlayedStory.getSteps().get(0).getGpsLongitude()));
                    fm.beginTransaction().replace(R.id.start_story_framelayout, SimpleMapFragment.newInstance("","",targetLocation,false, mySteps2), "tagMyMap").commit();

                }

                return true;
            } else {
                ToastClass.toastError(this, o.getString("feedback"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
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
                GlobalState.myCurrentPlayedStory.setLikedByThisUser(false);
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

    private void displayWithData(){

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

        //ETA
        //Pedestrian
        TextView txt4 = (TextView) findViewById(R.id.start_story_pedestrian_time_textview);
        //txt4.setText("x h");

        //Cyclist
        TextView txt5 = (TextView) findViewById(R.id.start_story_cyclist_time_textview);
        //txt5.setText("x h");

        initLikeUnlike();
    }

    private void initLikeUnlike(){
        //Like / unlike
        this.isLikedButton = (ImageButton) findViewById(R.id.start_story_isLike_button);
        if(GlobalState.myCurrentPlayedStory.isLikedByThisUser()){
            setOnClickListenerUnlike();
        }else{
            setOnClickListenerLike();
        }
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (MySingletonRequestApi.getInstance(this).getRequestQueue() != null) {
            MySingletonRequestApi.getInstance(this).getRequestQueue().cancelAll(TAG);
        }
    }

    public void updateWalkingTime(String time){
        //Pedestrian
        TextView txt4 = (TextView) findViewById(R.id.start_story_pedestrian_time_textview);
        txt4.setText(time);
    }
    public void updateBicyclingTime(String time){
        //Pedestrian
        TextView txt5 = (TextView) findViewById(R.id.start_story_cyclist_time_textview);
        txt5.setText(time);
    }


}
