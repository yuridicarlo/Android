package it.bancomatpay.sdkui.widgets;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import it.bancomatpay.sdk.manager.task.model.CashbackStatusData;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.cashaback.CashBackBPayTermsAndCoFragment;
import it.bancomatpay.sdkui.activities.cashaback.CashBackPagoPaTermsAndCoFragment;
import it.bancomatpay.sdkui.databinding.BottomDialogCashbackTcBinding;

public class CashbackTermsAndConditionsBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "BottomTermsAndConditionsDialogue";
    public static final String CASHBACK_STATUS_DATA_EXTRA = "CASHBACK_STATUS_DATA_EXTRA";

    private CashbackStatusData mCashbackStatusData;

    public static CashbackTermsAndConditionsBottomSheet newInstance(CashbackStatusData cashbackStatusData) {
        CashbackTermsAndConditionsBottomSheet fragment = new CashbackTermsAndConditionsBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(CASHBACK_STATUS_DATA_EXTRA, cashbackStatusData);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BottomDialogCashbackTcBinding binding = BottomDialogCashbackTcBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            mCashbackStatusData = (CashbackStatusData) getArguments().getSerializable(CASHBACK_STATUS_DATA_EXTRA);
            if (!mCashbackStatusData.isbPayTermsAndConditionsAccepted() && !TextUtils.isEmpty(mCashbackStatusData.getBpayTermsAndConditionsUrl())) {
                Fragment fragment = CashBackBPayTermsAndCoFragment.newInstance(mCashbackStatusData.getBpayTermsAndConditionsUrl());
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.container_fragment, fragment, TAG);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                if (!mCashbackStatusData.isPagoPaCashbackEnabled() && !TextUtils.isEmpty(mCashbackStatusData.getPagoPaTermsAndConditionsUrl())) {
                    Fragment fragment = CashBackPagoPaTermsAndCoFragment.newInstance(mCashbackStatusData.getPagoPaTermsAndConditionsUrl());
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.add(R.id.container_fragment, fragment, TAG);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        bottomSheetDialog.setOnShowListener(dialog -> {
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight(calculateDialogMaxHeight());
            behavior.setSkipCollapsed(false);
            behavior.setDraggable(false);
        });

        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(true);

        return bottomSheetDialog;
    }

    private int calculateDialogMaxHeight(){
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        return (int) (height*0.85);
    }

    public void onFragmentChanged() {
        if (!TextUtils.isEmpty(mCashbackStatusData.getPagoPaTermsAndConditionsUrl())) {
            Fragment fragment = CashBackPagoPaTermsAndCoFragment.newInstance(mCashbackStatusData.getPagoPaTermsAndConditionsUrl());
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.animator.slide_from_right, R.animator.slide_to_left, R.animator.slide_to_right, R.animator.slide_from_left);
            transaction.replace(R.id.container_fragment, fragment, TAG);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogCustomStyle;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().setOnKeyListener((DialogInterface dialog, int keyCode, KeyEvent event) -> {
            if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                requireActivity().finish();
                return true;
            }
            else return false;
        });
    }
}
