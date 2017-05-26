package lml.androidlivemylife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ExtendedPackage.UploadPictureActivity;

public class EditYourStepActivity extends UploadPictureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_your_step);

        this.setImageViewForUploadClass(R.id.edit_step_preview_picture);
        this.requestPermissions();
        //this.dispatchTakePictureIntent();
    }

    public void changePicture (View v){
        //Take picture
//        this.showFileChooser(this.findViewById(android.R.id.content));
        this.dispatchTakePictureIntent();
    }

    public void publishStepAndGoToMyStory (View v){

    }
}
