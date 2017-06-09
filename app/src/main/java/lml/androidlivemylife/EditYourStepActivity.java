package lml.androidlivemylife;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import API_request.MySingletonRequestApi;
import API_request.RequestClass;
import ClassPackage.GlobalState;
import ClassPackage.Step;
import ClassPackage.ToastClass;
import ExtendedPackage.UploadPictureActivity;

public class EditYourStepActivity extends UploadPictureActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private String TAG = "EditYourStep";
    private EditText stepDescription;
    private GlobalState gs;
    private boolean goToPicture;
    private AVLoadingIndicatorView loader;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient; //Permet d'effectuer des appels sur l'API google, entre autre permet le calcul du parcours entre notre position et notre objectif
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_your_step);

        this.stepDescription = (EditText) findViewById(R.id.edit_step_description);
        this.setImageViewForUploadClass(R.id.edit_step_preview_picture);

        gs = new GlobalState();

        //False la première fois : car on passe dans le onResume à la création de la page
        //On veut éviter de passer aussi dans le this.finish()
        goToPicture = false;

        this.loader = (AVLoadingIndicatorView) findViewById(R.id.edit_step_gif);

        if(requestEveryPermission()){
            goToPicture = true;
            this.takeNewPicture(this.getCurrentFocus());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Si on a bien les permissions requises, on initialise notre API Client
                buildGoogleApiClient();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if( requestCode == this.PERMISSION_ALL){
            int returnPermissions = verificatePermissions(permissions, grantResults, false);
            if(returnPermissions == 1){

                //On indique qu'au retour de la prise de photo ou non, on aura été sur l'activité camera
                //On regardera donc si on doit continuer la création de la step ou non
                goToPicture = true;

                this.takeNewPicture(this.getCurrentFocus());

            }else if(returnPermissions == -1){
                Toast.makeText(this, R.string.permissions_remember_edit_your_step, Toast.LENGTH_LONG).show();
                finish();
            }else{ //0
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.alert_confirm);
                alert.setMessage(R.string.alert_permissions_create_step);
                alert.setPositiveButton(R.string.alert_choice_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

                alert.setNegativeButton(R.string.alert_permissions_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestEveryPermission();
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //On a fait précédent, et on a donc pas d'image
        // Retour direct aux steps
        if(this.bitmap == null && goToPicture == true){
            this.finish();
        }
    }

    /**
     * Initialise notre API Client qui permettra de calculer les chemins
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Take picture using camera
     * @param v
     */
    public void changePicture (View v){
        //Take picture
//        this.showFileChooser(this.findViewById(android.R.id.content));
        this.takeNewPicture(v);
    }

    /**
     * On click the next button : will publish the step
     * @param v
     */
    public void publishStepAndGoToMyStory (View v){

        Location tempoLoc = new Location(mCurrentLocation);

        String stepDescription = this.stepDescription.getText().toString();

        if(stepDescription.equals("") || bitmap == null){
            ToastClass.toastError(this, getString(R.string.error_empty_field));
            return;
        }

        String action = "publishStep";

        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", action);
        dataToPass.put("idf_story", this.gs.getMyAccount().getMyCurrentStory().getIdStory());
        dataToPass.put("stepDescription", stepDescription);
        dataToPass.put("stepGpsLongitude", String.valueOf(tempoLoc.getLongitude()));
        dataToPass.put("stepGpsLatitude", String.valueOf(tempoLoc.getLatitude()));
        dataToPass.put("stepPicture", getImageToPassForRequest());

        loader.smoothToShow();
        loader.bringToFront();

        RequestClass.doRequestWithApi(this.getApplicationContext(), this.TAG, dataToPass, this::getReturnFromPublishStep);
    }

    /**
     * Be sure that the request was a success before finishing this activity
     * @param o
     * @return
     */
    public Boolean getReturnFromPublishStep(JSONObject o){

        loader.smoothToHide();
        try {

            if(o.getInt("status") == 200 && o.getJSONObject("newStep") != null){

                JSONObject newStep = o.getJSONObject("newStep");

                this.gs.getMyAccount().getMyCurrentStory().addStep(
                        new Step(
                                newStep.getString("id"),
                                newStep.getString("pictureUrl"),
                                newStep.getString("stepGpsLongitude"),
                                newStep.getString("stepGpsLatitude"),
                                newStep.getString("description")
                        )
                );

                this.finish();
                return true;

            }else{
                ToastClass.toastError(this, o.getString("feedback"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }



    @Override
    public void onLocationChanged(Location location) {
        if(mCurrentLocation == null || (mCurrentLocation.getLatitude() != location.getLatitude() || mCurrentLocation.getLongitude() != location.getLongitude()))
            this.mCurrentLocation = location;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100000);
        mLocationRequest.setSmallestDisplacement(20);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop () {
        super.onStop();
        if (MySingletonRequestApi.getInstance(this).getRequestQueue() != null) {
            MySingletonRequestApi.getInstance(this).getRequestQueue().cancelAll(TAG);
        }
    }
}
