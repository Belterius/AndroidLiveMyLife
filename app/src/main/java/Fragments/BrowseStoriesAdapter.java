package Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import lml.androidlivemylife.EditStoryActivity;
import lml.androidlivemylife.MainActivity;
import lml.androidlivemylife.PublishStoryActivity;
import lml.androidlivemylife.R;

/**
 * Created by Francois on 22/05/2017.
 */

public class BrowseStoriesAdapter extends BaseAdapter {

    Context context;
    private BrowseStoryFragment fragment;
    ArrayList<Story> data = null;
    private static LayoutInflater inflater=null;
    private GlobalState gs;
    private int position;
    private String title;
    private String TAG = "browseStories";

    public BrowseStoriesAdapter(Context context, ArrayList<Story> data, BrowseStoryFragment fragment) {
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
        TextView author;
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
        holder.author= (TextView) rowView.findViewById(R.id.txtAuthor);

        holder.title.setText(data.get(position).getTitle());
        holder.desc.setText(data.get(position).getDescription());
        //holder.author.setText("by " + data.get(position).getAuthor().getFirstname() + data.get(position).getAuthor().getLastname());
//        holder.img.setImageResource(R.drawable.magnifier);
        //holder.img.setImageResource(data.get(position).getHighlight());
        Picasso.with(context)
                .load(data.get(position).getHighlight())
//                .placeholder(R.drawable.loading_gears)
//                .error(R.drawable.ic_menu_report_image)
                .error(R.drawable.error_triangle)
                .into(((ImageView)holder.img));

        return rowView;
    }
}
