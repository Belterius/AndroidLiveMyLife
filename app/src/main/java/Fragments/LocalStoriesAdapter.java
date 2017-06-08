package Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import API_request.RequestClass;
import ClassPackage.GlobalState;
import ClassPackage.Story;
import ClassPackage.ToastClass;
import lml.androidlivemylife.EditMyProfileActivity;
import lml.androidlivemylife.EditStoryActivity;
import lml.androidlivemylife.MainActivity;
import lml.androidlivemylife.PublishStoryActivity;
import lml.androidlivemylife.R;

/**
 * Created by Francois on 22/05/2017.
 */

public class LocalStoriesAdapter extends BaseAdapter {

    Context context;
    private LocalStoriesFragment fragment;
    ArrayList<Story> data = null;
    private static LayoutInflater inflater=null;
    private GlobalState gs;
    private int position;
    private int lastRemoved = -1;
    private String title;
    private static final int result_from_publish = 1;
    private String TAG = "localStories";

    public LocalStoriesAdapter(Context context, ArrayList<Story> data, LocalStoriesFragment fragment) {
        this.context = context;
        this.data = data;
        this.fragment = fragment;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        gs = new GlobalState();
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
        ImageButton imgb1;
        ImageButton imgb2;
        ImageButton imgb3;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.listview_item_row, null);
        this.position = position;

        holder.title=(TextView) rowView.findViewById(R.id.txtTitle);
        holder.desc=(TextView) rowView.findViewById(R.id.txtDesc);
        holder.img=(ImageView) rowView.findViewById(R.id.imgIcon);
        holder.imgb1= (ImageButton) rowView.findViewById(R.id.storybutton1);
        holder.imgb2= (ImageButton) rowView.findViewById(R.id.storybutton2);
        holder.imgb3= (ImageButton) rowView.findViewById(R.id.storybutton3);

        holder.title.setText(data.get(position).getTitle());
        holder.desc.setText(data.get(position).getDescription());
//        holder.img.setImageResource(R.drawable.magnifier);
        //holder.img.setImageResource(data.get(position).getHighlight());
        Picasso.with(context)
                .load(data.get(position).getHighlight())
//                .placeholder(R.drawable.loading_gears)
//                .error(R.drawable.ic_menu_report_image)
                .error(R.drawable.error_triangle)
                .into(((ImageView)holder.img));

        if(! data.get(position).isPublished()){
            holder.imgb1.setImageResource(R.drawable.publish);
            holder.imgb2.setImageResource(R.drawable.edit);
        }
        else{
            holder.imgb1.setImageResource(R.drawable.facebook);
            holder.imgb2.setImageResource(R.drawable.twitter);
        }
        holder.imgb3.setImageResource(R.drawable.delete);
        holder.imgb1.setOnClickListener(new View.OnClickListener() {

            //Publish
            @Override
            public void onClick(View v) {
                if(! data.get(position).isPublished()){
                    AlertDialog.Builder alert = new AlertDialog.Builder(parent.getContext());
                    alert.setTitle(R.string.title_publish);
                    alert.setMessage(R.string.confirmation_publish);
                    alert.setPositiveButton(R.string.choice_yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            publishStory(data.get(position).getIdStory(), holder.title.getText().toString().toUpperCase());
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
                else{
                    Toast.makeText(parent.getContext(), "Facebook - " + holder.title.getText(), Toast.LENGTH_LONG).show();
                }
            }
        });

        //Edit
        holder.imgb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( ! data.get(position).isPublished()){
                    Intent nextView = new Intent(fragment.getContext(), EditStoryActivity.class);
                    nextView.putExtra("isCreation", false);
                    nextView.putExtra("storyId", data.get(position).getIdStory());
                    nextView.putExtra("storyTitle", holder.title.getText().toString());
                    nextView.putExtra("storyDescription", holder.desc.getText().toString());
                    nextView.putExtra("storyHighlight", data.get(position).getHighlight());
                    nextView.putExtra("action", "edit");
                    fragment.getActivity().startActivityForResult(nextView, MainActivity.getResult_from_edit());
                }
                else{
                    Toast.makeText(parent.getContext(), "Twitter - " + holder.title.getText(), Toast.LENGTH_LONG).show();
                }
            }
        });

        //Remove
        holder.imgb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(parent.getContext());
                alert.setTitle(R.string.title_delete);
                alert.setMessage(R.string.confirmation_delete_story);
                alert.setPositiveButton(R.string.choice_yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Animation animation = AnimationUtils.loadAnimation(fragment.getContext(), android.R.anim.slide_out_right);
                        rowView.startAnimation(animation);
                        Handler handle = new Handler();
                        handle.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                lastRemoved = position;
                                fragment.getStoryArrayList().remove(lastRemoved);
                                notifyDataSetChanged();
                                animation.cancel();
                            }
                        },500);
                        deleteStory(data.get(position).getIdStory());
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
     * Delete la story sur laquelle on a cliqué
     */
    public void deleteStory(String id){

        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", "deleteStory");
        dataToPass.put("storyId", id);

        RequestClass.doRequestWithApi(context, this.TAG,dataToPass, this::resultDeleteStory);
    }

    public boolean resultDeleteStory(JSONObject o){
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

    /**
     * Publish la story sur laquelle on a cliqué
     */
    public void publishStory(String id, String title){
        this.title = title;

        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", "publishStory");
        dataToPass.put("storyId", id);

        RequestClass.doRequestWithApi(context, this.TAG,dataToPass, this::resultPublishStory);
    }

    public boolean resultPublishStory(JSONObject o){
        try {
            if(o.getInt("status") == 200){
                Intent nextView = new Intent(fragment.getActivity(), PublishStoryActivity.class);
                nextView.putExtra("storyTitle", this.title);
                fragment.getActivity().startActivityForResult(nextView, MainActivity.getResult_from_publish());
                return true;

            }else{
                ToastClass.toastError(fragment.getActivity(), o.getString("feedback"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public int getLastRemoved() {
        return lastRemoved;
    }

    public void setLastRemoved(int lastRemoved) {
        this.lastRemoved = lastRemoved;
    }

    public void resetLastRemoved(){
        lastRemoved = -1;
    }
}
