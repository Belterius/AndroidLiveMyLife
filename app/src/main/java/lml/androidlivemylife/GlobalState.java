package lml.androidlivemylife;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Gimlibéta on 27/04/2017.
 */

public class GlobalState extends Application{

    void alerter(String s){
        Log.i("LiveMyLife", s);
        Toast t = Toast.makeText(this, s, Toast.LENGTH_LONG);
        t.show();
    }

}
