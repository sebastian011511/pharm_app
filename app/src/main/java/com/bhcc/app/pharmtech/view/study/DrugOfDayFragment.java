package com.bhcc.app.pharmtech.view.study;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bhcc.app.pharmtech.R;
import com.bhcc.app.pharmtech.data.MedicineLab;
import com.bhcc.app.pharmtech.data.model.Medicine;

/**
 *@author Nils Johnson
 */

public class DrugOfDayFragment extends DialogFragment
{
    private final static String ARG_MEDICINE_NAME = "medicine_name";

    private TextView tvDrugName;
    private Button btnShowCard;
    private Button btnDontShowCard;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final String medicineName = (String) getArguments().getSerializable(ARG_MEDICINE_NAME);


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_drug_of_day, null);
        tvDrugName = (TextView) v.findViewById(R.id.drugOfDayTitle);
        tvDrugName.setText(medicineName);

        btnShowCard = (Button) v.findViewById(R.id.button_show_drug_card);
        btnShowCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent i = CardActivity.newIntent(getContext(), medicineName);
                startActivity(i);
                getDialog().dismiss();
            }
        });

        btnDontShowCard = (Button) v.findViewById(R.id.button_dont_show_drug_card);
        btnDontShowCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getDialog().dismiss();
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.drug_of_day_title)
                .create();
    }


   public static DrugOfDayFragment newInstance(String medicine)
   {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEDICINE_NAME, medicine);
        DrugOfDayFragment fragment = new DrugOfDayFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
