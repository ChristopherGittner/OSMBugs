package org.gittner.osmbugs.fragments;

import android.app.Activity;
import android.app.Fragment;

public class BugEditFragment extends Fragment{

    FragmentInteractionListener mListener;

    public interface FragmentInteractionListener {
        public void onBugSaved(int platform);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}
