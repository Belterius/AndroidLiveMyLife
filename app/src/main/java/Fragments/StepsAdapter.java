package Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ClassPackage.GlobalState;
import ClassPackage.Step;
import ClassPackage.Story;
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

        holder.title.setText("Step " + String.valueOf(position+1));
        holder.desc.setText(dataStep.getDescription());

        Picasso.with(context)
                .load(data.get(position).getUrlPicture())
//                .placeholder(R.drawable.loading_gears)
//                .error(R.drawable.ic_menu_report_image)
                .error(R.drawable.error_triangle)
                .into(((ImageView)holder.img));

        return rowView;
    }
}
