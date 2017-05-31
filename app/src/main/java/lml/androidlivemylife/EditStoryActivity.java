package lml.androidlivemylife;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import ClassPackage.GlobalState;

public class EditStoryActivity extends AppCompatActivity {

    final public String TAG = "publishStory";
    private GlobalState gs;
    private String storyId;
    private TextView pageTitle;
    private TextView title_to_edit;
    private TextView description_to_edit;
    private String old_title;
    private String old_description;
    private String old_highlight;
    private ImageView highlight_to_edit;
    private ImageButton previousarrow;
    private ImageButton validate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);

        pageTitle = (TextView) findViewById(R.id.titleStory);
        title_to_edit = (TextView) findViewById(R.id.story_title_edit);
        description_to_edit = (TextView) findViewById(R.id.story_description_edit);
        highlight_to_edit = (ImageView) findViewById(R.id.story_highlight_edit);
        previousarrow = (ImageButton) findViewById(R.id.edit_story_previous_button);
        validate = (ImageButton) findViewById(R.id.edit_story_validate_button);

        Bundle b = this.getIntent().getExtras();
        storyId = b.getString("storyId");
        pageTitle.setText("Edit " + b.getString("storyTitle").toString());
        title_to_edit.setText(b.getString("storyTitle").toString());
        old_title = b.getString("storyTitle").toString();
        description_to_edit.setText(b.getString("storyDescription").toString());
        old_description = b.getString("storyDescription").toString();
        old_highlight = b.getString("storyHighlight").toString();

        Picasso.with(this.getApplicationContext())
                .load(b.getString("storyHighlight").toString())
//                .placeholder(R.drawable.loading_gears)
//                .error(R.drawable.ic_menu_report_image)
                .error(R.drawable.error_triangle)
                .into(((ImageView)highlight_to_edit));


        previousarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToLocalStories();
            }
        });

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editStory();
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

    private void goBackToLocalStories(){
        finish();
    }

    private void editStory(){
        //TODO requeteBDD + retour sur écran d'accueil
        if(old_description != description_to_edit.getText() || old_title != title_to_edit.getText()){ //|| bitmap != null
            String qs = "action=editStory&storyId=" + storyId
                    + "&title=" + title_to_edit.getText()
                    + "&description=" + description_to_edit.getText()
                    + "&highlight=" + old_highlight; //TODO REMPLACER PAR LE NOUVEAU HIGHLIGHT
            gs.doRequestWithApi(this, this.TAG, qs, this::resultEditStory);
        }
    }

    public boolean resultEditStory(JSONObject o){
        try {
            if(o.getInt("status") == 200){
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            else{
                this.gs.toastError(this, o.getString("feedback"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
