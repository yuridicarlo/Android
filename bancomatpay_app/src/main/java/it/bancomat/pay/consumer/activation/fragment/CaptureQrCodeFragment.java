package it.bancomat.pay.consumer.activation.fragment;

import static com.google.zxing.client.android.Intents.Scan.MIXED_SCAN;
import static com.google.zxing.client.android.Intents.Scan.SCAN_TYPE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;

import java.util.List;

import it.bancomatpay.consumer.databinding.FragmentCaptureQrcodeBinding;

import static it.bancomat.pay.consumer.activation.activities.ActivationActivity.REQUEST_CODE_CAMERA_PERMISSION;

public class CaptureQrCodeFragment extends Fragment implements BarcodeCallback {

    private static final String ARG_BANK_UUID = "argBankUuid";

    private FragmentCaptureQrcodeBinding binding;

    private InteractionListener listener;
    private CaptureManager capture;
    private String bankUuid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bankUuid = getArguments().getString(ARG_BANK_UUID);
        }
    }

    public static CaptureQrCodeFragment newInstance(String bankUuid) {
        CaptureQrCodeFragment fragment = new CaptureQrCodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BANK_UUID, bankUuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCaptureQrcodeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initQrView();
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initCaptureView();
        }
    }

    public void initCamera() {
        initCaptureView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        if (capture != null) {
            capture.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (capture != null) {
            capture.onResume();
            binding.zxingBarcodeScanner.decodeSingle(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (capture != null) {
            capture.onPause();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            listener = (InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CaptureQrCodeFragment.InteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    protected void initQrView() {
        binding.zxingBarcodeScanner.setStatusText("");
        binding.zxingBarcodeScanner.getViewFinder().setVisibility(View.GONE);
    }

    private void initCaptureView() {
        capture = new CaptureManager(requireActivity(), binding.zxingBarcodeScanner);
        Intent intent = requireActivity().getIntent().putExtra(SCAN_TYPE, MIXED_SCAN);
        intent.setAction(Intents.Scan.ACTION);
        capture.initializeFromIntent(intent, null);
        capture.decode();
    }

    @Override
    public void barcodeResult(BarcodeResult result) {
        if (listener != null) {
            listener.verifyQrCode(result.getText(), bankUuid);
        }
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {
        //unused
    }

    public void resumeCapture() {
        binding.zxingBarcodeScanner.decodeSingle(this);
    }

    public interface InteractionListener {
        void verifyQrCode(String data, String bankUuid);
    }

}

