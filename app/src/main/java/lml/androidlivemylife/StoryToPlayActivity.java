package lml.androidlivemylife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ClassPackage.GlobalState;

public class StoryToPlayActivity extends AppCompatActivity {
    final public String TAG = "toPlayStory";
    private TextView title;
    private TextView authorName;

    private GlobalState gs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_to_play);

        title = (TextView) findViewById(R.id.titleStoryToPlay);
        authorName = (TextView) findViewById(R.id.authorStoryToPlay);

        Bundle b = this.getIntent().getExtras();
        this.title.setText(b.getString("storyTitle").toString());
        this.authorName.setText("by " + b.getString("authorPseudo").toString());
    }
}
