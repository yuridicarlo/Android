package it.bancomat.pay.consumer.extended.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.bancomatpay.consumer.R;
import it.bancomatpay.sdkui.fragment.home.ServicesFragment;

public class ServicesFragmentExtended extends ServicesFragment {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		if (view != null) {
			toolbar.setLeftImageVisibility(false);
			toolbar.setLeftImage(R.drawable.empty);
			toolbar.setOnClickLeftImageListener(null);
		}

		return view;
	}
}
