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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.function.Function;

import API_request.MySingletonRequestApi;
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

    public static Story myCurrentPlayedStory;

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

    public void alerter(Context c, String s){
        Log.i(CAT, s);
        Toast t = Toast.makeText(c, s, Toast.LENGTH_LONG);
        t.show();
    }

    /**
     * We check if the network is available, if it is, we make the button clickable
     * @param c
     * @return
     */
    public boolean verifNetwork(Context c)
    {
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
}
