package com.example.ehs.Feed

import com.android.volley.AuthFailureError
import com.android.volley.Response

import com.android.volley.toolbox.StringRequest


class FeedLikeSave_Request(
    feedNum: String,
    userId: String,
    feed_like_true: String,
    listener: Response.Listener<String?>?) : StringRequest(Method.POST, URL, listener, null) {

    private val map: MutableMap<String, String>

    @Throws(AuthFailureError::class)
    override fun getParams(): Map<String, String> {
        return map
    }

    companion object {
        //서버 URL 설정(php 파일 연동)
        private const val URL = "http://13.125.7.2/FeedLikeSave_Request.php"
    }

    //private Map<String, String>parameters;
    init {
        map = HashMap()
        map["feedNum"] = feedNum
        map["userId"] = userId
        map["feed_like_true"] = feed_like_true
    }
}
