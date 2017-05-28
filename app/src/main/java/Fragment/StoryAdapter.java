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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
                Toast.makeText(parent.getContext(), "Delete " + holder.title.getText(), Toast.LENGTH_LONG).show();
            }
        });

        return rowView;
    }
}
