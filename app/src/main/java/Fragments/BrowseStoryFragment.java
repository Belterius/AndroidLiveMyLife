package Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import API_request.RequestClass;
import ClassPackage.GlobalState;
import ClassPackage.MyUser;
import ClassPackage.Step;
import ClassPackage.Story;
import ClassPackage.ToastClass;
import lml.androidlivemylife.StartStoryActivity;
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

    final public String TAG = "browseStory";

    private GlobalState gs;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Story myStoryToPlay;

    private OnFragmentInteractionListener mListener;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getStoryToPlay();
    }

    /**
     * Sends the request to the API server - gets the data about the story to play
     */
    public void getStoryToPlay(){
        Map<String, String> dataToPass = new HashMap<>();
        dataToPass.put("action", "getStoryToPlayWithSteps");
        dataToPass.put("storyId", "27");

        RequestClass.doRequestWithApi(this.getActivity().getApplicationContext(), this.TAG,dataToPass, this::initializeStoryToPlay);
    }

    /**
     * Gets the data from the API server about the story to play
     * @param o
     * @return
     */
    public Boolean initializeStoryToPlay(JSONObject o){

        try {

            if(o.getInt("status") == 200 &&  o.getJSONObject("story") != null){
                JSONObject myStoryToPlay = o.getJSONObject("story");
                if(myStoryToPlay != null){

                    this.myStoryToPlay = new Story(
                            myStoryToPlay.getString("id"),
                            myStoryToPlay.getString("storyTitle"),
                            myStoryToPlay.getString("storyDescription"),
                            myStoryToPlay.getString("storyPicture")
                    );

                    JSONArray mySteps = myStoryToPlay.getJSONArray("steps");
                    if(mySteps != null){

                        for (int i = 0; i < mySteps.length(); i++) {
                            JSONObject step = mySteps.getJSONObject(i);
                            this.myStoryToPlay.addStep(
                                    new Step(
                                            step.getString("stepId"),
                                            step.getString("stepPicture"),
                                            step.getString("stepGpsData"),
                                            step.getString("stepDescription")
                                    )
                            );
                        }
                    }

                    JSONObject myAuthor = myStoryToPlay.getJSONObject("user");
                    if(myAuthor != null){
                        this.myStoryToPlay.setAuthor(new MyUser(
                                myAuthor.getString("id"),
                                myAuthor.getString("email"),
                                myAuthor.getString("pseudo"),
                                myAuthor.getString("firstname"),
                                myAuthor.getString("lastname"),
                                myAuthor.getString("description"),
                                myAuthor.getString("photo")
                        ));
                    }

                    this.myStoryToPlay.setLikedByThisUser(myStoryToPlay.getBoolean("isLikedByMe"));
                }
                Button button = (Button) getView().findViewById(R.id.test_play_story);
                button.setVisibility(View.VISIBLE);

                return true;
            }else{
                ToastClass.toastError(this.getActivity(), o.getString("feedback"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        gs = new GlobalState();

    }

    public void goToPlayStory(){
        Intent nextView = new Intent(this.getContext(), StartStoryActivity.class);
        nextView.putExtra("storyToPlay", this.myStoryToPlay);
        startActivity(nextView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_browse_story, container, false);
        Button button = (Button) v.findViewById(R.id.test_play_story);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToPlayStory();
            }
        });

        return v;
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
