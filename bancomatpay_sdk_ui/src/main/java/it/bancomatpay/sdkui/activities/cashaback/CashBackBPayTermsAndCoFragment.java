package it.bancomatpay.sdkui.activities.cashaback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdkui.databinding.FragmentCashbackBpayTermsAndCoBinding;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_TC_BPAY;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_ACCEPTED;

public class CashBackBPayTermsAndCoFragment extends GenericErrorFragment {

    protected static final String TAG = "FRAGMENT_TAG_BPAY_TERMS_AND_CO";
    private static final String BPAY_TERMS_AND_CO_URL = "BPAY_TERMS_AND_CO_URL";

    private FragmentCashbackBpayTermsAndCoBinding binding;
    private OnButtonInteractionListener mListener;
    private String mBpayTermsAndConditionsUrl;

    public static CashBackBPayTermsAndCoFragment newInstance(String bpayTermsAndConditionsUrl) {
        CashBackBPayTermsAndCoFragment fragment = new CashBackBPayTermsAndCoFragment();
        Bundle args = new Bundle();
        args.putString(BPAY_TERMS_AND_CO_URL, bpayTermsAndConditionsUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBpayTermsAndConditionsUrl = getArguments().getString(BPAY_TERMS_AND_CO_URL, "");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCashbackBpayTermsAndCoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        applyInsets();

        binding.buttonNo.setOnClickListener(new CustomOnClickListener(v -> {
            HashMap<String, String> mapEventParams = new HashMap<>();
            mapEventParams.put(PARAM_ACCEPTED, "no");
            CjUtils.getInstance().sendCustomerJourneyTagEvent(requireContext(), KEY_CASHBACK_TC_BPAY, mapEventParams, false);
            requireActivity().finish();
        }));
        binding.closeButton.setOnClickListener(new CustomOnClickListener(v -> requireActivity().finish()));
        binding.buttonYes.setOnClickListener(new CustomOnClickListener(v -> {
            if (mListener != null) {
                HashMap<String, String> mapEventParams = new HashMap<>();
                mapEventParams.put(PARAM_ACCEPTED, "yes");
                CjUtils.getInstance().sendCustomerJourneyTagEvent(requireContext(), KEY_CASHBACK_TC_BPAY, mapEventParams, false);
                mListener.onBpayYesClicked();
            }
        }));
        doRequest();
    }

    private void applyInsets() {
        int insetBottom = BancomatDataManager.getInstance().getScreenInsetBottom();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.spaceBottom.getLayoutParams();

        if (hasSoftKeys()) {
            binding.spaceBottom.post(() -> {
                if (isEdgeToEdgeEnabled(getActivity()) != 0) {
                    layoutParams.bottomMargin = 120;
                } else
                    layoutParams.bottomMargin = insetBottom;
                binding.spaceBottom.setLayoutParams(layoutParams);

            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void doRequest() {
        if (!TextUtils.isEmpty(mBpayTermsAndConditionsUrl)) {
            WebSettings webSettings = binding.webViewTermsAndConditions.getSettings();
            webSettings.setJavaScriptEnabled(false);
            webSettings.setDisplayZoomControls(false);
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setBuiltInZoomControls(false);
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            webSettings.setAllowFileAccess(false);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
            binding.webViewTermsAndConditions.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            binding.webViewTermsAndConditions.setBackgroundColor(Color.TRANSPARENT);
            binding.webViewTermsAndConditions.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                    if(binding != null) {
                        binding.progressWebViewBpay.setVisibility(View.VISIBLE);
                    }
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if(binding != null) {
                        binding.progressWebViewBpay.setVisibility(View.GONE);
                    }
                    super.onPageFinished(view, url);
                }
            });

            binding.webViewTermsAndConditions.loadUrl(mBpayTermsAndConditionsUrl);

            binding.webViewTermsAndConditions.setOnOverScrolledCallback((scrollX, scrollY, clampedX, clampedY) -> {
                if (clampedY) {
                    if (scrollY != 0) {
                        binding.buttonNo.setEnabled(true);
                        binding.buttonYes.setEnabled(true);
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnButtonInteractionListener) {
            mListener = (OnButtonInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CashBackBPayTermsAndCoFragment.OnButtonInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnButtonInteractionListener {
        void onBpayYesClicked();
    }

}
