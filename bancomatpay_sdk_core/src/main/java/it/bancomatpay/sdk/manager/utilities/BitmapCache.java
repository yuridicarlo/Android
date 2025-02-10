package it.bancomatpay.sdk.manager.utilities;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;

import java.io.InputStream;

public class BitmapCache {

    private LruCache<Uri, Bitmap> mMemoryCache;
    private LruCache<Uri, Bitmap> mThumbnailCache;
    private static final String TAG = BitmapCache.class.getSimpleName();
    private static BitmapCache instance;

    public static synchronized BitmapCache getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new BitmapCache();
            return instance;
        }

    }

    public BitmapCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<Uri, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(@NonNull Uri key, @NonNull Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        mThumbnailCache = new LruCache<Uri, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(@NonNull Uri key, @NonNull Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }


    public Bitmap getThumbnail(Uri key, Context context) {
        Bitmap response = mThumbnailCache.get(key);
        if (response == null) {
            response = ThumbnailUtils.extractThumbnail(getBitmap(key, context), 512, 512);
            mThumbnailCache.put(key, response);
        }
        return response;
    }

    public Bitmap getFullSizeBitmapFromUri(Uri photoUri, Context context) {
        //returns full size picture...does not seem to work on all occasions

        if (photoUri != null) {
            AssetFileDescriptor afd = null;
            Bitmap bitmap = null;
            try {
                afd = context.getContentResolver().openAssetFileDescriptor(photoUri, "r");
                InputStream is = afd.createInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
                afd.close();
            } catch (Exception e) {
                CustomLogger.e(TAG, "File not found when trying to retrieve full sized photo using AssetFileDescriptor", e);
                return null;
            }

            return bitmap;
        }
        return null;
    }

    public Bitmap getBitmap(Uri uri, Context context) {
        Bitmap response = mMemoryCache.get(uri);
        if (response == null && !Uri.EMPTY.equals(uri)) {
            //   response = BitmapFactory.decodeResource(context.getResources(), key);
            response = getFullSizeBitmapFromUri(uri, context);
            mMemoryCache.put(uri, response);
        }
        return response;
    }

}
