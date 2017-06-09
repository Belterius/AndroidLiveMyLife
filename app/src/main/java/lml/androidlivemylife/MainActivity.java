package lml.androidlivemylife;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import ClassPackage.GlobalState;
import Fragments.BrowseStoryFragment;
import Fragments.LocalStoriesFragment;
import Fragments.MyAccountFragment;
import Fragments.NewStoryFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigation;
    private GlobalState gs;
    private static final int result_from_publish = 1;
    private static final int result_from_edit = 2;

    public BottomNavigationView getNavigation() {
        return navigation;
    }

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
        Fragment myFragment = manager.findFragmentByTag("LocalStoriesFragment");
        if (myFragment == null || !myFragment.isVisible()) {
            manager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                    .replace(R.id.content_home, LocalStoriesFragment.newInstance("test","test"), "LocalStoriesFragment")
                    .commit();
        }
    }

    public void switchToNewStory() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment myFragment = manager.findFragmentByTag("NewStoryFragment");
        if (myFragment == null || !myFragment.isVisible()) {
            Fragment myFragment2 = manager.findFragmentByTag("LocalStoriesFragment");
            if(myFragment2 != null && myFragment2.isVisible()){
                manager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.content_home, NewStoryFragment.newInstance("test", "test"), "NewStoryFragment")
                        .commit();
            }
            else{
                manager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                        .replace(R.id.content_home, NewStoryFragment.newInstance("test", "test"), "NewStoryFragment")
                        .commit();
            }

        }
    }

    public void switchToBrowseStory() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment myFragment = manager.findFragmentByTag("BrowseStoryFragment");
        if (myFragment == null || !myFragment.isVisible()) {
            Fragment myFragment2 = manager.findFragmentByTag("MyAccountFragment");
            if(myFragment2 != null && myFragment2.isVisible()){
                manager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                        .replace(R.id.content_home, BrowseStoryFragment.newInstance("test", "test"), "BrowseStoryFragment")
                        .commit();
            }
            else{
                manager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.content_home, BrowseStoryFragment.newInstance("test", "test"), "BrowseStoryFragment")
                        .commit();
            }
        }
    }

    public void switchToMyAccount() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment myFragment = manager.findFragmentByTag("MyAccountFragment");
        if (myFragment == null || !myFragment.isVisible()) {
            manager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.content_home, MyAccountFragment.newInstance("test", "test"), "MyAccountFragment")
                    .commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        switchToLocalStories();

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (result_from_publish) : {
                if (resultCode == Activity.RESULT_OK) {
                    FragmentManager manager = getSupportFragmentManager();
                    Fragment myFragment = manager.findFragmentByTag("LocalStoriesFragment");
                    ((LocalStoriesFragment) myFragment).getPersonalStories();
                }
                break;
            }
            case (result_from_edit) : {
                if (resultCode == Activity.RESULT_OK) {
                    String newTitle = data.getStringExtra("newTitle");
                    String newDescription = data.getStringExtra("newDescription");
                    String newHighlight = data.getStringExtra("newHighlight");

                    FragmentManager manager = getSupportFragmentManager();
                    Fragment myFragment = manager.findFragmentByTag("LocalStoriesFragment");
                    LocalStoriesFragment myLocalFragment = ((LocalStoriesFragment) myFragment);

                    myLocalFragment.getStoryArrayList().get(myLocalFragment.getLastItemOpened()).setTitle(newTitle);
                    myLocalFragment.getStoryArrayList().get(myLocalFragment.getLastItemOpened()).setDescription(newDescription);
                    if(!newHighlight.equals("")){
                        myLocalFragment.getStoryArrayList().get(myLocalFragment.getLastItemOpened()).setHighlight(newHighlight);
                    }
                    myLocalFragment.getLocalStoriesAdapter().notifyDataSetChanged();
                }
                break;
            }
        }
    }

    public static int getResult_from_publish() {
        return result_from_publish;
    }

    public static int getResult_from_edit() {
        return result_from_edit;
    }

//    public void goToMaps(View v){
//        startActivity(new Intent(this, MapsActivity.class));
//    }

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
