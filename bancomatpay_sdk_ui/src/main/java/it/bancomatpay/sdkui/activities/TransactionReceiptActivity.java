package it.bancomatpay.sdkui.activities;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.TransactionType;
import it.bancomatpay.sdk.manager.utilities.BigDecimalUtils;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.MovementsReceiptBinding;
import it.bancomatpay.sdkui.flowmanager.TransactionsFlowManager;
import it.bancomatpay.sdkui.model.Transaction;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.StringUtils;

import static it.bancomatpay.sdk.manager.task.model.TransactionData.Status.ANN_POS;
import static it.bancomatpay.sdk.manager.task.model.TransactionData.Status.POS;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Mobile.INVALID_FILE_PROVIDER_AUTHORITY;

import org.greenrobot.eventbus.EventBus;

public class TransactionReceiptActivity extends GenericErrorActivity {

    private MovementsReceiptBinding binding;

    private static final String TAG = TransactionReceiptActivity.class.getSimpleName();


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                manageResult(1000);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(TransactionReceiptActivity.class.getSimpleName());
        binding = MovementsReceiptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getSerializableExtra(TransactionsFlowManager.TRANSACTION_DATA) != null) {

            Transaction transaction = (Transaction) getIntent().getSerializableExtra(TransactionsFlowManager.TRANSACTION_DATA);

            Bitmap bitmap = transaction.getBitmap();
            if (bitmap != null) {
                binding.contactMerchantImageProfile.setImageBitmap(bitmap);
            } else {
                binding.contactMerchantImageProfile.setImageResource(R.drawable.placeholder_merchant);
            }

            binding.textMerchantName.setText(transaction.getTitle());
            binding.textMerchantAddress.setText(transaction.getItemInterface().getDescription());

            if (StringUtils.getFormattedValue(transaction.getAmount()).contains("-")) {
                binding.transactionDetailPriceMerchant.setText(StringUtils.getFormattedValue(transaction.getAmount()).substring(1));
            } else {
                binding.transactionDetailPriceMerchant.setText(StringUtils.getFormattedValue(transaction.getAmount()));
            }
            String date = StringUtils.getDateStringFormatted(transaction.getDisplayDate(), "dd MMMM - HH:mm");
            binding.transactionDetailDateMerchant.setText(date);

            if (!TextUtils.isEmpty(transaction.getTransactionIban())) {
                  binding.textIbanMerchant.setText(transaction.getTransactionIban());
            } else {
                binding.textIbanView.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(transaction.getIdSct())) {
                binding.transactionIdTextMerchant.setText(transaction.getIdSct());
                if(isCashbackTransaction(transaction)){
                    binding.transactionIdLine.setVisibility(View.VISIBLE);
                }
            } else {
                binding.transactionIdView.setVisibility(View.GONE);
                if(isCashbackTransaction(transaction)){
                    binding.lineIban.setVisibility(View.VISIBLE);
                }
            }

            if (isCashbackTransaction(transaction)) {
                BigDecimal cashbackAmount = transaction.getCashbackAmount();
                NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ITALY);
                binding.transactionCashbackView.setVisibility(View.VISIBLE);
                binding.transactionCashbackText.setText(format.format(cashbackAmount));
            }

            if (!TextUtils.isEmpty(transaction.getFee())) {
                if (transaction.getFee().equals("0")) {
                    if (transaction.getTransactionStatus() == POS || transaction.getTransactionStatus() == ANN_POS) {
                        AnimationFadeUtil.startFadeInAnimationV1(binding.transactionFeeView, AnimationFadeUtil.DEFAULT_DURATION);
                        binding.transactionFee.setText(getString(R.string.fee_label, StringUtils.getFormattedValue(BigDecimalUtils.getBigDecimalFromCents(transaction.getFee()))));
                    }
                } else {
                    AnimationFadeUtil.startFadeInAnimationV1(binding.transactionFeeView, AnimationFadeUtil.DEFAULT_DURATION);
                    binding.transactionFee.setText(getString(R.string.fee_label, StringUtils.getFormattedValue(BigDecimalUtils.getBigDecimalFromCents(transaction.getFee()))));
                }
            } else
                AnimationFadeUtil.startFadeOutAnimationV1(binding.transactionFeeView, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);


            switch (transaction.getTransactionStatus()) {
                case PAG:
                    binding.textIbanLabelMerchant.setText(R.string.charge_to);
                    break;
                case STR:
                    binding.textIbanLabelMerchant.setText(R.string.accreditation);
                    binding.transactionDetailStatusMerchant.setText(getString(R.string.transaction_detail_money_starling));
                    break;
                case RIC:
                    binding.transactionDetailStatusMerchant.setText(getString(R.string.transaction_detail_money_received));
                    binding.textIbanLabelMerchant.setText(R.string.accreditation);
                    break;
                case ADD:
                    binding.textIbanLabelMerchant.setText(R.string.charge_to);
                    binding.transactionDetailStatusMerchant.setText(R.string.direct_debit_label);
                    binding.transactionReceiptTitle.setText(R.string.direct_debit_receipt_title);
                    break;
                case POS:
                    binding.textIbanLabelMerchant.setText(R.string.charge_to);
                    binding.transactionDetailStatusMerchant.setText(R.string.withdraw_transaction_detail_success);
                    binding.transactionReceiptTitle.setText(R.string.withdraw_pos_receipt_label);
                    binding.contactMerchantImageProfile.setImageResource(R.drawable.ricevuta_prelievo_pos);
                    binding.transactionFeeView.setVisibility(View.VISIBLE);
                    break;
                case ATM:
                    binding.textIbanLabelMerchant.setText(R.string.charge_to);
                    binding.transactionDetailStatusMerchant.setText(R.string.withdraw_transaction_detail_success);
                    binding.transactionReceiptTitle.setText(R.string.withdraw_pos_receipt_label);
                    binding.contactMerchantImageProfile.setImageResource(R.drawable.ricevuta_prelievo_atm);
                    break;
            }

        }

        binding.cardViewReceipt.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                binding.cardViewReceipt.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    Bitmap bitmap = getBitmapFromView(binding.cardViewReceipt,
                            binding.cardViewReceipt.getChildAt(0).getHeight(),
                            binding.cardViewReceipt.getChildAt(0).getWidth());

                    String fileName = "BCM_receipt" + ".jpeg";

                    File screenshot = null;
                    Uri uriImage = null;

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        screenshot = saveImageToInternalMemoryLegacy(fileName, bitmap);
                    } else {
                        uriImage = saveImageToInternalMemoryAPI29(fileName, bitmap);
                    }

                    String authority = BancomatDataManager.getInstance().getFileProviderAuthority();

                    if (!TextUtils.isEmpty(authority)) {

                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);

                        Uri contentUri;

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            contentUri = screenshot != null ? FileProvider.getUriForFile(TransactionReceiptActivity.this, authority, screenshot) : null;
                        } else {
                            contentUri = uriImage;
                        }

                        sharingIntent.setType("image/*");

                        sharingIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        sharingIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                            sharingIntent.setClipData(ClipData.newRawUri("", contentUri));
                            sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                        activityResultLauncher.launch(Intent.createChooser(sharingIntent, getString(R.string.share)));
                    } else {
                        showError(INVALID_FILE_PROVIDER_AUTHORITY);
                    }

                }, 200);

            }

        });
    }

    @Nullable
    private File saveImageToInternalMemoryLegacy(String fileName, Bitmap bitmap) {
        File file = new File(getCacheDir(), fileName);
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            CustomLogger.e(TAG, e.getMessage());
            return null;
        }

        return file;
    }

    private boolean isCashbackTransaction(Transaction transaction) {
        boolean isCashbackTransaction =  false;
        if ((transaction.getTransactionType() == (TransactionType.P2B) || transaction.getTransactionType() == (TransactionType.B2P)) && transaction.getCashbackAmount() != null) {
            isCashbackTransaction = true;
        }
        return isCashbackTransaction;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Nullable
    private Uri saveImageToInternalMemoryAPI29(String fileName, Bitmap bitmap) {

        OutputStream outputStream;
        try {

            String filePath = Environment.DIRECTORY_PICTURES + File.separator + "BancomatPaySharedImages";

            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, filePath);
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();

            return imageUri;

        } catch (Exception e) {
            CustomLogger.e(TAG, "Error saving bitmap " + e.getMessage());
            return null;
        }
    }

    private Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    protected void manageResult(int requestCode) {
        if (requestCode == 1000) {
           /* File dir = getCacheDir();
            File file = new File(dir, "BCM_receipt.png");
            boolean deleted = file.delete();
            CustomLogger.d(TAG, "Receipt.png deleted = " + deleted);*/
            finish();
        }
    }

}