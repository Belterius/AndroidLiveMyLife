package Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import ClassPackage.GlobalState;
import lml.androidlivemylife.CreateStoryActivity;
import lml.androidlivemylife.EditYourStepActivity;
import lml.androidlivemylife.MainActivity;
import lml.androidlivemylife.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewStoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewStoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewStoryFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    private GlobalState gs;
    private ImageButton buttonCreateStep;
    private ImageButton buttonFinalizeStory;
    private final int STATIC_RETURN_FROM_CREATE = 1;

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public NewStoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewStoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewStoryFragment newInstance(String param1, String param2) {
        NewStoryFragment fragment = new NewStoryFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

        gs = new GlobalState();

    }

    @Override
    public void onResume() {
        super.onResume();

        if(this.gs.getMyAccount().getMyCurrentStory().getSteps().size() > 0 ){
            buttonFinalizeStory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_story, container, false);

        //Get the steps for the currentStory

        //New step button listener
        buttonCreateStep = (ImageButton) view.findViewById(R.id.your_story_plus_button);
        buttonCreateStep.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToNewStep();
            }
        });

        //Finalize new story button listener
        buttonFinalizeStory = (ImageButton) view.findViewById(R.id.your_story_next_button);
        buttonFinalizeStory.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finalizeStory();
            }
        });

        // Inflate the layout for this fragment
        return view;
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

    /**
     * Will go to the process to add a new step to the current story
     * @param v
     */
    public void newStep(View v){
        goToNewStep();
    }

    private void goToNewStep(){
        Intent nextView = new Intent(this.getContext(), EditYourStepActivity.class);
        startActivity(nextView);
    }

    /**
     * Open a new intent to set the attributes to the current story
     * and to publish it
     */
    private void finalizeStory(){
        Intent nextView = new Intent(this.getContext(), CreateStoryActivity.class);
        startActivityForResult(nextView, STATIC_RETURN_FROM_CREATE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (STATIC_RETURN_FROM_CREATE) : {
                if (resultCode == Activity.RESULT_OK) {
                    if(data.getBooleanExtra("isCreated", false)){
                        ((MainActivity)getActivity()).getNavigation().setSelectedItemId( R.id.navigation_home);
                    }
                }
                break;
            }
        }
    }
}
