package it.bancomatpay.sdkui.fragment.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;
import it.bancomatpay.sdkui.widgets.ToolbarSimple;

public class BplayFragment extends GenericErrorFragment {

	protected ToolbarSimple toolbar;
	View spaceTop;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home_bplay, container, false);

		toolbar = view.findViewById(R.id.toolbar_simple);
		spaceTop = view.findViewById(R.id.space_top);

		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		toolbar.setOnClickLeftImageListener(v ->
				BCMReturnHomeCallback.getInstance().getReturnHomeListener()
						.goToHome(requireActivity(), false, false, false)
		);
		toolbar.post(() -> {
			toolbar.setRightImageVisibility(false);
			toolbar.setRightCenterImageVisibility(false);
		});

		int insetTop = BancomatDataManager.getInstance().getScreenInsetTop();
		if (insetTop != 0) {
			spaceTop.post(() -> {
				RelativeLayout.LayoutParams spaceParams = (RelativeLayout.LayoutParams) spaceTop.getLayoutParams();
				spaceParams.height = insetTop;
				spaceTop.setLayoutParams(spaceParams);
				spaceTop.requestLayout();
			});
		}
	}

}
