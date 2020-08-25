package com.funk.jajo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

public class SettingsFragment extends Fragment {
    private View fragView;
    private AppViewModel viewModel;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.fragView = inflater.inflate(R.layout.fragment_settings, container, false);

        if (this.getActivity() != null) {
            this.viewModel = ViewModelProviders.of(this.getActivity()).get(AppViewModel.class);
        }

        if (this.getActivity() instanceof MainActivity) {
            this.mainActivity = (MainActivity) this.getActivity();
        }

        return this.fragView;
    }
}
