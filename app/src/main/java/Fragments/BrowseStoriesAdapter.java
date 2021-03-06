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
import ClassPackage.Story;
import lml.androidlivemylife.R;

/**
 * Created by Francois on 22/05/2017.
 */

public class BrowseStoriesAdapter extends BaseAdapter implements Filterable {

    Context context;
    private BrowseStoryFragment fragment;
    ArrayList<Story> data = null;
    ArrayList<Story> newData = null;
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

    public ArrayList<Story> getData() {
        return data;
    }

    public ArrayList<Story> getNewData() {
        return newData;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
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
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.listview_item_row, null);
        this.position = position;
        Story dataStory = data.get(position);

        holder.title=(TextView) rowView.findViewById(R.id.txtTitle);
        holder.desc=(TextView) rowView.findViewById(R.id.txtDesc);
        holder.img=(ImageView) rowView.findViewById(R.id.imgIcon);
        holder.author= (TextView) rowView.findViewById(R.id.txtAuthor);

        holder.title.setText(dataStory.getTitle());
        holder.desc.setText(dataStory.getDescription());
        holder.author.setText(" by " + dataStory.getAuthor().getPseudo());
        holder.author.setVisibility(View.VISIBLE);
        Picasso.with(context)
                .load(data.get(position).getHighlight())
                .error(R.drawable.error_triangle)
                .into(((ImageView)holder.img));

        return rowView;
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Story> results = new ArrayList<Story>();
                if (newData == null)
                    newData = data;
                if (constraint != null) {
                    if (newData != null && newData.size() > 0) {
                        for (final Story s : newData) {
                            if (s.getTitle().toLowerCase().contains(constraint.toString())
                                    || s.getDescription().toLowerCase().contains(constraint.toString())
                                    || s.getAuthor().getPseudo().toLowerCase().contains(constraint.toString()))
                                results.add(s);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                data = (ArrayList<Story>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
