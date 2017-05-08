package lml.androidlivemylife;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ClassPackage.GlobalState;
import Fragment.BrowseStoryFragment;
import Fragment.LocalStoriesFragment;
import Fragment.MyAccountFragment;
import Fragment.NewStoryFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigation;
    private GlobalState gs;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchToLocalStories();
                    break;
                case R.id.navigation_new:
                    switchToNewStory();
                    break;
                case R.id.navigation_browse:
                    switchToBrowseStory();
                    break;
                case R.id.navigation_account:
                    switchToMyAccount();
                    break;
            }
            return true;
        }

    };

    public void switchToLocalStories() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content_home, LocalStoriesFragment.newInstance("test","test")).commit();
    }

    public void switchToNewStory() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content_home, NewStoryFragment.newInstance("test","test")).commit();
    }

    public void switchToBrowseStory() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content_home, BrowseStoryFragment.newInstance("test","test")).commit();
    }

    public void switchToMyAccount() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content_home, MyAccountFragment.newInstance("test","test")).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_home, LocalStoriesFragment.newInstance("test","test"));
        transaction.commit();

        checkLocationPermission();

        gs = new GlobalState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(this.navigation.getSelectedItemId() == R.id.navigation_account){
            //Refresh the data
            //initMyProfileView();
        }
    }


    public void goToMaps(View v){
        startActivity(new Intent(this, MapsActivity.class));
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

}
