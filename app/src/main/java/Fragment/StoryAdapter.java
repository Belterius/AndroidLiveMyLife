package Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ClassPackage.GlobalState;
import ClassPackage.Story;
import lml.androidlivemylife.R;

/**
 * Created by Francois on 22/05/2017.
 */

public class StoryAdapter extends BaseAdapter {

    Context context;
    private LocalStoriesFragment fragment;
    ArrayList<Story> data = null;
    private static LayoutInflater inflater=null;
    private GlobalState gs;
    private int position;
    private int lastRemoved;

    public StoryAdapter(Context context, ArrayList<Story> data, LocalStoriesFragment fragment) {
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
        holder.img.setImageResource(R.drawable.magnifier);
        //holder.img.setImageResource(data.get(position).getHighlight());
        if(data.get(position).isPublished() == false){
            holder.imgb1.setImageResource(R.drawable.publish);
            holder.imgb2.setImageResource(R.drawable.edit);
        }
        else{
            holder.imgb1.setImageResource(R.drawable.facebook);
            holder.imgb2.setImageResource(R.drawable.twitter);
        }
        holder.imgb3.setImageResource(R.drawable.delete);
        holder.imgb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.get(position).isPublished() == false){
                    Toast.makeText(parent.getContext(), "Publish " + holder.title.getText(), Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(parent.getContext(), "Facebook - " + holder.title.getText(), Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.imgb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.get(position).isPublished() == false){
                    Toast.makeText(parent.getContext(), "Edit " + holder.title.getText(), Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(parent.getContext(), "Twitter - " + holder.title.getText(), Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.imgb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(parent.getContext());
                alert.setTitle("Delete");
                alert.setMessage("Are you sure you want to delete this story?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

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
                                lastRemoved = -1;
                                notifyDataSetChanged();
                                animation.cancel();
                            }
                        },500);
                        deleteStory(data.get(position).getIdStory());
                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

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
        String qs = "action=deleteStory&storyId=" + id;
        gs.doRequestWithApi(context, "localStories", qs, this::resultDeleteStory);
    }

    public boolean resultDeleteStory(JSONObject o){
        try {
            if(o.getInt("status") == 200){
                return true;

            }else{
                this.gs.toastError(fragment.getActivity(), o.getString("feedback"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }
}
