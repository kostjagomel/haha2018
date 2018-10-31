package com.intervale.testbvg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.intervale.testbvg.https.HttpHelper;
import com.intervale.testbvg.view.FloatingLabelEditText;

public class PayFragment extends FragmentBase {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pay_fragment, container, false);

        final FloatingLabelEditText panField = (FloatingLabelEditText) view.findViewById(R.id.pan_card);
        panField.getEditText().setType(TYPE_FORMATTED_INVISIBLE_MASK);
        panField.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        panField.getEditText().setMask("dddd dddd dddd dddd");

        final FloatingLabelEditText expatyDate = (FloatingLabelEditText) view.findViewById(R.id.expaty_date);
        expatyDate.getEditText().setType(TYPE_FORMATTED_INVISIBLE_MASK);
        expatyDate.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        expatyDate.getEditText().setMask("dd/dd");

        final FloatingLabelEditText cvc = (FloatingLabelEditText) view.findViewById(R.id.cvc);
        cvc.getEditText().setType(TYPE_FORMATTED_INVISIBLE_MASK);
        cvc.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        cvc.getEditText().setMask("ddd");

        final FloatingLabelEditText amount = (FloatingLabelEditText) view.findViewById(R.id.amount);
        amount.getEditText().setType(TYPE_FORMATTED_INVISIBLE_MASK);
        amount.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        amount.getEditText().setMask("ddddddddd BYN");

        Button nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();
                replaceFragmentWithoutStack(R.id.fragment, new PayFinshFramgment());


            }
        });

        return view;
    }
}
