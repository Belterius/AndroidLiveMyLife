package ClassPackage;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import lml.androidlivemylife.R;

/**
 * Created by Gimlibéta on 22/05/2017.
 */

public class ToastClass {

    /**
     * Shows an error message to the user
     * @param act
     * @param errorToDisplay
     */
    public static void toastError(Activity act, String errorToDisplay){

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
