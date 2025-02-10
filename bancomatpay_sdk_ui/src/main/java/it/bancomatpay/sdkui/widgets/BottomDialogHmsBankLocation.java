package it.bancomatpay.sdkui.widgets;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.SupportMapFragment;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.MarkerOptions;

import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.model.DisplayData;
import it.bancomatpay.sdkui.model.MerchantDisplayData;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class BottomDialogHmsBankLocation {

    private final DisplayData displayData;
    private final Context context;
    private final FragmentManager supportFragmentManager;

    private SupportMapFragment hmsMapFragment;
    private HuaweiMap hMap;

    private BottomSheetBehavior<?> behavior;
    private BottomSheetDialog dialog;

    public BottomDialogHmsBankLocation(AppCompatActivity activity, DisplayData displayData) {
        this.context = activity;
        this.supportFragmentManager = activity.getSupportFragmentManager();
        this.displayData = displayData;
    }

    public void showDialog() {
        dialog = new BottomSheetDialog(context, R.style.BottomSheetCustom);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.hms_bank_location_bottomsheet_layout, null);

        dialog.setContentView(bottomSheetView);

        ImageView imageMapBackground = dialog.findViewById(R.id.image_map_background);
        //CircleImageView imageProfileCircle = dialog.findViewById(R.id.image_merchant_profile_circle);
        TextView textTitle = dialog.findViewById(R.id.text_bank_title);
        TextView textDescription = dialog.findViewById(R.id.text_bank_subtitle);

        MerchantDisplayData displayItemWithMap = (MerchantDisplayData) displayData;
        imageMapBackground.setOnClickListener(new CustomOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q="
                    + ((MerchantDisplayData) displayData).getLatitude()
                    + ","
                    + ((MerchantDisplayData) displayData).getLongitude()
                    + "(" + displayData.getTitle() + ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
            }
        }));

        hmsMapFragment = (SupportMapFragment) this.supportFragmentManager.findFragmentById(R.id.fragment_bank_static_map);
        hmsMapFragment.getMapAsync(huaweiMap -> {
            hMap = huaweiMap;

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                hMap.setMyLocationEnabled(false);
            }

            hMap.getUiSettings().setMapToolbarEnabled(false);
            hMap.getUiSettings().setAllGesturesEnabled(true);

            hMap.addMarker(new MarkerOptions()
                    .position(new LatLng(displayItemWithMap.getLatitude(), displayItemWithMap.getLongitude())));
            hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(displayItemWithMap.getLatitude(), displayItemWithMap.getLongitude()), 17));

            hMap.setOnMarkerClickListener(marker -> {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="
                        + displayItemWithMap.getLatitude()
                        + ","
                        + displayItemWithMap.getLongitude()
                        + "(" + displayData.getTitle() + ")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                }
                return false;
            });
        });

        textTitle.setText(displayData.getTitle());
        textDescription.setText(displayData.getItemInterface().getDescription());

        dialog.setOnDismissListener(dialog -> {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            this.supportFragmentManager.beginTransaction().remove(hmsMapFragment).commit();
        });

        dialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss();
            }
            return true;
        });

        behavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        dialog.show();
    }

    public boolean isVisible() {
        return dialog != null && dialog.isShowing();
    }

    public void dismiss() {
        this.supportFragmentManager.beginTransaction().remove(hmsMapFragment).commit();
        dialog.dismiss();
    }

}
