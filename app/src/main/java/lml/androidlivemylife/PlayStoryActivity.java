package lml.androidlivemylife;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ClassPackage.GlobalState;

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
}
