package lml.androidlivemylife;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ClassPackage.GlobalState;

public class FinishedStepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_step);

        //Picture - Highlight
        ImageView imageViewPicturePreview = (ImageView) findViewById(R.id.step_done_image);
        Picasso.with(this.getApplicationContext())
                .load(GlobalState.myCurrentPlayedStory.getCurrentPlayedStep().getUrlPicture())
                .placeholder(R.drawable.loading_gears)
                .error(R.drawable.ic_menu_report_image)
                .into(imageViewPicturePreview);

        //

        //Step X
        TextView tx2 = (TextView) findViewById(R.id.step_done_step_x);
        //The index begins at 0, so increment before displaying
        tx2.append(" " + (GlobalState.myCurrentPlayedStory.getIndexCurrentStep() +1));
        tx2.setPaintFlags(tx2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }


    /**
     * Back to the current story display
     * @param v
     */
    public void backToSelectStory(View v){
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
