package com.intervale.testbvg;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by kbolotin on 09.03.2017.
 */

public abstract class FragmentBase extends Fragment {

    public static final int TYPE_FORMATTED_INVISIBLE_MASK = 1;

    public FragmentBase() {
    }

    @Override
    public void onStart() {
        super.onStart();
        hideKeyboard();
    }

    public void runOnUiThread(Runnable runnable) {
        Activity activity = getActivity();
        if (activity != null && runnable != null) {
            activity.runOnUiThread(runnable);
        }
    }

    public ScrollingActivity getMainActivity() {
        return  ((ScrollingActivity) getActivity());
    }

    public FragmentBase getFragmentBase() {
        return this;
    }

    public void hideKeyboard() {
        hideKeyboard(getActivity().getCurrentFocus());
    }

    public void hideKeyboard(View view) {
        if (view == null) view = getActivity().getCurrentFocus();
        hideKeyboard(view, getActivity().getApplicationContext());
    }

    public static void hideKeyboard(final View view, final Context context) {
        if (view == null) return;
        view.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    public static void showKeyboard(final Context context, final EditText editText) {
        editText.post(new Runnable() {
            public void run() {
                editText.clearFocus();
                editText.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (android.os.Build.VERSION.SDK_INT < 11) {
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
                } else {
                    inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
                editText.setSelection(editText.getText().length());
            }
        });
    }



    public void popBackStack(String name) {
        getFragmentManager().popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void popBackStack(int count) {
        for (int i = 1; i <= count; i++) {
//            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            String backStackId = getFragmentManager().getBackStackEntryAt(count - i).getName();
            getFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void popBackStackLast() {
        String backStackId = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1).getName();
        getFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void popBackStackToFirstFragment() {
        Fragment baseFragment = this;
        while (baseFragment.getParentFragment() != null) baseFragment = baseFragment.getParentFragment();

        int backStackId = baseFragment.getFragmentManager().getBackStackEntryAt(0).getId();
//        if (baseFragment.getFragmentManager().getBackStackEntryCount() > backStackId)
        baseFragment.getFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


    public void replaceFragment(int containerId, FragmentBase fragment) {
        getActivity().getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(containerId, fragment, fragment.getTagForFragment())
                .addToBackStack(getNameForBackStackTransaction())
                .commit();
    }

    protected void replaceFragmentAnim(int containerId, FragmentBase fragment) {
        getActivity().getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right, R.anim.slide_out_left, R.anim.slide_in_right)
                .replace(containerId, fragment, fragment.getTagForFragment())
                .addToBackStack(getNameForBackStackTransaction())
                .commit();
    }

    public void replaceFragmentWithoutStack(int containerId, FragmentBase fragment) {
        for (int i = 0; i < getActivity().getFragmentManager().getBackStackEntryCount(); ++i) {
            getActivity().getFragmentManager().popBackStack();
        }

        getActivity().getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(containerId, fragment, fragment.getTagForFragment())
                .commit();
    }

    public String getNameForBackStackTransaction() {
        return getClass().getName();
    }

    public String getTagForFragment() {
        return getClass().getSimpleName();
    }


    protected void setActionBarTitle(String s)
    {
//        getMainActivity().setActionBarTitle(s);
    }


    @Override
    public void onDestroyView() {
//        getMainActivity().setActionBarTitle(getString(R.string.app_name));
        super.onDestroyView();
    }

}
