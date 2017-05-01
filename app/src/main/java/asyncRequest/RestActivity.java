package asyncRequest;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import lml.androidlivemylife.GlobalState;
import lml.androidlivemylife.R;


public abstract class RestActivity extends FinishAllReceiver {

    protected GlobalState gs;
    protected TimerTask doAsynchronousTask;

    // Une classe capable de faire des requêtes simplement
    // Si elle doit faire plusieurs requetes,
    // comment faire pour controler quelle requete se termine ?
    // on passe une seconde chaine à l'appel asynchrone

    public void sendRequest(String qs, String action) {
        // En instanciant à chaque fois, on peut faire autant de requetes que l'on veut...

        RestRequest req = new RestRequest(this);
        req.execute(qs,action);
    }

    public String periodicUrl(String action) {
        // devrait être abstraite, mais dans ce cas doit être obligatoirement implémentée...
        // On pourrait utiliser une interface ?
        return "";
    }

    // http://androidtrainningcenter.blogspot.fr/2013/12/handler-vs-timer-fixed-period-execution.html
    // Try AlarmManager running Service
    // http://rmdiscala.developpez.com/cours/LesChapitres.html/Java/Cours3/Chap3.1.htm
    // La requete elle-même sera récupérée grace à l'action demandée dans la méthode periodicUrl
    public void periodicRequest(int periode, final String action) {


        final Handler handler = new Handler();
        Timer timer = new Timer();

        doAsynchronousTask = new TimerTask() {

            @Override
            public void run() {

                handler.post(new Runnable() {
                    public void run() {
                        sendRequest(periodicUrl(action),action);
                    }
                });

            }

        };

        timer.schedule(doAsynchronousTask, 0, 1000 * periode);

    }



    public abstract void postRequest(JSONObject o, String action);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gs = (GlobalState) getApplication();

        this.registerBaseActivityReceiver();
    }

    @Override
    protected void onDestroy(){

        this.unRegisterBaseActivityReceiver();
        super.onDestroy();
    }

    public void toastError(String errorToDisplay){

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(errorToDisplay);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

    }

}