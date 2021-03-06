package Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ClassPackage.GlobalState;
import lml.androidlivemylife.EditMyProfileActivity;
import lml.androidlivemylife.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyAccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyAccountFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private GlobalState gs;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyAccountFragment.
     */
    public static MyAccountFragment newInstance(String param1, String param2) {
        MyAccountFragment fragment = new MyAccountFragment();
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
    }

    @Override
    public void onResume() {
        super.onResume();
        initMyProfileView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        ImageButton button = (ImageButton) view.findViewById(R.id.show_profile_validate);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToEditMyProfilePage();
            }
        });
        return view;

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


    public void initMyProfileView(){
        ((TextView) getView().findViewById(R.id.show_profile_name)).setText(this.gs.getMyAccount().getFirstname() + " " + this.gs.getMyAccount().getLastname());
        ((TextView) getView().findViewById(R.id.show_profile_pseudo)).setText(this.gs.getMyAccount().getPseudo());
        ((TextView) getView().findViewById(R.id.show_profile_description)).setText(this.gs.getMyAccount().getDescription());
        TextView about = ((TextView) getView().findViewById(R.id.show_profile_about));
        about.setText(getString(R.string.about) + " " + this.gs.getMyAccount().getFirstname() + " " + this.gs.getMyAccount().getLastname());


        if(this.gs.getMyAccount().getPicture().equals("")){
            Picasso.with(this.getContext())
                    .load(R.drawable.users)
                    .placeholder(R.drawable.loading_gears)
                    .error(R.drawable.ic_menu_report_image)
                    .into(((ImageView)getView().findViewById(R.id.show_profile_picture)));
        }else{
            Picasso.with(this.getContext())
                    .load(this.gs.getMyAccount().getPicture())
                    .placeholder(R.drawable.loading_gears)
                    .error(R.drawable.ic_menu_report_image)
                    .into(((ImageView)getView().findViewById(R.id.show_profile_picture)));
        }
    }

    public void editMyProfile(View v){
        goToEditMyProfilePage();
    }

    private void goToEditMyProfilePage(){
        Intent nextView = new Intent(this.getContext(),EditMyProfileActivity.class);
        startActivity(nextView);
    }
}
