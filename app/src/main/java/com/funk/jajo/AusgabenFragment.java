package com.funk.jajo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AusgabenFragment extends Fragment {

    private View fragView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.fragView = inflater.inflate(R.layout.fragment_ausgaben, container, false);

        return this.fragView;
    }
}
