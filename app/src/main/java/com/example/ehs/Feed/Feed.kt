package com.example.ehs.Feed

import android.graphics.Bitmap

class Feed(val feedNum: String, var userprofileImg: Bitmap, val userID: String, val styletag: String, val feedImg: Bitmap?, val feedLikeCount: String, val feedUnlikeCount: String)

//val userphoto : Bitmap, val feedphoto : Bitmap,
// 일단 Int로 받는ㄷ 비트맵으로 바꿔주기
// 검색 키워드 kotlin bitmap