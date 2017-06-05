package lml.androidlivemylife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import API_request.MySingleton;
import ClassPackage.GlobalState;

public class ChangeMyPasswordActivity extends AppCompatActivity {

    private GlobalState gs;
    final public String TAG = "changeMyPassword";
    EditText password_old;
    EditText password_new;
    EditText password_new_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_my_password);

        gs = new GlobalState();

        password_old = (EditText)findViewById(R.id.update_password_old_edit);
        password_new = (EditText)findViewById(R.id.update_password_new_edit);
        password_new_confirm = (EditText)findViewById(R.id.update_password_confirm_edit);
    }

    public void updatePassword(View v){

        String password_old = this.password_old.getText().toString();
        String password_new = this.password_new.getText().toString();
        String password_new_confirm = this.password_new_confirm.getText().toString();

        if(password_old.equals(password_new)){
            this.gs.toastError(this, "The old password must be different from the new one !");
            return;
        }
        if(! password_new.toString().equals(password_new_confirm)){
            this.gs.toastError(this, "The new password and the confirmation password must be the same !");
            return;
        }

        String action = "updatePassword";

        String qs = "action=" + action
                + "&password_old=" + password_old
                + "&password_new=" + password_new;

        this.gs.doRequestWithApi(this.getApplicationContext(), this.TAG, qs, this::postRequestUpdatePassword);
    }

    private Boolean postRequestUpdatePassword(JSONObject o){
        try {
            if(o.getInt("status") == 200){
                finish();
                return true;
            }else{
                this.gs.toastError(this, o.getString("feedback"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (MySingleton.getInstance(this).getRequestQueue() != null) {
            MySingleton.getInstance(this).getRequestQueue().cancelAll(TAG);
        }
    }
}
