package com.example.ehs.Fashionista

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.ehs.Login.AutoLogin
import com.example.ehs.R
import kotlinx.android.synthetic.main.activity_fashionista_profile.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class FashionistaProfile_Activity : AppCompatActivity() {

    val REQUEST_OPEN_GALLERY = 2
    lateinit var bitmap : Bitmap
    lateinit var resized : Bitmap

    lateinit var userId : String
    lateinit var fashionistaId : String

    val FashionistaFeedList = mutableListOf<FashionistaUserProfiles>()

    var FashionistaImgPathArr = ArrayList<String>()
    var FashionistaImgNameArr = ArrayList<String>()
    var a_bitmap : Bitmap? = null

    var adapter = FashionistaProfileAdapter(FashionistaFeedList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fashionista_profile)
        userId = AutoLogin.getUserId(this)

        val intent = intent
        fashionistaId = intent.getStringExtra("fashionistaId").toString()
        val arr = intent.getByteArrayExtra("fashionistaProfile")
        val fashionistaProfile = BitmapFactory.decodeByteArray(arr, 0, arr!!.size)
        iv_profile.setImageBitmap(fashionistaProfile)

        //Log.d("?????????", fashionistaProfile!!)
        tv_profileid.text = fashionistaId

        if(fashionistaId == userId) {
            btn_profilePlus.isVisible
        } else {
            btn_profilePlus.isVisible = false
        }

        /**
         * ????????? ?????? ????????? ??????????????? ??????
         */
        val toolbar = findViewById(R.id.toolbar_profile) as Toolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        // ????????? ?????? ?????? ?????? ??????
        ab.setDisplayHomeAsUpEnabled(true) // ???????????? ?????? ?????? ??????
        

        btn_profilePlus.setOnClickListener {
            openGallery()
        }


        val gridLayoutManager = GridLayoutManager(applicationContext, 3)
        rv_feed.layoutManager = gridLayoutManager
//        rv_feed.setHasFixedSize(true)

        rv_feed.adapter = adapter
        adapter.notifyDataSetChanged()


    }

    override fun onResume() {
        super.onResume()
        FashionistaImgPathArr = AutoPro.getplusImgPath(this)
        FashionistaImgNameArr = AutoPro.getplusImgName(this)

        Log.d("??????", FashionistaImgNameArr.toString())
        for (i in 0 until FashionistaImgNameArr.size) {
            val uThread: Thread = object : Thread() {
                override fun run() {
                    try {
//                        Log.d("zzzzasd", FashionistaImgPathArr[i])
                        val url = URL(FashionistaImgPathArr[i] + FashionistaImgNameArr[i])

                        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection

                        conn.setDoInput(true) //Server ???????????? ?????? ????????? ????????? ??????
                        conn.connect() //????????? ?????? ????????? ??? (connect() ???????????? ?????? ?????? ?????????)
                        val iss: InputStream = conn.getInputStream() //inputStream ??? ????????????
                        a_bitmap = BitmapFactory.decodeStream(iss) // Bitmap?????? ??????

                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            uThread.start() // ?????? Thread ??????

            try {
                uThread.join()
                count()
                var fashionistaFeed = FashionistaUserProfiles(a_bitmap)
                FashionistaFeedList.add(fashionistaFeed)

            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        adapter.notifyDataSetChanged()

    }

    /**
     * ?????? ???????????? ?????? ?????? ??????
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {

                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    } // ?????? ???????????? ?????? ?????? ???
    

    /**
     * ????????? ?????? ??????
     */
    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, REQUEST_OPEN_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) { //resultCode??? Ok??????
                REQUEST_OPEN_GALLERY -> { // requestcode??? REQUEST_OPEN_GALLERY??????
                    val currentImageUrl: Uri? = data?.data // data??? data????????? ?????????
//                    uploadImgName = getName(currentImageUrl)
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,
                            currentImageUrl)
                        resized = Bitmap.createScaledBitmap(bitmap!!, 500, 500, true)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        else {
            Toast.makeText(this, "?????????????????????.", Toast.LENGTH_SHORT).show()
        }

        val intent = Intent(this, ProfilePlus_Activity::class.java)
        val stream = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val plusImgArr = stream.toByteArray()
        intent.putExtra("plusImgArr", plusImgArr)
        startActivity(intent)

    }

    fun count() {
        var userId = fashionistaId
        var FollowerCount: String
        var PostCount: String
        var HashTag: String

        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {

                    var jsonObject = JSONObject(response)
                    val arr: JSONArray = jsonObject.getJSONArray("response")

                    for (i in 0 until arr.length()) {
                        val countObject = arr.getJSONObject(i)
                        FollowerCount = countObject.getString("FollowerCount")
                        PostCount = countObject.getString("PostCount")
                        HashTag = countObject.getString("HashTag")

                        Log.d("??????????????????,..1212?", FollowerCount)
                        Log.d("??????????????????,..1212?", PostCount)

                        tv_follower.text = FollowerCount
                        tv_post.text = PostCount
                        tv_hashtag.text = "#"+HashTag

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        val fashionistaProfileCount_Request = FashionistaProfileCount_Request(userId!!,
            responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(fashionistaProfileCount_Request)

    }



}