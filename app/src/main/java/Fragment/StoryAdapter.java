package Fragment;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ClassPackage.Story;
import lml.androidlivemylife.R;

/**
 * Created by Francois on 22/05/2017.
 */

public class StoryAdapter extends BaseAdapter {

    Context context;
    ArrayList<Story> data = null;
    private static LayoutInflater inflater=null;

    public StoryAdapter(Context context, ArrayList<Story> data) {
        this.context = context;
        this.data = data;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        rowView = inflater.inflate(R.layout.listview_item_row, null);
        holder.title=(TextView) rowView.findViewById(R.id.txtTitle);
        holder.desc=(TextView) rowView.findViewById(R.id.txtDesc);
        holder.img=(ImageView) rowView.findViewById(R.id.imgIcon);
        holder.title.setText(data.get(position).getTitle());
        holder.desc.setText(data.get(position).getDescription());
        holder.img.setImageResource(R.drawable.magnifier);
        //holder.img.setImageResource(data.get(position).getHighlight());
        /*rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked "+result[position], Toast.LENGTH_LONG).show();
            }
        });*/
        return rowView;
    }
}
