package it.bancomatpay.sdkui.utilities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import it.bancomatpay.sdkui.viewModel.WindowViewModel;
import it.bancomatpay.sdk.R;

public class ExtendedProgressDialogFragment extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widgets_progress_dialog_transparent, container);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar_loading_qr);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.SRC_IN);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WindowViewModel windowViewModel = new ViewModelProvider(requireActivity()).get(WindowViewModel.class);
        windowViewModel.getLoader().observe(this, integer -> {
            if (integer == 0) {
                dismiss();
            }
        });
    }
}
