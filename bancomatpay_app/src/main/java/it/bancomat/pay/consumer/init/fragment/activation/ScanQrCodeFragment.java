package it.bancomat.pay.consumer.init.fragment.activation;

import static com.google.zxing.client.android.Intents.Scan.MIXED_SCAN;
import static com.google.zxing.client.android.Intents.Scan.SCAN_TYPE;

import android.Manifest;
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
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;

import java.util.List;

import it.bancomat.pay.consumer.init.ActivationActivity;
import it.bancomat.pay.consumer.network.dto.VerifyActionCodeData;
import it.bancomat.pay.consumer.utilities.NavHelper;
import it.bancomat.pay.consumer.utilities.UserMonitoringConstants;
import it.bancomat.pay.consumer.viewmodel.InitViewModel;
import it.bancomatpay.sdkui.viewModel.WindowViewModel;
import it.bancomatpay.consumer.databinding.FragmentScanQrCodeBinding;
import it.bancomatpay.sdk.manager.lifecycle.SingleListener;
import it.bancomatpay.sdkui.utilities.CjUtils;

import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.QRCODE_WRONG_ACTIVATION_CODE;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_ACTIVATION_READ_QR;


public class ScanQrCodeFragment extends Fragment implements BarcodeCallback {

    FragmentScanQrCodeBinding binding;

    private CaptureManager capture;
    private InitViewModel initViewModel;
    private WindowViewModel windowViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentScanQrCodeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel = new ViewModelProvider(requireActivity()).get(InitViewModel.class);
        windowViewModel = new ViewModelProvider(requireActivity()).get(WindowViewModel.class);
        initQrView();

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initCaptureView();
        }

        if(getActivity() instanceof ActivationActivity){
            ((ActivationActivity) getActivity()).initSmsReceiver();
        }

        initViewModel.getVerifyActivationCodeResponse().setListener(getViewLifecycleOwner(), new SingleListener<VerifyActionCodeData>() {
            @Override
            public void onSuccess(VerifyActionCodeData response) {
                windowViewModel.hideLoader();

                NavHelper.navigate(getActivity(), ScanQrCodeFragmentDirections.actionScanQrCodeFragmentToInsertOtpCodeFragment());
            }

            @Override
            public void onError(Throwable throwable) {
                windowViewModel.hideLoader();
                NavHelper.showSnackBarMessage(getActivity(), throwable);
                resumeCapture();
                //NavHelper.navigate(getActivity(), ScanQrCodeFragmentDirections.actionScanQrCodeFragmentToInsertOtpCodeFragment());
            }
        });

        binding.cancelButton.setOnClickListener(v -> NavHelper.popBackStack(getActivity()));
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
        CjUtils.getInstance().sendCustomerJourneyTagEvent(getContext(), KEY_ACTIVATION_READ_QR, null, true);
        initViewModel.userMonitoring(initViewModel.getBankUUID(),
                UserMonitoringConstants.ACTIVATION_TAG,
                UserMonitoringConstants.ACTIVATION_THROUGH_QRCODE,
                "");

        if(initViewModel.setActivationCodeFromQrCode(result.getText())) {
            windowViewModel.showLoader();
            initViewModel.verifyActivationCode();
            if(getActivity() instanceof ActivationActivity){
                ((ActivationActivity) getActivity()).initSmsReceiver();
            }
        }else {
            NavHelper.showSnackBarMessage(getActivity(), QRCODE_WRONG_ACTIVATION_CODE);
            resumeCapture();
        }
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {
        //unused
    }

    public void resumeCapture() {
        if (binding != null) {
            binding.zxingBarcodeScanner.post(() -> binding.zxingBarcodeScanner.decodeSingle(ScanQrCodeFragment.this));
        }
    }


}