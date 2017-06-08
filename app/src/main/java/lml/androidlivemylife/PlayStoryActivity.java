package lml.androidlivemylife;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import ClassPackage.GlobalState;
import Fragments.SimpleCompassFragment;
import Fragments.SimpleMapFragment;

public class PlayStoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_story);

        //Title
        TextView txt = (TextView) findViewById(R.id.play_story_title_textView);
        txt.setText(GlobalState.myCurrentPlayedStory.getTitle());

        //Step X
        TextView tx2 = (TextView) findViewById(R.id.play_story_step_x);
        //The index begins at 0, so increment before displaying
        tx2.append(" " + (GlobalState.myCurrentPlayedStory.getIndexCurrentStep() +1));
        tx2.setPaintFlags(tx2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //description
        TextView txt3 = (TextView) findViewById(R.id.play_story_description_textView);
        txt3.setText(GlobalState.myCurrentPlayedStory.getCurrentPlayedStep().getDescription());

        //Switch
        Switch mySwitch = (Switch) findViewById(R.id.play_story_switch_button);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO - Change map - boussole
                if(isChecked){
                    mySwitch.setText(R.string.map_on);

                    FragmentManager fm = getSupportFragmentManager();
                    Location targetLocation = new Location("");
                    targetLocation.setLatitude(Float.parseFloat(GlobalState.myCurrentPlayedStory.getCurrentPlayedStep().getGpsLatitude()));//your coords of course
                    targetLocation.setLongitude(Float.parseFloat(GlobalState.myCurrentPlayedStory.getCurrentPlayedStep().getGpsLongitude()));
                    fm.beginTransaction().replace(R.id.play_story_frame_maps, SimpleMapFragment.newInstance("","",targetLocation,true, null), "tagMyMap").commit();
                }else{
                    mySwitch.setText(R.string.map_off);
                    FragmentManager fm = getSupportFragmentManager();
                    Location targetLocation = new Location("");
                    targetLocation.setLatitude(Float.parseFloat(GlobalState.myCurrentPlayedStory.getCurrentPlayedStep().getGpsLatitude()));//your coords of course
                    targetLocation.setLongitude(Float.parseFloat(GlobalState.myCurrentPlayedStory.getCurrentPlayedStep().getGpsLongitude()));
                    fm.beginTransaction().replace(R.id.play_story_frame_maps, SimpleCompassFragment.newInstance("","",targetLocation,true), "tagMyMap").commit();
                }
            }
        });


        FragmentManager fm = getSupportFragmentManager();
        Location targetLocation = new Location("");
        targetLocation.setLatitude(Float.parseFloat(GlobalState.myCurrentPlayedStory.getCurrentPlayedStep().getGpsLatitude()));//your coords of course
        targetLocation.setLongitude(Float.parseFloat(GlobalState.myCurrentPlayedStory.getCurrentPlayedStep().getGpsLongitude()));
        fm.beginTransaction().replace(R.id.play_story_frame_maps, SimpleMapFragment.newInstance("","",targetLocation,true, null), "tagMyMap").commit();

    }

    /**
     * Back to the current story display
     * @param v
     */
    public void backToSelectStory(View v){
        this.finish();
    }

    /**
     * Back to the previous step (map screen)
     * or to the current story display (if they already are on the first step)
     * @param v
     */
    public void backToPreviousStep(View v){
        if(GlobalState.myCurrentPlayedStory.getIndexCurrentStep() - 1 >= 0 ) {
            //Go to the previous step
            GlobalState.myCurrentPlayedStory.decrementIndexCurrentStep();
            Intent nextView = new Intent(this.getApplication().getApplicationContext(), PlayStoryActivity.class);
            startActivity(nextView);
        }

        //Removes from the stack this Activity
        //And it is the default else if the current step is the first one
        this.finish();
    }

    /**
     * Go to the next step
     * or to the final finished story display (if they already are on the last step)
     * @param v
     */
    public void goToNextStep(View v){
        if(GlobalState.myCurrentPlayedStory.getIndexCurrentStep() + 1 >= GlobalState.myCurrentPlayedStory.getSteps().size() ){
            //Story finished
            Intent nextView = new Intent(this.getApplication().getApplicationContext(), FinishedStoryActivity.class);
            startActivity(nextView);
        }else{
            //Next step
            GlobalState.myCurrentPlayedStory.incrementIndexCurrentStep();
            Intent nextView = new Intent(this.getApplication().getApplicationContext(), PlayStoryActivity.class);
            startActivity(nextView);
        }

        //Removes from the stack this Activity
        this.finish();
    }

    /**
     * Go the the screen where they see the picture of the step
     * @param v
     */
    public void goToStepDone(View v){
        Intent nextView = new Intent(this.getApplication().getApplicationContext(), FinishedStepActivity.class);
        startActivity(nextView);

        //Removes from the stack this Activity
        this.finish();
    }




}
