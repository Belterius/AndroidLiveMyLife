package Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ClassPackage.GlobalState;
import ClassPackage.MyUser;
import ClassPackage.Story;
import lml.androidlivemylife.MainActivity;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    final public String TAG = "localStories";
    private ArrayList<Story> storiesList;

    private GlobalState gs;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LocalStoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocalStoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        gs = new GlobalState();

        storiesList = new ArrayList<Story>();
    }

    @Override
    public void onResume() {
        super.onResume();
        initLocalStoriesView();
    }

    public void initLocalStoriesView(){
        getPersonalStories();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_local_stories, container, false);
    }

    public void getPersonalStories(){
        String qs = "action=getPersonalStories"
                + "&idUser=" +gs.getMyAccount().getIdUser();
        gs.doRequestWithApi(this.getContext(), this.TAG, qs, this::getMyPersonalStories);
    }

    public Boolean getMyPersonalStories(JSONObject o){
        storiesList.clear(); //On remet la liste à zéro
        try {
            JSONArray stories = o.getJSONArray("stories");
            if(o.getInt("status") == 200 && stories != null){
                for(int i=0; i<stories.length(); i++){
                    JSONObject json_data = stories.getJSONObject(i);
                    Story story = new Story(json_data.getString("storyId"),
                            json_data.getString("storyTitle"),
                            json_data.getString("storyDescription"),
                            json_data.getString("storyPicture"),
                            Boolean.valueOf(json_data.getString("storyIsPublished")));
                    storiesList.add(story);
                }

                return true;
            }else{
                this.gs.toastError(this.getActivity(), o.getString("feedback"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
