package com.intervale.testbvg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.intervale.testbvg.view.FloatingLabelEditText;

public class DefaultFragment extends FragmentBase {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_default, container, false);

//        if (PayDataHelper.getInstance().getS() == ""&&PayDataHelper.getInstance()==null) {
//            hideKeyboard();
            replaceFragmentWithoutStack(R.id.fragment, new StartFragment());
//        }else{
//            replaceFragmentWithoutStack(R.id.fragment, new PayFragment());
//        }

        return view;
    }
}
