package com.intervale.testbvg;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.intervale.testbvg.view.FloatingLabelEditText;

public class StartFragment extends FragmentBase {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        if (PayDataHelper.getInstance()!=null&&PayDataHelper.getInstance().getS() != "") {
            replaceFragmentWithoutStack(R.id.fragment, new PayFragment());
        }

        final FloatingLabelEditText ownerNumber = (FloatingLabelEditText) view.findViewById(R.id.owner_number);
        ownerNumber.getEditText().setType(TYPE_FORMATTED_INVISIBLE_MASK);
        ownerNumber.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        ownerNumber.getEditText().setMask("+375_dd_ddd_dd_dd");

        final FloatingLabelEditText destinationNumber = (FloatingLabelEditText) view.findViewById(R.id.destination_number);
        destinationNumber.getEditText().setType(TYPE_FORMATTED_INVISIBLE_MASK);
        destinationNumber.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        destinationNumber.getEditText().setMask("+375_dd_ddd_dd_dd");

        Button nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Конверт для сбора денежных средств по ссылке - http://www.bvgteam.site/f395dc318c3d23 ");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent,"Поделиться"));

//                hideKeyboard();
//                replaceFragmentWithoutStack(R.id.fragment, new PayFragment());

//                HttpHelper.getInstance().getToken(new HttpHelper.HttpCallback() {
//                    @Override
//                    public void response(String response) {
//                        HttpHelper.getInstance().startPayment(response, new HttpHelper.HttpCallback() {
//                            @Override
//                            public void response(String response) {
//                                Log.d("pay","ok");
//                            }
//
//                            @Override
//                            public void error(String error, String description) {
//                                Log.d("pay","ok");
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void error(String error, String description) {
//                        Log.d("pay","ok");
//                    }
//                });

//                hideKeyboard();
//                replaceFragmentWithoutStack(R.id.fragment, WebViewFragment.newInstance("https://ya.ru","yandex"));

            }
        });

        return view;
    }
}
