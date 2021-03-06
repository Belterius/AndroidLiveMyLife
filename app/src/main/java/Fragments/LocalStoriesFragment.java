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
import java.util.Map;

import API_request.MySingletonRequestApi;
import ClassPackage.GlobalState;
import API_request.RequestClass;
import ClassPackage.Story;
import ClassPackage.ToastClass;
import lml.androidlivemylife.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocalStoriesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocalStoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalStoriesFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    final public String TAG = "localStories";

    private TextView title_local_stories;
    private ListView lv;
    private LocalStoriesAdapter localStoriesAdapter;
    private ArrayList<Story> storyArrayList;
    private int lastItemOpened;
    private View lastViewOpened;
    private AVLoadingIndicatorView loader;

    private GlobalState gs;

    public LocalStoriesFragment() {
        // Required empty public constructor
    }

    public int getLastItemOpened() {
        return lastItemOpened;
    }

    public LocalStoriesAdapter getLocalStoriesAdapter() {
        return localStoriesAdapter;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocalStoriesFragment.
     */
    public static LocalStoriesFragment newInstance(String param1, String param2) {
        LocalStoriesFragment fragment = new LocalStoriesFragment();
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_local_stories, container, false);
        title_local_stories = (TextView) rootView.findViewById(R.id.title_local_stories);
        lv = (ListView) rootView.findViewById(R.id.listView);
        loader = (AVLoadingIndicatorView) rootView.findViewById(R.id.avilocal);

        localStoriesAdapter = new LocalStoriesAdapter(this.getActivity(), storyArrayList, this);
        lv.setAdapter(localStoriesAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                if(arg1.findViewById(R.id.buttonshidden).getVisibility() == View.GONE){
                    showButtons(arg1);
                    lastItemOpened = position;
                    lastViewOpened = arg1;
                }
                else{
                    hideButtons(arg1);
                }
            }
        });

        lv.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                getMoreLocalStories(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (MySingletonRequestApi.getInstance(this.getContext()).getRequestQueue() != null) {
            MySingletonRequestApi.getInstance(this.getContext()).getRequestQueue().cancelAll(TAG);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPersonalStories();
    }

    public void showButtons(View hiddenbuttons){
        if(lastViewOpened != null && lastViewOpened != hiddenbuttons && localStoriesAdapter.getLastRemoved() == -1){
            View toHide = lastViewOpened.findViewById(R.id.buttonshidden);
            toHide.setVisibility(View.GONE);
        }else{
            localStoriesAdapter.resetLastRemoved();
        }

        View v = hiddenbuttons.findViewById(R.id.buttonshidden);
           v.setVisibility(View.VISIBLE);
    }

    public void hideButtons(View hiddenbuttons){
        View v = hiddenbuttons.findViewById(R.id.buttonshidden);
        v.setVisibility(View.GONE);
    }

    /**
     * Récupère les stories de l'utilisateur courant (utilisation de la variable de session côté serveur)
     */
    public void getPersonalStories(){
        loader.smoothToShow();
        loader.bringToFront();
        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", "getPersonalStories");

        RequestClass.doRequestWithApi(this.getActivity().getApplicationContext(), this.TAG,dataToPass, this::getMyPersonalStories);
    }

    public boolean getMyPersonalStories(JSONObject o){
        try {
            storyArrayList.clear();
            JSONArray stories = o.getJSONArray("stories");
            if(o.getInt("status") == 200 && stories != null){
                for(int i=0; i<stories.length(); i++){
                    JSONObject json_data = stories.getJSONObject(i);
                    Story story = new Story(json_data.getString("storyId"),
                            json_data.getString("storyTitle"),
                            json_data.getString("storyDescription"),
                            json_data.getString("storyPicture"),
                            "1".equals(json_data.getString("storyIsPublished")));
                    storyArrayList.add(story);
                }

                localStoriesAdapter.notifyDataSetChanged();
                loader.smoothToHide();
                return true;
            }else{
                ToastClass.toastError(this.getActivity(), o.getString("feedback"));
            }

        } catch (JSONException e) {
            this.title_local_stories.setText(R.string.error_no_stories);
            e.printStackTrace();
        }

        loader.smoothToHide();
        return false;
    }

    /**
     * Récupère toutes les stories locales(les X prochaines à partir de l'offset défini en paramètre)
     */
    public void getMoreLocalStories(int offset){
        loader.smoothToShow();
        loader.bringToFront();
        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", "getMoreLocalStories");
        dataToPass.put("offset", String.valueOf(offset));

        RequestClass.doRequestWithApi(this.getActivity().getApplicationContext(), this.TAG,dataToPass, this::resultGetMoreLocalStories);
    }

    public boolean resultGetMoreLocalStories(JSONObject o){
        try {
            JSONArray stories = o.getJSONArray("stories");
            if(o.getInt("status") == 200 && stories != null){
                for(int i=0; i<stories.length(); i++){
                    JSONObject json_data = stories.getJSONObject(i);
                    Story story = new Story(json_data.getString("storyId"),
                            json_data.getString("storyTitle"),
                            json_data.getString("storyDescription"),
                            json_data.getString("storyPicture"),
                            "1".equals(json_data.getString("storyIsPublished")));
                    storyArrayList.add(story);
                }

                localStoriesAdapter.notifyDataSetChanged();
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

    public ArrayList<Story> getStoryArrayList() {
        return storyArrayList;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        void onFragmentInteraction(Uri uri);
    }
}
