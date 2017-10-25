package com.bhcc.app.pharmtech.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bhcc.app.pharmtech.R;

/**
 * Created by nt628 on 10/23/2017.
 */

public class LegalFragment extends Fragment {

    public LegalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_legal, container, false);
        return v;
    }
}
