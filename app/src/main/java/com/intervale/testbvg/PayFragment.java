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
        View view = inflater.inflate(R.layout.fragment_start, container, false);

//        final FloatingLabelEditText panField = (FloatingLabelEditText) view.findViewById(R.id.owner_number);
//        panField.getEditText().setType(TYPE_FORMATTED_INVISIBLE_MASK);
//        panField.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
//        panField.getEditText().setMask("dddd dd** **** dddd ddd");

        Button nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HttpHelper.getInstance().getToken(new HttpHelper.HttpCallback() {
                    @Override
                    public void response(String response) {
                        HttpHelper.getInstance().startPayment(response, new HttpHelper.HttpCallback() {
                            @Override
                            public void response(String response) {
                                Log.d("pay","ok");
                            }

                            @Override
                            public void error(String error, String description) {
                                Log.d("pay","ok");
                            }
                        });
                    }

                    @Override
                    public void error(String error, String description) {
                        Log.d("pay","ok");
                    }
                });

//                hideKeyboard();
//                replaceFragmentWithoutStack(R.id.fragment, WebViewFragment.newInstance("https://ya.ru","yandex"));

            }
        });

        return view;
    }
}
