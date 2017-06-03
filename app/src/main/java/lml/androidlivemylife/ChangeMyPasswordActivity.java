package lml.androidlivemylife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import API_request.MySingletonRequestApi;
import ClassPackage.GlobalState;
import API_request.RequestClass;
import ClassPackage.ToastClass;

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

        if(password_old.equals("") || password_new.equals("")|| password_new_confirm.equals("")){
            ToastClass.toastError(this, getString(R.string.error_empty_field));
            return;
        }

        if(password_old.equals(password_new)){
            ToastClass.toastError(this, getString(R.string.error_oldPassword_equal_newPassword));
            return;
        }
        if(! password_new.toString().equals(password_new_confirm)){
            ToastClass.toastError(this, getString(R.string.error_password_and_confirmation_different));
            return;
        }

        String action = "updatePassword";

        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", action);
        dataToPass.put("password_old", password_old);
        dataToPass.put("password_new", password_new);

        //Avoid doing the request
        RequestClass.doRequestWithApi(this.getApplicationContext(), this.TAG, dataToPass, this::postRequestUpdatePassword);

    }

    private Boolean postRequestUpdatePassword(JSONObject o){
        try {
            if(o.getInt("status") == 200){
                this.finish();
                return true;
            }else{
                ToastClass.toastError(this, o.getString("feedback"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (MySingletonRequestApi.getInstance(this).getRequestQueue() != null) {
            MySingletonRequestApi.getInstance(this).getRequestQueue().cancelAll(TAG);
        }
    }
}
