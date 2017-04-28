package lml.androidlivemylife;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

// Il ne s'agit plus de classes filles...
// On passe donc des références à la classe mère dans le constructeur
// On crée une classe RestActivity qui implémente
// ce qu'il faut pour simplifier les requêtes


public class RestRequest extends AsyncTask<String, Void, JSONObject> {

    private RestActivity mAct ;
    private GlobalState gs;
    private String action = null;
    // Une tâche ne peut être exécutée qu'une seule fois

    public RestRequest(RestActivity act) {
        mAct = act;
        gs = mAct.gs; // On fait normalement des WeakReferences ?
    }

    @Override
    protected void onPreExecute() {
        // S'exécute dans l'UI Thread
        super.onPreExecute();
        Log.i(gs.CAT,"onPreExecute");
    }

    @Override
    protected JSONObject doInBackground(String... qs) {
        Log.i(gs.CAT,"doInBackground");	// rien de l'UI thread ici

        // si nb args de qs = 1 ??
        // qs.length existe

        action = qs[1];

        String res = gs.requete(qs[0]);
        Log.i(gs.CAT,"res : " + res);

        JSONObject json;
        try {
            json = new JSONObject(res);

        } catch (JSONException e) {
            e.printStackTrace();
            json = new JSONObject();
            // Comment faire de manière rigoureuse ?
        }

        Log.i(gs.CAT,"interpretation effectuee");
        return json;
    }

    protected void onPostExecute(JSONObject result) {
        Log.i(gs.CAT,"onPostExecute");
        mAct.traiteReponse(result, action);
    }
}
