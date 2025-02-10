package it.bancomat.pay.consumer.extended.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.bancomatpay.consumer.R;
import it.bancomatpay.sdkui.fragment.home.BplayFragment;

public class BplayFragmentExtended extends BplayFragment {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		if (view != null) {
			toolbar.setLeftImage(R.drawable.empty);
			toolbar.setLeftImageVisibility(false);
			toolbar.setOnClickLeftImageListener(null);
		}

		return view;
	}

}
