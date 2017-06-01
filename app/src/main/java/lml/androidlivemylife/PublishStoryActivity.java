package lml.androidlivemylife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import ClassPackage.GlobalState;

public class PublishStoryActivity extends AppCompatActivity {

    final public String TAG = "publishStory";
    private TextView title;
    private ImageButton previousarrow;
    private GlobalState gs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_story);

        title = (TextView) findViewById(R.id.titleStory);
        previousarrow = (ImageButton) findViewById(R.id.publish_story_previous_button);

        Bundle b = this.getIntent().getExtras();
        String s = b.getString("storyTitle").toString();
        this.title.setText(s);

        previousarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToLocalStories();
            }
        });

        gs = new GlobalState();
    }

    @Override
    protected void onStop () {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * To refactor : Ne pas ouvrir un nouvel intent, mais plutot renvoyer une paramètre !!
     */
    private void goBackToLocalStories(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
