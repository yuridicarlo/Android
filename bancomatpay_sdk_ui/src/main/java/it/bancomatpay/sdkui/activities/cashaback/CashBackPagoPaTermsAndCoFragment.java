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
import it.bancomatpay.sdkui.databinding.FragmentCashbackPagopaTermsAndCoBinding;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_TC_PAGOPA;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_ACCEPTED;

public class CashBackPagoPaTermsAndCoFragment extends GenericErrorFragment {

    protected static final String TAG = "FRAGMENT_TAG_PAGO_PA_TERMS_AND_CO";
    protected static final String PAGO_PA_TERM_AND_CO_URL = "PAGO_PA_TERM_AND_CO_URL";

    private FragmentCashbackPagopaTermsAndCoBinding binding;
    private OnButtonInteractionListener mListener;
    private String mPagoPaTermsAndConditionsUrl;

    public static CashBackPagoPaTermsAndCoFragment newInstance(String pagoPaTermsAndConditionsUrl) {
        CashBackPagoPaTermsAndCoFragment fragment = new CashBackPagoPaTermsAndCoFragment();
        Bundle args = new Bundle();
        args.putString(PAGO_PA_TERM_AND_CO_URL, pagoPaTermsAndConditionsUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPagoPaTermsAndConditionsUrl = getArguments().getString(PAGO_PA_TERM_AND_CO_URL, "");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCashbackPagopaTermsAndCoBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        applyInsets();

        binding.buttonNo.setOnClickListener(new CustomOnClickListener(v -> {
            HashMap<String, String> mapEventParams = new HashMap<>();
            mapEventParams.put(PARAM_ACCEPTED, "no");
            CjUtils.getInstance().sendCustomerJourneyTagEvent(getContext(), KEY_CASHBACK_TC_PAGOPA, mapEventParams, false);
            requireActivity().finish();
        }));
        binding.closeButton.setOnClickListener(new CustomOnClickListener(v -> requireActivity().finish()));
        binding.buttonYes.setOnClickListener(new CustomOnClickListener(v -> {
            if (mListener != null) {
                HashMap<String, String> mapEventParams = new HashMap<>();
                mapEventParams.put(PARAM_ACCEPTED, "yes");
                CjUtils.getInstance().sendCustomerJourneyTagEvent(getContext(), KEY_CASHBACK_TC_PAGOPA, mapEventParams, false);
                mListener.onPagoPaYesClicked();
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
        if (!TextUtils.isEmpty(mPagoPaTermsAndConditionsUrl)) {
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
                        binding.progressWebViewCashback.setVisibility(View.VISIBLE);
                    }
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if(binding != null) {
                        binding.progressWebViewCashback.setVisibility(View.GONE);
                    }
                    super.onPageFinished(view, url);
                }
            });

            binding.webViewTermsAndConditions.loadUrl(mPagoPaTermsAndConditionsUrl);
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
        if (context instanceof CashBackBPayTermsAndCoFragment.OnButtonInteractionListener) {
            mListener = (OnButtonInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CashBackPagoPaTermsAndCoFragment.OnButtonInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnButtonInteractionListener {
        void onPagoPaYesClicked();
    }

}