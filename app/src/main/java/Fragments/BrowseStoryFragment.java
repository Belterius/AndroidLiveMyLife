package Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import API_request.RequestClass;
import ClassPackage.GlobalState;
import ClassPackage.MyUser;
import ClassPackage.Story;
import ClassPackage.ToastClass;
import lml.androidlivemylife.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BrowseStoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BrowseStoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowseStoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    final public String TAG = "browseStories";

    private ListView lv;
    private BrowseStoriesAdapter browseStoriesAdapter;
    private ArrayList<Story> storyArrayList;
    private AVLoadingIndicatorView loader;

    private GlobalState gs;

    public ArrayList<Story> getStoryArrayList() {
        return storyArrayList;
    }

    public BrowseStoriesAdapter getBrowseStoriesAdapter() {
        return browseStoriesAdapter;
    }

    public BrowseStoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BrowseStoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrowseStoryFragment newInstance(String param1, String param2) {
        BrowseStoryFragment fragment = new BrowseStoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        storyArrayList = new ArrayList<>();
        gs = new GlobalState();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse_story, container, false);
        lv = (ListView) rootView.findViewById(R.id.listView);
        loader = (AVLoadingIndicatorView) rootView.findViewById(R.id.avi);


        browseStoriesAdapter = new BrowseStoriesAdapter(this.getActivity(), storyArrayList, this);
        lv.setAdapter(browseStoriesAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                //TODO ouvrir l'activité affichant les détails de la story pour la jouer
                gs.alerter(container.getContext(), "CLIC");
            }
        });

        lv.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                getMoreStories(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getMoreStories(0);
    }

    /**
     * Récupère toutes les stories (les X prochaines à partir de l'offset défini en paramètre)
     */
    public void getMoreStories(int offset){
        loader.smoothToShow();
        loader.bringToFront();
        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", "getMoreStories");
        dataToPass.put("offset", String.valueOf(offset));

        RequestClass.doRequestWithApi(this.getActivity().getApplicationContext(), this.TAG,dataToPass, this::resultGetMoreStories);
    }

    public boolean resultGetMoreStories(JSONObject o){
        try {
            JSONArray stories = o.getJSONArray("stories");
            if(o.getInt("status") == 200 && stories != null){
                for(int i=0; i<stories.length(); i++){
                    JSONObject json_data = stories.getJSONObject(i);
                    MyUser author = new MyUser(json_data.getString("authorId"),
                            json_data.getString("authorEmail"),
                            json_data.getString("authorPseudo"),
                            json_data.getString("authorFirstname"),
                            json_data.getString("authorLastname"),
                            json_data.getString("authorDescription"),
                            json_data.getString("authorPhoto"));
                    Story story = new Story(json_data.getString("storyId"),
                            json_data.getString("storyTitle"),
                            json_data.getString("storyDescription"),
                            json_data.getString("storyPicture"),
                            "1".equals(json_data.getString("storyIsPublished")),
                            author);
                    storyArrayList.add(story);
                }

                browseStoriesAdapter.notifyDataSetChanged();
                loader.smoothToHide();
                return true;
            }else{
                ToastClass.toastError(this.getActivity(), o.getString("feedback"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        loader.smoothToHide();
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
