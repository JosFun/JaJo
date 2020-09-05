package com.funk.jajo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

public class SettingsFragment extends Fragment {
    private View fragView;
    private AppViewModel viewModel;
    private MainActivity mainActivity;

    private EditText firstPercentage;
    private EditText secondPercentage;
    private Button applyButton;

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

        this.firstPercentage = this.getActivity().findViewById(R.id.give_percentage_first);
        this.secondPercentage = this.getActivity().findViewById(R.id.give_percentage_second);

        this.applyButton = this.getActivity().findViewById(R.id.apply_settings_changes);
        this.applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( viewModel != null ) {
                    /* On Buttonclick: Try to parse strings in the EditFields into valid
                    * percentages and set first percentage permamnently in viewModel. */
                    String text1 = firstPercentage.getText().toString();
                    String text2 = secondPercentage.getText().toString();
                    double firstShare, secondShare;
                    try {
                        firstShare = Integer.parseInt( text1 ) / Math.pow(10, text1.length() + 1 );
                        secondShare = Integer.parseInt ( text2 ) / Math.pow(10, text2.length() + 1 );
                    } catch ( NumberFormatException n) {
                        n.printStackTrace();
                        firstShare = viewModel.getShareRatio();
                        secondShare = 1 - firstShare;
                    }

                    if ( firstShare + secondShare == 1 ) {
                        viewModel.setShareRatio( firstShare );
                    }
                }


            }
        });


        return this.fragView;
    }
}
