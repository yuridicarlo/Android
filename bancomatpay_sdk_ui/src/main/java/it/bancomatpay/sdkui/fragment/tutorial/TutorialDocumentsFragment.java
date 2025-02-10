package it.bancomatpay.sdkui.fragment.tutorial;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import it.bancomatpay.sdkui.databinding.FragmentTutorialDocumentsBinding;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomatpay.sdkui.utilities.TutorialFlowManager.TUTORIAL_HIDE_BUTTON_NEXT;

public class TutorialDocumentsFragment extends Fragment {

	private FragmentTutorialDocumentsBinding binding;
	private InteractionListener listener;

	private boolean hideButtonNext;

	public static TutorialDocumentsFragment newInstance(boolean hideButtonNext) {
		TutorialDocumentsFragment fragment = new TutorialDocumentsFragment();
		Bundle args = new Bundle();
		args.putBoolean(TUTORIAL_HIDE_BUTTON_NEXT, hideButtonNext);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			hideButtonNext = getArguments().getBoolean(TUTORIAL_HIDE_BUTTON_NEXT, false);
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = FragmentTutorialDocumentsBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		FullStackSdkDataManager.getInstance().putTutorialDocumentsAlreadyShown(true);

		if (hideButtonNext) {
			binding.buttonNext.setVisibility(View.GONE);
		}

		binding.buttonNext.setOnClickListener(new CustomOnClickListener(v -> {
			if (listener != null) {
				listener.onTutorialDocumentsClose();
			}
		}));
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		if (context instanceof TutorialDocumentsFragment.InteractionListener) {
			listener = (TutorialDocumentsFragment.InteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement TutorialDocumentsFragment.InteractionListener");
		}
	}

	public interface InteractionListener {
		void onTutorialDocumentsClose();
	}

}
