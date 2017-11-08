package com.kakao.cookie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


public class Record extends Fragment implements View.OnClickListener {
    private ImageButton Step;
    private ImageButton Balance;
    private ImageButton Activity;
    private ImageButton Bmi;
    private ImageButton Weight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        Step = (ImageButton) getView().findViewById(R.id.step);
        Balance = (ImageButton) getView().findViewById(R.id.balance);
        Activity = (ImageButton) getView().findViewById(R.id.activity);
        Bmi = (ImageButton) getView().findViewById(R.id.bmi);
        Weight = (ImageButton) getView().findViewById(R.id.weight);

        Step.setOnClickListener(this);
        Balance.setOnClickListener(this);
        Activity.setOnClickListener(this);
        Bmi.setOnClickListener(this);
        Weight.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if(v.equals(Step)){
            Intent intent = new Intent(getActivity(), Stepcount.class);
            startActivity(intent);
            //    getActivity().finish();
        }
        else if(v.equals(Balance)){
            Intent intent = new Intent(getActivity(), Balance.class);
            startActivity(intent);
            //    getActivity().finish();
        }
        else if(v.equals(Activity)){
            Intent intent = new Intent(getActivity(), Act.class);
            startActivity(intent);
            //    getActivity().finish();
        }
        else if(v.equals(Bmi)){
            Intent intent = new Intent(getActivity(), BmiActivity.class);
            startActivity(intent);
            //    getActivity().finish();
        }
        else if(v.equals(Weight)){
            Intent intent = new Intent(getActivity(), WeightActivity.class);
            startActivity(intent);
            //    getActivity().finish();
        }
    }
}
