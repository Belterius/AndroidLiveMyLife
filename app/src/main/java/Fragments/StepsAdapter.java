package Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import API_request.RequestClass;
import ClassPackage.GlobalState;
import ClassPackage.Step;
import ClassPackage.Story;
import ClassPackage.ToastClass;
import lml.androidlivemylife.R;

/**
 * Created by Francois on 22/05/2017.
 */

public class StepsAdapter extends BaseAdapter {

    Context context;
    private NewStoryFragment fragment;
    ArrayList<Step> data = null;
    private static LayoutInflater inflater=null;
    private GlobalState gs;
    private int position;
    private String TAG = "newStory";

    public StepsAdapter(Context context, ArrayList<Step> data, NewStoryFragment fragment) {
        this.context = context;
        this.data = data;
        this.fragment = fragment;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        gs = new GlobalState();
    }

    public ArrayList<Step> getData() {
        return data;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView title;
        TextView desc;
        ImageView img;
        ImageButton deletebutton;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.listview_item_row_step, null);
        this.position = position;
        Step dataStep = data.get(position);

        holder.title= (TextView) rowView.findViewById(R.id.txtTitle);
        holder.desc=(TextView) rowView.findViewById(R.id.txtDesc);
        holder.img=(ImageView) rowView.findViewById(R.id.imgIcon);
        holder.deletebutton= (ImageButton) rowView.findViewById(R.id.deletebutton);

        holder.title.setText("Step " + String.valueOf(position+1));
        holder.desc.setText(dataStep.getDescription());

        Picasso.with(context)
                .load(data.get(position).getUrlPicture())
//                .placeholder(R.drawable.loading_gears)
//                .error(R.drawable.ic_menu_report_image)
                .error(R.drawable.error_triangle)
                .into(((ImageView)holder.img));

        holder.deletebutton.setImageResource(R.drawable.delete);

        holder.deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(parent.getContext());
                alert.setTitle(R.string.title_delete);
                alert.setMessage(R.string.confirmation_delete_step);
                alert.setPositiveButton(R.string.choice_yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Animation animation = AnimationUtils.loadAnimation(fragment.getContext(), android.R.anim.slide_out_right);
                        rowView.startAnimation(animation);
                        Handler handle = new Handler();
                        handle.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fragment.getStepsArrayList().remove(position);
                                notifyDataSetChanged();
                                animation.cancel();
                            }
                        },500);
                        deleteStep(data.get(position).getId());
                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton(R.string.choice_no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });

        return rowView;
    }

    /**
     * Delete la step sur laquelle on a cliqué
     */
    public void deleteStep(String id){

        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", "deleteStep");
        dataToPass.put("stepId", id);

        RequestClass.doRequestWithApi(context, this.TAG,dataToPass, this::resultDeleteStep);
    }

    public boolean resultDeleteStep(JSONObject o){
        try {
            if(o.getInt("status") == 200){
                return true;

            }else{
                ToastClass.toastError(fragment.getActivity(), o.getString("feedback"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }
}
