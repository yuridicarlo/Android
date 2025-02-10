package it.bancomatpay.sdkui.utilities;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.StyleSpan;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationRestriction;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.DetailSearchRequest;
import com.huawei.hms.site.api.model.DetailSearchResponse;
import com.huawei.hms.site.api.model.HwLocationType;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hms.site.api.model.TextSearchRequest;
import com.huawei.hms.site.api.model.TextSearchResponse;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.model.AbstractPaymentData;
import it.bancomatpay.sdkui.model.MerchantDisplayData;

public class PlacesClientUtil {

    public static final String LAT_LNG_SEPARATOR = "-";
    private static final String TAG = PlacesClientUtil.class.getSimpleName();

    private static PlacesClientUtil instance;
    private static PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;

    private SearchService searchService;

    public static PlacesClientUtil getInstance() {
        if (instance == null) {
            instance = new PlacesClientUtil();
        }
        return instance;
    }

    public void loadBackgroundMerchant(Activity activity, MerchantImageLoadingListener listener, AbstractPaymentData paymentData, String constraint) {

        String googleApiKey;

        if (BancomatFullStackSdk.getInstance().hasGooglePlayServices() && !TextUtils.isEmpty(googleApiKey = getGoogleApiKey(activity))) {

            Places.initialize(activity, googleApiKey);
            placesClient = Places.createClient(activity);

            //layoutMerchantImage.setVisibility(View.INVISIBLE);

            double latitude = ((MerchantDisplayData) paymentData.getDisplayData()).getLatitude();
            double longitude = ((MerchantDisplayData) paymentData.getDisplayData()).getLongitude();

            //latitude = 45.070145;
            //longitude = 7.675347;

            LocationRestriction restriction = RectangularBounds.newInstance(
                    new LatLng(latitude - 0.005, longitude - 0.05),
                    new LatLng(latitude + 0.005, longitude + 0.05));

            this.sessionToken = AutocompleteSessionToken.newInstance();
            FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                    // Call either setLocationBias() OR setLocationRestriction().
                    .setLocationRestriction(restriction)
                    .setCountry("IT")
                    //.setTypeFilter(TypeFilter.ADDRESS)
                    .setSessionToken(this.sessionToken)
                    .setQuery(constraint)
                    .build();

            String nameConstraint = paymentData.getDisplayData().getTitle();
            //String nameConstraint = "Pizzeria Pecchia";

            String latLng = latitude + LAT_LNG_SEPARATOR + longitude;
            placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            FindAutocompletePredictionsResponse findAutocompletePredictionsResponse = task.getResult();
                            if (findAutocompletePredictionsResponse != null) {
                                AutocompletePrediction prediction;
                                if (!findAutocompletePredictionsResponse.getAutocompletePredictions().isEmpty()) {
                                    prediction = findAutocompletePredictionsResponse.getAutocompletePredictions().get(0);
                                    CustomLogger.d(TAG, prediction.getPlaceId());
                                    if (prediction.getFullText(new StyleSpan(Typeface.NORMAL)).toString().toLowerCase()
                                            .contains(nameConstraint.toLowerCase())) {
                                        CustomLogger.d(TAG, "Contains!");
                                    }
                                    getGooglePlacePhoto(activity, listener, latLng, prediction.getPlaceId());
                                }
                            }
                        } else {
                            CustomLogger.e(TAG, "findAutocompletePredictions generic error");
                        }
                    })
                    .addOnFailureListener(e -> CustomLogger.e(TAG, "findAutocompletePredictions failure: " + e.getMessage()));

        } else if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {

            String nameConstraint = paymentData.getDisplayData().getTitle();

            // Instantiate the SearchService object.
            searchService = SearchServiceFactory.create(activity, Uri.encode(activity.getString(R.string.HUAWEI_API_KEY)));
            // Create a request body.
            TextSearchRequest request = new TextSearchRequest();
            request.setQuery(constraint);

            double latitude = ((MerchantDisplayData) paymentData.getDisplayData()).getLatitude();
            double longitude = ((MerchantDisplayData) paymentData.getDisplayData()).getLongitude();

            //latitude = 45.1036111;
            //longitude = 7.668807;

            Coordinate location = new Coordinate(latitude, longitude);
            request.setLocation(location);
            request.setRadius(200);
            request.setHwPoiType(HwLocationType.ADDRESS);
            request.setCountryCode("IT");
            request.setLanguage("it");
            request.setPageIndex(1);
            request.setPageSize(1);

            String latLng = latitude + LAT_LNG_SEPARATOR + longitude;

            // Create a search result listener.
            SearchResultListener<TextSearchResponse> resultListener = new SearchResultListener<TextSearchResponse>() {
                // Return search results upon a successful search.
                @Override
                public void onSearchResult(TextSearchResponse results) {
                    if (results == null || results.getTotalCount() <= 0) {
                        return;
                    }
                    List<Site> sites = results.getSites();
                    if (sites == null || sites.size() == 0) {
                        return;
                    }
                    for (Site site : sites) {
                        CustomLogger.i(TAG, String.format("siteId: '%s', name: %s\r\n", site.getSiteId(), site.getName()));
                    }

                    Site prediction = sites.get(0);
                    CustomLogger.d(TAG, prediction.getSiteId());
                    if (prediction.getFormatAddress().toLowerCase()
                            .contains(nameConstraint.toLowerCase())) {
                        CustomLogger.d(TAG, "Contains!");
                    }
                    getHuaweiPlacePhoto(listener, latLng, prediction.getSiteId());
                }

                // Return the result code and description upon a search exception.
                @Override
                public void onSearchError(SearchStatus status) {
                    CustomLogger.i(TAG, "Error : " + status.getErrorCode() + " " + status.getErrorMessage());
                }
            };
            // Call the place search API.
            searchService.textSearch(request, resultListener);

        }
    }

    public void loadBackgroundMerchant(Activity activity, MerchantImageLoadingListener listener, double latitude, double longitude, String constraint) {

        String googleApiKey;

        if (BancomatFullStackSdk.getInstance().hasGooglePlayServices() && !TextUtils.isEmpty(googleApiKey = getGoogleApiKey(activity))) {

            Places.initialize(activity, googleApiKey);
            placesClient = Places.createClient(activity);

            //layoutMerchantImage.setVisibility(View.INVISIBLE);

            //latitude = 45.070145;
            //longitude = 7.675347;

            LocationRestriction restriction = RectangularBounds.newInstance(
                    new LatLng(latitude - 0.005, longitude - 0.05),
                    new LatLng(latitude + 0.005, longitude + 0.05));

            this.sessionToken = AutocompleteSessionToken.newInstance();
            FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                    // Call either setLocationBias() OR setLocationRestriction().
                    .setLocationRestriction(restriction)
                    .setCountry("IT")
                    //.setTypeFilter(TypeFilter.ADDRESS)
                    .setSessionToken(this.sessionToken)
                    .setQuery(constraint)
                    .build();

            String latLng = latitude + LAT_LNG_SEPARATOR + longitude;
            placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            FindAutocompletePredictionsResponse findAutocompletePredictionsResponse = task.getResult();
                            if (findAutocompletePredictionsResponse != null) {
                                AutocompletePrediction prediction;
                                if (!findAutocompletePredictionsResponse.getAutocompletePredictions().isEmpty()) {
                                    prediction = findAutocompletePredictionsResponse.getAutocompletePredictions().get(0);
                                    CustomLogger.d(TAG, prediction.getPlaceId());
                                    getGooglePlacePhoto(activity, listener, latLng, prediction.getPlaceId());
                                }
                            }
                        } else {
                            CustomLogger.e(TAG, "findAutocompletePredictions generic error");
                        }
                    })
                    .addOnFailureListener(e -> CustomLogger.e(TAG, "findAutocompletePredictions failure: " + e.getMessage()));
        } else if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {

            // Instantiate the SearchService object.
            searchService = SearchServiceFactory.create(activity, Uri.encode(activity.getString(R.string.HUAWEI_API_KEY)));
            // Create a request body.
            TextSearchRequest request = new TextSearchRequest();
            request.setQuery(constraint);

            Coordinate location = new Coordinate(latitude, longitude);
            request.setLocation(location);
            request.setRadius(200);
            request.setHwPoiType(HwLocationType.ADDRESS);
            request.setCountryCode("IT");
            request.setLanguage("it");
            request.setPageIndex(1);
            request.setPageSize(1);

            String latLng = latitude + LAT_LNG_SEPARATOR + longitude;

            // Create a search result listener.
            SearchResultListener<TextSearchResponse> resultListener = new SearchResultListener<TextSearchResponse>() {
                // Return search results upon a successful search.
                @Override
                public void onSearchResult(TextSearchResponse results) {
                    if (results == null || results.getTotalCount() <= 0) {
                        return;
                    }
                    List<Site> sites = results.getSites();
                    if (sites == null || sites.size() == 0) {
                        return;
                    }
                    for (Site site : sites) {
                        CustomLogger.i(TAG, String.format("siteId: '%s', name: %s\r\n", site.getSiteId(), site.getName()));
                    }

                    Site prediction = sites.get(0);
                    CustomLogger.d(TAG, prediction.getSiteId());
                    getHuaweiPlacePhoto(listener, latLng, prediction.getSiteId());
                }

                // Return the result code and description upon a search exception.
                @Override
                public void onSearchError(SearchStatus status) {
                    CustomLogger.i(TAG, "Error : " + status.getErrorCode() + " " + status.getErrorMessage());
                }
            };
            // Call the place search API.
            searchService.textSearch(request, resultListener);

        }
    }

    private void getGooglePlacePhoto(Activity activity, MerchantImageLoadingListener listener, String latLng, String placeId) {
        // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
        List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);

        // Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        FetchPlaceRequest placeRequest =
                FetchPlaceRequest.builder(placeId, fields)
                        .setSessionToken(this.sessionToken)
                        .build();

        placesClient.fetchPlace(placeRequest)
                .addOnSuccessListener((response) -> {

                    this.sessionToken = null;
                    Place place = response.getPlace();

                    if (place.getPhotoMetadatas() != null && !place.getPhotoMetadatas().isEmpty()) {

                        // Get the photo metadata.
                        PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);

                        // Get the attribution text.
                        //String attributions = photoMetadata.getAttributions();

                        Point point = new Point();
                        activity.getWindowManager().getDefaultDisplay().getSize(point);

                        // Create a FetchPhotoRequest.
                        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                .setMaxWidth(point.x + 200) // Optional.
                                //.setMaxHeight(300) // Optional.
                                .build();
                        placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                            Bitmap bitmap = fetchPhotoResponse.getBitmap();
                            listener.merchantImageLoaded(bitmap, true);
                        }).addOnFailureListener((exception) -> {
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                // Handle error with given status code.
                                CustomLogger.e(TAG, "Place not found, apiException statusCode: " + apiException.getStatusCode());
                                CustomLogger.e(TAG, "Place not found, exception: " + exception.getMessage());
                            }
                        });

                    }
                })
                .addOnFailureListener(e -> {
                    CustomLogger.e(TAG, "PlacesClientUtil fetchPlace error: " + e.getMessage());
                    this.sessionToken = null;
                });
    }

    private void getHuaweiPlacePhoto(MerchantImageLoadingListener listener, String latLng, String placeId) {
        // Create a request body.
        DetailSearchRequest request = new DetailSearchRequest();
        request.setSiteId(placeId);
        request.setLanguage("it");
        // Create a search result listener.
        SearchResultListener<DetailSearchResponse> resultListener = new SearchResultListener<DetailSearchResponse>() {
            // Return search results upon a successful search.
            @Override
            public void onSearchResult(DetailSearchResponse result) {
                Site site;
                if (result == null || (site = result.getSite()) == null) {
                    return;
                }
                CustomLogger.i(TAG, String.format("siteId: '%s', name: %s\r\n", site.getSiteId(), site.getName()));

                if (result.getSite() != null && result.getSite().getPoi() != null && result.getSite().getPoi().getPhotoUrls() != null) {
                    String[] photos = result.getSite().getPoi().getPhotoUrls();
                    if (photos.length > 0) {
                        try {
                            Bitmap bitmap = Picasso.get().load(photos[0]).get();
                            listener.merchantImageLoaded(bitmap, true);
                        } catch (IOException e) {
                            CustomLogger.e(TAG, "Error in getting bitmap image from url: " + photos[0]);
                        }
                    }
                }
            }

            // Return the result code and description upon a search exception.
            @Override
            public void onSearchError(SearchStatus status) {
                CustomLogger.i(TAG, "Error : " + status.getErrorCode() + " " + status.getErrorMessage());
            }
        };
        // Call the place details search API.
        searchService.detailSearch(request, resultListener);
    }

    private String getGoogleApiKey(Activity activity) {
        String googleApiKey = "";
        try {
            Bundle bundle = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA).metaData;
            googleApiKey = bundle.getString("com.google.android.geo.API_KEY", "");
        } catch (PackageManager.NameNotFoundException e) {
            CustomLogger.e(TAG, "Error getting google api key: " + e.getMessage());
        }
        return googleApiKey;
    }

    public interface MerchantImageLoadingListener {
        void merchantImageLoaded(Bitmap bitmap, boolean animate);
    }

}
