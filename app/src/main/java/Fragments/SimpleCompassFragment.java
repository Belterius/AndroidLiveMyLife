package Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ClassPackage.GlobalState;
import GPS.DirectionsJSONParser;
import lml.androidlivemylife.R;

/**
 * Created by belterius on 30/05/2017.
 * https://pastebin.com/ABNtuHNQ Save
 */

public class SimpleCompassFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        SensorEventListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    MapView myMap;//MapView que l'on récupère de notre fragment
    GoogleMap mMap;//GoogleMap sur laquelle on effectue les traitements & affichages
    private GoogleApiClient mGoogleApiClient; //Permet d'effectuer des appels sur l'API google, entre autre permet le calcul du parcours entre notre position et notre objectif
    private GlobalState gs; //nos fonctions/paramètres globaux

    private OnFragmentInteractionListener mListener;//Permet d'intéragir avec notre fragment
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private boolean showCurrentPos;//We don't show our current pos on the screen displaying a story
    private Location mCurrentLocation;//Notre position actuelle, mise à jour automatiquement par des appels sur onLocationChanged lorsque la position GPS du téléphone change
    private Location targetLocation;//La position que l'on souhaite atteindre
    private Marker mCurrLocationMarker;//le marqueur correspondant à notre position actuelle
    private LocationRequest mLocationRequest;
    private Polyline currentPolylinePath;//le chemin entre notre position et notre cible, sauvegardé de manière à pouvoir le supprimmer lorsque le chemin change


    public static SimpleCompassFragment newInstance(String param1, String param2, Location targetLocation, Boolean showCurrentPos) {
        SimpleCompassFragment fragment = new SimpleCompassFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        fragment.targetLocation = targetLocation;
        fragment.showCurrentPos = showCurrentPos;
        return fragment;
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boussole, container, false);//On récupère notre XML qui vient remplir le fragment
        myMap = (MapView) view.findViewById(R.id.myGoogleMap); //On récupère notre MapView
        myMap.onCreate(savedInstanceState);
        myMap.getMapAsync(this); //Permet de récupérer notre googlemap depuis notre mapview
        myMap.onResume();//Permet un affichage instantané de la map

        mSensorManager = (SensorManager) this.getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        return view;
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    }

    /**
     * Called when the accuracy of the registered sensor has changed.  Unlike
     * onSensorChanged(), this is only called when this accuracy value changes.
     * <p>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link SensorManager SensorManager} for details.
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gs = new GlobalState();
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Permet de récupérer une googlemap depuis une mapview
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialise Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Si on a bien les permissions requises, on initialise notre API Client et on active notre map Location (entre autre pour LocationChanged
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            throw new Error("Permissions manquantes");
        }
        if(!showCurrentPos){
            if(targetLocation == null)
                return;

            LatLng latLng = new LatLng(targetLocation.getLatitude(), targetLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            mMap.animateCamera(cameraUpdate);
            setMarker(targetLocation, "Target Location");

            return;
        }
    }

    /**
     * Initialise notre API Client qui permettra de calculer les chemins
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     *Permet de placer un market sur notre google map
     * @param loc l'emplacement de notre marker
     * @param markerText le nom (description) de notre marker
     */
    public void setMarker(Location loc, String markerText) {
        if(loc == null)
            return;
        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(markerText);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
    }

    /**
     * Apppellé à chaque changement de l'emplacement GPS du téléphone.
     * On va changer :
     * notre valeur de position actuelle
     * notre marker de position actuelle
     * le chemin entre notre position et notre cible
     * @param location notre position actuelle
     */
    @Override
    public void onLocationChanged(Location location) {
        if(!showCurrentPos){
            return;
        }

        if(mCurrentLocation == null){
            mCurrentLocation = location;
        }else if(mCurrentLocation.getLongitude() == location.getLongitude() && mCurrentLocation.getLatitude() == location.getLatitude())
            return;

        mCurrentLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place le marker de notre position actuelle
        setMarker(location, "Current Position");
        setMarker(targetLocation, "Target Location");

        //place la carte sur notre position actuelle
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        //Si on a une destination, trâce le chemin
        if(mCurrentLocation != null && targetLocation != null)
            pathFromCurrentToTarget();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setSmallestDisplacement(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this.getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this.getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }

    /**
     * Permet de lancer l'analyse du chemin entre notre position et notre cible
     * récupère les informations depuis Google Api Client, puis les affiche sur notre googlemap
     */
    private void pathFromCurrentToTarget(){
        if(mCurrentLocation == null)
            return;
        if(targetLocation == null)
            return;
        String url = getDirectionsUrl(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), new LatLng(targetLocation.getLatitude(), targetLocation.getLongitude()));

        SimpleCompassFragment.DownloadTask downloadTask = new SimpleCompassFragment.DownloadTask();

        // télécharge les informations (json) fournie par Google Directions API
        downloadTask.execute(url);
    }

    /**
     * Crée la structure de l'url de requête pour le chemin entre notre position et notre destination
     * @param origin latitude de notre position
     * @param dest latitude de notre destination
     * @return
     */
    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origine de la route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination de la route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Si le sensor est activé
        String sensor = "sensor=true";

        //Le type de moyen de déplacement (walking/driving/bicycling/transit)
        String mode = "mode=walking";

        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+mode;

        // le format de la réponse
        String output = "json";

        // Construction de l'URL
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }
    /** permet de délécharger la réponse format JSON du Google API */
    private String downloadJsonFromUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // On ouvre la connection vers l'URL Google API(générée par notre getDirectionsUrl)
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connection
            urlConnection.connect();

            // On récupère la réponse de l'API ligne par ligne
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception dl url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Permet de récupérer des données de réponse à l'URL fourni de manière asynchrone
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            // les données que l'on récupère
            String data = "";
            try{
                data = downloadJsonFromUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // S'execute sur l'UI thread, une fois doInBackground terminé
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            SimpleCompassFragment.ParserGooglePlacesToJSONTask parserTask = new SimpleCompassFragment.ParserGooglePlacesToJSONTask();

            // Parse la data récupéré depuis l'URL en JSON
            parserTask.execute(result);
        }
    }

    /** Parse les réponses de l'api Google place/direction en JSON */
    private class ParserGooglePlacesToJSONTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Pour l'ensemble des routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                // Récupère l'ensemble des points de notre route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Ajoute l'ensemble des points de notre route à notre ligne
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);
            }

            // Affiche notre route sur notre google map
            Polyline myPathLine = mMap.addPolyline(lineOptions);

            //Si on avait déjà une route, retire la précédente
            if(currentPolylinePath != null)
                currentPolylinePath.remove();

            //Sauvegarde notre route actuelle, de manière à pouvoir la supprimer ultérieurement sans avoir besoin de clear() l'intégralité de notre map
            currentPolylinePath = myPathLine;
        }
    }

    float currentDegree = 0f;
    /**
     * Appellé lorsque le sensor du téléphone change
     * @param event
     */
    public void onSensorChanged( SensorEvent event ) {
        if ( mCurrentLocation == null || targetLocation == null) return;
        GeomagneticField geoField = new GeomagneticField(
                Double.valueOf(mCurrentLocation.getLatitude()).floatValue(),
                Double.valueOf(mCurrentLocation.getLongitude()).floatValue(),
                Double.valueOf(mCurrentLocation.getAltitude()).floatValue(),
                System.currentTimeMillis()
        );

        float degree = Math.round(event.values[0]);
        degree += geoField.getDeclination();


        float bearing = mCurrentLocation.bearingTo(targetLocation);
        degree = (bearing - degree) * -1;
        degree = normalizeDegree(degree);

        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        if(this.getActivity() == null)
            return;
        this.getActivity().findViewById(R.id.compass).startAnimation(ra);
        currentDegree = -degree;



    }

    private float normalizeDegree(float value) {
        if (value >= 0.0f && value <= 180.0f) {
            return value;
        } else {
            return 180 + (180 + value);
        }
    }

    public void testSensor(){
        SensorEvent event = null;
        float azimuth = event.values[0];
        float baseAzimuth = azimuth;

        GeomagneticField geoField = new GeomagneticField( Double
                .valueOf( mCurrentLocation.getLatitude() ).floatValue(), Double
                .valueOf( mCurrentLocation.getLongitude() ).floatValue(),
                Double.valueOf( mCurrentLocation.getAltitude() ).floatValue(),
                System.currentTimeMillis() );

        azimuth -= geoField.getDeclination(); //converti le nord magnétique (intersection des lignes de champ magnétiques) en nord réel (emplacement physique correspondant à l'intersection des méridens)

        Location test = new Location("");
        test.setLatitude(50.3946885);
        test.setLongitude(2.8287587);

        // récupère l'orientation entre notre position et notre destination
        float bearTo = mCurrentLocation.bearingTo( test );

        //Si notre orientation est <0, on doit ajouter 360 pour conserver une rotation dans le sens des aiguilles d'une montre
        if (bearTo < 0) {
            bearTo = bearTo + 360;
        }
        //On souhaite donc pointer vers notre orientation, MOINS la compensation entre le nord magnétique, et le nord physique
        float direction = bearTo - azimuth ;

        //Si notre orientation est <0, on doit ajouter 360 pour conserver une rotation dans le sens des aiguilles d'une montre
        if (direction < 0) {
            direction = direction + 360;
        }

        //Permet de sécuriser, si l'utilisateur
        if(this.getActivity() == null)
            return;

        if(this.getActivity().findViewById(R.id.compass) != null)
            this.getActivity().findViewById(R.id.compass).setRotation(direction % 360);

        //Set the field
        String bearingText = "N";

        if ( (360 >= baseAzimuth && baseAzimuth >= 337.5) || (0 <= baseAzimuth && baseAzimuth <= 22.5) ) bearingText = "N";
        else if (baseAzimuth > 22.5 && baseAzimuth < 67.5) bearingText = "NE";
        else if (baseAzimuth >= 67.5 && baseAzimuth <= 112.5) bearingText = "E";
        else if (baseAzimuth > 112.5 && baseAzimuth < 157.5) bearingText = "SE";
        else if (baseAzimuth >= 157.5 && baseAzimuth <= 202.5) bearingText = "S";
        else if (baseAzimuth > 202.5 && baseAzimuth < 247.5) bearingText = "SW";
        else if (baseAzimuth >= 247.5 && baseAzimuth <= 292.5) bearingText = "W";
        else if (baseAzimuth > 292.5 && baseAzimuth < 337.5) bearingText = "NW";
        else bearingText = "?";

        System.out.println(bearingText);
        System.out.println(direction);
        Log.i("bearing",bearingText);
        Log.i("direction",String.valueOf(direction));
        Log.i("azimuth",String.valueOf(baseAzimuth));
    }
}
