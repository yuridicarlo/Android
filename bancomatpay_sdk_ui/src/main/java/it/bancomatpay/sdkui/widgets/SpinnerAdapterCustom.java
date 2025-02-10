package it.bancomatpay.sdkui.widgets;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class SpinnerAdapterCustom extends ArrayAdapter<String> {

    SpinnerAdapterCustom(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }

}
