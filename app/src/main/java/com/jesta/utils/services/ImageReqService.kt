package com.jesta.utils.services

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache

import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.android.volley.toolbox.Volley
import com.jesta.gui.activities.MainActivity

object ImageReqService {
    private val requestQueue: RequestQueue
    private val imageLoader: ImageLoader
    private val maxByteSize: Int

    init {
        val context = MainActivity.instance
        requestQueue = Volley.newRequestQueue(context)
        requestQueue.start()
        maxByteSize = calculateMaxByteSize(context)
        imageLoader = ImageLoader(
            requestQueue,
            object : ImageLoader.ImageCache {
                private val lruCache = object : LruCache<String, Bitmap>(maxByteSize) {
                    override fun sizeOf(url: String, bitmap: Bitmap): Int {
                        return bitmap.byteCount
                    }
                }

                @Synchronized
                override fun getBitmap(url: String): Bitmap? {
                    return lruCache.get(url)
                }

                @Synchronized
                override fun putBitmap(url: String, bitmap: Bitmap) {
                    lruCache.put(url, bitmap)
                }
            })
    }

    fun setImageFromUrl(networkImageView: NetworkImageView,url: String) {
        networkImageView.setImageUrl(url, imageLoader)
    }

    private fun calculateMaxByteSize(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenBytes = displayMetrics.widthPixels * displayMetrics.heightPixels * 4
        return screenBytes * 3
    }
}