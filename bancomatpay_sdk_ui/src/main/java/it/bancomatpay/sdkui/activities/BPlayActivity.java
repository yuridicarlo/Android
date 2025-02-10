package it.bancomatpay.sdkui.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.manager.task.model.LoyaltyJwtData;
import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.ActivityBcmBplayBinding;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomatpay.sdkui.flowmanager.HomeFlowManager.LOYALTY_JWT_DATA;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_BPLAY_OPEN_WEBVIEW;

public class BPlayActivity extends GenericErrorActivity {

    private ActivityBcmBplayBinding binding;

    private static final String TAG = BPlayActivity.class.getSimpleName();

    //private static final String DESKTOP_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36";

    private LoyaltyJwtData loyaltyJwtData;
    private List<Uri> whitelistUrls;
    private Boolean loading = true;

    private static final int REQUEST_SELECT_FILE = 100;
    private ValueCallback<Uri[]> uploadMessage;

    ActivityResultLauncher<Intent> activityResultLauncherRequestSelectFile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_SELECT_FILE,result.getResultCode(),data);
            });

    public void scaleView(View v, float startScale, float endScale) {
        if(loading){
            Animation anim = new ScaleAnimation(
                    startScale, endScale, // Start and end values for the X axis scaling
                    startScale, endScale, // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
            anim.setFillAfter(true); // Needed to keep the result of the animation
            anim.setDuration(600);
            v.startAnimation(anim);
            binding.image.postDelayed(new Runnable() {
                @Override
                public void run() {
                    restoreView(binding.image,0.4f, 1f);
                }
            }, 600);
        } else {
            binding.image.setVisibility(View.GONE);

        }

    }

    public void restoreView(View v, float startScale, float endScale) {
        if(loading){
            Animation anim = new ScaleAnimation(
                    startScale, endScale, // Start and end values for the X axis scaling
                    startScale, endScale, // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
            anim.setFillAfter(true); // Needed to keep the result of the animation
            anim.setDuration(700);
            v.startAnimation(anim);
            binding.image.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scaleView(binding.image,1f, 0.4f);
                }
            }, 700);
        } else {
            binding.image.setVisibility(View.GONE);

        }

    }
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(BPlayActivity.class.getSimpleName());
        binding = ActivityBcmBplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        scaleView(binding.image,1f, 0.4f);


        binding.toolbarSimple.setOnClickLeftImageListener(v -> finish());
        binding.toolbarSimple.setRightCenterImageVisibility(false);
        binding.toolbarSimple.setRightImageVisibility(false);

        loyaltyJwtData = (LoyaltyJwtData) getIntent().getSerializableExtra(LOYALTY_JWT_DATA);
        whitelistUrls = new ArrayList<>();
        for (String url : loyaltyJwtData.getWhitelistUrls()) {
            whitelistUrls.add(Uri.parse(url));
        }

        WebSettings webSettings = binding.webViewBplay.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        //webSettings.setUserAgentString(DESKTOP_USER_AGENT);

        binding.webViewBplay.setBackgroundColor(Color.TRANSPARENT);
        binding.webViewBplay.setWebViewClient(new CustomWebViewClient());
        binding.webViewBplay.setWebChromeClient(new CustomWebChromeClient());

		if (loyaltyJwtData != null && !TextUtils.isEmpty(loyaltyJwtData.getBplayLoyaltyUrl())) {
			String bPlayLoyaltyUrl = loyaltyJwtData.getBplayLoyaltyUrl().concat("?lang=").concat(Locale.getDefault().getLanguage());
			CustomLogger.d(TAG, "Bplay url loyalty = " + bPlayLoyaltyUrl);
			CustomLogger.d(TAG, "Callback url loyalty = " + loyaltyJwtData.getCallbackUrl());

            try {
                String postData = "{ \"token\": \"" + URLEncoder.encode(loyaltyJwtData.getJwt(), "UTF-8") + "\" }";
                binding.webViewBplay.postUrl(bPlayLoyaltyUrl, postData.getBytes());

                FullStackSdkDataManager.getInstance().putBplayBalloonAlreadyShown(true);
                FullStackSdkDataManager.getInstance().putBplayStarGifAlreadyShown(true);
                CjUtils.getInstance().sendCustomerJourneyTagEvent(
                        this, KEY_BPLAY_OPEN_WEBVIEW, null, false);

            } catch (UnsupportedEncodingException e) {
                CustomLogger.e(TAG, "UnsupportedEncodingException: " + e.getMessage());
            }
        }

    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }

    public static boolean isAppAvailable(Context context, String appName)
    {
        PackageManager pm = context.getPackageManager();
        try
        {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    public class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.equals(loyaltyJwtData.getCallbackUrl())) {
                finish();
            }

            boolean isDomainInWhitelist = false;
            if(url.contains("whatsapp://send")){
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                try {
                    startActivity(i);

                } catch (android.content.ActivityNotFoundException e) {
                    CustomLogger.e(TAG, "Cannot open whatsapp");

                }

                return true;
            } else if(url.contains("https://telegram.me/share/url")){
                final String appName = "org.telegram.messenger";
                final boolean isAppInstalled = isAppAvailable(getApplicationContext(), appName);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                if (isAppInstalled) {
                    i.setPackage("org.telegram.messenger");
                }

                startActivity(i);

                return true;

            } else if (loyaltyJwtData != null && loyaltyJwtData.getWhitelistUrls() != null) {
                for (Uri domain : whitelistUrls) {
                    if (domain.getHost() != null && url.contains(domain.getHost())) {
                        isDomainInWhitelist = true;
                        break;
                    }
                }
            }
            Uri bplayUri;
            if (loyaltyJwtData != null) {
                bplayUri = Uri.parse(loyaltyJwtData.getBplayLoyaltyUrl());
                if (bplayUri.getHost() != null && url.contains(bplayUri.getHost())) {
                    isDomainInWhitelist = true;
                }
            }

            if (!isDomainInWhitelist) {
                if (Constants.SERVER_ENV.equals("PRODUCTION")) {
                    showWarningWhitelist();
                } else {
                    if (loyaltyJwtData != null) {
                        bplayUri = Uri.parse(loyaltyJwtData.getBplayLoyaltyUrl());
                        showWarningWhitelistAndUrl(bplayUri, url);
                    }
                }
            }

            return !isDomainInWhitelist;

            //"return true;" will NOT load the link
            // use "return false;" to allow it to load
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            loading = false;
        }
    }

    public class CustomWebChromeClient extends WebChromeClient {

        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            // make sure there is no existing message
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            }

            uploadMessage = filePathCallback;

            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                intent = fileChooserParams.createIntent();
            } else {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
            }
            try {
                activityResultLauncherRequestSelectFile.launch(intent);
            } catch (ActivityNotFoundException e) {
                uploadMessage = null;
                CustomLogger.e(TAG, "Cannot open file chooser");
                return false;
            }

            return true;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && event.getAction() == KeyEvent.ACTION_UP && binding.webViewBplay.canGoBack()) {
            binding.webViewBplay.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showWarningWhitelist() {
        if (!isFinishing() && !isDestroyed()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.warning_title))
                    .setMessage(getString(R.string.bplay_warning_open_url_not_in_whitelist))
                    .setPositiveButton(getString(R.string.ok), null);
            builder.show();
        }

    }

    private void showWarningWhitelistAndUrl(Uri bplayUri, String url) {
        if (!isFinishing() && !isDestroyed()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.warning_title))
                    .setMessage(getString(R.string.bplay_warning_open_url_not_in_whitelist) + "\n url: " + url + "\n host: " + bplayUri.getHost())
                    .setPositiveButton(getString(R.string.ok), null);
            builder.show();
        }

    }

    protected void manageResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_SELECT_FILE) {
            if (uploadMessage == null) return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            }
            uploadMessage = null;
        }
    }


}

