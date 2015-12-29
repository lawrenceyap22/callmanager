package ph.intrepidstream.callmanager.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.service.CallManageService;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    private boolean detectEnabled;

    private Button buttonToggleDetect;
    private Button buttonExit;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonToggleDetect = (Button) getActivity().findViewById(R.id.buttonDetectToggle);
        buttonToggleDetect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setDetectEnabled(!detectEnabled);
            }
        });

        buttonExit = (Button) getActivity().findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setDetectEnabled(false);
                getActivity().finish();
            }
        });
    }

    private void setDetectEnabled(boolean enable) {
        detectEnabled = enable;
        Intent intent = new Intent(getContext(), CallManageService.class);
        if (enable) {
            // start detect service
            getContext().startService(intent);

            buttonToggleDetect.setText("Disable service");
        }
        else {
            // stop detect service
            getContext().stopService(intent);

            buttonToggleDetect.setText("Enable service");
        }
    }
}
