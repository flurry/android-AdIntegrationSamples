package com.flurry.sample.gemini;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NativeAdChooserFragment extends ListFragment {

    private Callback mCallbackListener;

    public static final String TAG = NativeAdChooserFragment.class.getSimpleName();

    public static final NativeAdChooserFragment newInstance() {
        return new NativeAdChooserFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] adTypes = getActivity().getResources().getStringArray(R.array.native_ad_types);
        ArrayAdapter<String> adTypeListAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, adTypes);
        setListAdapter(adTypeListAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            try {
                mCallbackListener = (Callback) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() +
                        " must implement NativeAdChooserFragment.Callback");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbackListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mCallbackListener != null) {
            mCallbackListener.onAdDisplaySelected(position);
        }
    }

    interface Callback {
        void onAdDisplaySelected(int index);
    }
}
