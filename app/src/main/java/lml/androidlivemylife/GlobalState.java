package lml.androidlivemylife;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.function.Function;

import ClassPackage.Personne;

/**
 * Created by Gimlibéta on 21/02/2017.
 */

public class GlobalState extends Application{

    public String CAT = "LiveMyLife";
    public CookieManager cookieManager;

    public Boolean connected = false;
    public Personne myAccount;

    public int notificationId = 1;

    @Override
    public void onCreate() {
        super.onCreate();

        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    void alerter(String s){
        Log.i(CAT, s);
        Toast t = Toast.makeText(this, s, Toast.LENGTH_LONG);
        t.show();
    }


    private String convertStreamToString(InputStream in) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean verifReseau()
    {
        // On vérifie si le réseau est disponible,
        // si oui on change le statut du bouton de connexion
        ConnectivityManager cnMngr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cnMngr.getActiveNetworkInfo();

        String sType = "Aucun réseau détecté";
        Boolean bStatut = false;
        if (netInfo != null)
        {
            NetworkInfo.State netState = netInfo.getState();

            if (netState.compareTo(NetworkInfo.State.CONNECTED) == 0)
            {
                bStatut = true;
                int netType= netInfo.getType();
                switch (netType)
                {
                    case ConnectivityManager.TYPE_MOBILE :
                        sType = "Réseau mobile détecté"; break;
                    case ConnectivityManager.TYPE_WIFI :
                        sType = "Réseau wifi détecté"; break;
                }

            }
        }

        this.alerter(sType);
        return bStatut;
    }

    public String requete(String qs) {
        if (qs != null)
        {
            //String urlData = "https://api.livemylife.coniface.fr/verif.php";
            //For localhost
            String urlData = "http://10.0.2.2/PHP-LiveMyLife/verif.php";
            String urlStr = urlData + "?" + qs;
            try {

                //To encode the parameters
                URL url = new URL(urlStr);
                URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                url = uri.toURL();

                Log.i(CAT,"used url : " + url.toString());
                HttpURLConnection urlConnection = null;
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Content-Language", Locale.getDefault().toString());

                Log.i("Appli","Language : " + Locale.getDefault().toString());

                InputStream in = null;
                in = new BufferedInputStream(urlConnection.getInputStream());
                String txtReponse = convertStreamToString(in);
                urlConnection.disconnect();
                return txtReponse;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }

        return "";
    }

    public void doRequestWithApi(final String TAG, String qs, final Function<JSONObject,Boolean> f){

        if (qs == null){
            return;
        }

        //String urlData = "https://api.livemylife.coniface.fr/verif.php";
        //For localhost
        String urlData = "http://10.0.2.2/PHP-LiveMyLife/verif.php";
        String urlStr = urlData + "?" + qs;

        URL url = null;
        try {

            url = new URL(urlStr);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            url = uri.toURL();

            Log.i(this.CAT, "Start Building");

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url.toString(), new JSONObject(),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    f.apply(response);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Log.i(TAG, error.toString());
                        }
                    });

            // Access the RequestQueue through your singleton class.
            MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

}
