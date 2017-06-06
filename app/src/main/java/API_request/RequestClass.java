package API_request;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.function.Function;

import API_request.MySingletonRequestApi;

/**
 * Created by Gimlibéta on 22/05/2017.
 */

public class RequestClass {

    private static final String CAT = "RequestClass";

    public static void doRequestWithApi(Context c, final String TAG, final Map<String, String> paramsToPass, final Function<JSONObject,Boolean> f){

        String urlData = "https://api.livemylife.coniface.fr/verif.php";
        //For localhost
        //String urlData = "http://10.0.2.2/PHP-LiveMyLife/verif.php";

        Log.i(CAT,paramsToPass.toString());

        URL url = null;
        try {

            url = new URL(urlData);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            url = uri.toURL();

            Log.i(CAT, "Start Building");

            StringRequest jsObjRequest = new StringRequest(Request.Method.POST, url.toString(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i(CAT, "Reponse : " + response.toString() );
                            try {
                                f.apply(new JSONObject(response));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Log.i(TAG, error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams(){
                    return paramsToPass;
                }
            };

            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Access the RequestQueue through your singleton class.
            MySingletonRequestApi.getInstance(c).addToRequestQueue(jsObjRequest);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

}
