package ClassPackage;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Function;

import API_request.MySingleton;
import lml.androidlivemylife.R;

/**
 * Created by Gimlibéta on 21/02/2017.
 */

public class GlobalState{

    private static String CAT = "LiveMyLife";
    private static CookieManager cookieManager = null;

    private static Boolean connected = false;
    private static MyUser myAccount;

    private static int notificationId = 1;

    public GlobalState() {
        if(cookieManager == null){
            cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
        }
    }

    public String getCAT() {
        return CAT;
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        GlobalState.connected = connected;
    }

    public MyUser getMyAccount() {
        return myAccount;
    }

    public void setMyAccount(MyUser myAccount) {
        GlobalState.myAccount = myAccount;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        GlobalState.notificationId = notificationId;
    }

    void alerter(Context c, String s){
        Log.i(CAT, s);
        Toast t = Toast.makeText(c, s, Toast.LENGTH_LONG);
        t.show();
    }

    public boolean verifReseau(Context c)
    {
        // On vérifie si le réseau est disponible,
        // si oui on change le statut du bouton de connexion
        ConnectivityManager cnMngr = (ConnectivityManager) c.getSystemService(c.CONNECTIVITY_SERVICE);
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

        this.alerter(c, sType);
        return bStatut;
    }

    public void doRequestWithApi(Context c, final String TAG, String qs, final Function<JSONObject,Boolean> f){

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

            Log.i(GlobalState.CAT, "Start Building");

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url.toString(), new JSONObject(),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i(GlobalState.CAT, "Reponse : " + response.toString() );
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
            MySingleton.getInstance(c).addToRequestQueue(jsObjRequest);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void toastError(Activity act, String errorToDisplay){

        LayoutInflater inflater = (LayoutInflater) act.getSystemService(act.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) (act).findViewById(R.id.custom_toast_container));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(errorToDisplay);

        Toast toast = new Toast(act.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

    }

}
