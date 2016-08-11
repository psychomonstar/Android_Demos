package com.czl.localweather.util;

import com.android.volley.toolbox.ImageLoader.ImageCache;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * ͼƬ���洦��
 *
 */
public class BitmapCacheUtil implements ImageCache {
	private LruCache<String, Bitmap> mCache;

	public BitmapCacheUtil() {
		int maxSize = 10 * 1024 * 1024;
		mCache = new LruCache<String, Bitmap>(maxSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
			}
		};
	}

	@Override
	public Bitmap getBitmap(String url) {
		return mCache.get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		mCache.put(url, bitmap);
	}

}
