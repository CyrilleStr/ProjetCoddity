package com.coddity.grabthetrash.web

import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.toolbox.ImageLoader

class LruBitmapCache(maxSize: Int) : LruCache<String?, Bitmap>(maxSize), ImageLoader.ImageCache {
    override fun sizeOf(key: String?, value: Bitmap): Int {
        return value.rowBytes * value.height
    }

    override fun getBitmap(url: String): Bitmap? {
        return get(url)
    }

    override fun putBitmap(url: String, bitmap: Bitmap) {
        put(url, bitmap)
    }
}