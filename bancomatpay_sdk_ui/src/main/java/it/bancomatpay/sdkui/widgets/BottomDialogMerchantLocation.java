package it.bancomatpay.sdkui.widgets;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.HashMap;

import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.model.DisplayData;
import it.bancomatpay.sdkui.model.MerchantDisplayData;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_OPEN_MAP;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_ELAPSED;

public class BottomDialogMerchantLocation {

    private final DisplayData displayData;
    private final Context context;
    private final FragmentManager supportFragmentManager;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private BottomSheetBehavior<?> behavior;
    private BottomSheetDialog dialog;

    public BottomDialogMerchantLocation(AppCompatActivity activity, DisplayData displayData) {
        this.context = activity;
        this.supportFragmentManager = activity.getSupportFragmentManager();
        this.displayData = displayData;
    }

    public void showDialog() {
        dialog = new BottomSheetDialog(context, R.style.BottomSheetCustom);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.merchant_location_bottomsheet_layout, null);

        dialog.setContentView(bottomSheetView);

        ImageView imageMapBackground = dialog.findViewById(R.id.image_map_background);
        ImageView imageProfile = dialog.findViewById(R.id.image_merchant_profile);
        //CircleImageView imageProfileCircle = dialog.findViewById(R.id.image_merchant_profile_circle);
        TextView textTitle = dialog.findViewById(R.id.text_merchant_title);
        TextView textDescription = dialog.findViewById(R.id.text_merchant_subtitle);

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
        /*Picasso.get().load(
                MapsUtils.getUrlMap(context, displayItemWithMap.getLatitude(), displayItemWithMap.getLongitude()))
                .into(imageMapBackground);*/


        //SWITCH BETWEEN MAP FRAGMENT IN GMS AND HMS
        mapFragment = (SupportMapFragment) this.supportFragmentManager.findFragmentById(R.id.fragment_static_map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(false);
            }
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setAllGesturesEnabled(true);

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(displayItemWithMap.getLatitude(), displayItemWithMap.getLongitude())));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(displayItemWithMap.getLatitude(), displayItemWithMap.getLongitude()), 17));

            mMap.setOnMarkerClickListener(marker -> {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="
                        + displayItemWithMap.getLatitude()
                        + ","
                        + displayItemWithMap.getLongitude()
                        + "(" + displayData.getTitle() + ")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                }

                // Return false to indicate that we have not consumed the event and that we wish
                // for the default behavior to occur (which is for the camera to move such that the
                // marker is centered and for the marker's info window to open, if it has one).
                return false;

            });

        });


        Bitmap imageBitmap = displayData.getBitmap();
        if (imageBitmap != null) {
            imageProfile.setImageBitmap(imageBitmap);
            //imageProfileCircle.setImageBitmap(imageBitmap);
        } else {
            imageProfile.setImageResource(R.drawable.placeholder_merchant);
            //imageProfileCircle.setVisibility(View.INVISIBLE);
        }

        textTitle.setText(displayData.getTitle());
        textDescription.setText(displayData.getItemInterface().getDescription());

        dialog.setOnDismissListener(dialog -> {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            this.supportFragmentManager.beginTransaction().remove(mapFragment).commit();


            HashMap<String, String> mapEventParams = new HashMap<>();
            mapEventParams.put(PARAM_ELAPSED, CjUtils.getInstance().getP2BOpenMapTimeElapsed());
            CjUtils.getInstance().sendCustomerJourneyTagEvent(context, KEY_P2B_OPEN_MAP, mapEventParams, true);
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

        CjUtils.getInstance().startP2BOpenMap();
    }

    public boolean isVisible() {
        return dialog != null && dialog.isShowing();
    }

    public void dismiss() {
        this.supportFragmentManager.beginTransaction().remove(mapFragment).commit();
        dialog.dismiss();
    }

}
