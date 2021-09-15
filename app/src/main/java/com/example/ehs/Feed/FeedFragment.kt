package com.example.ehs.Feed

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ehs.Closet.CodyFragment
import com.example.ehs.MainActivity
import com.example.ehs.R
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.fragment_feed.view.*
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList

class FeedFragment : Fragment() {
    private var a: Activity? = null

    val feedsList = mutableListOf<Feed>()

    var feedNumArr = ArrayList<String>()
    var feedIdArr = ArrayList<String>()
    var feedStyleArr = ArrayList<String>()
    var feedImgArr = ArrayList<String>()

    val adapter = FeedsListAdapter(feedsList)

    companion object {
        const val TAG : String = "피드 프레그먼트"
        fun newInstance() : FeedFragment { // newInstance()라는 함수를 호출하면 HomeFragment를 반환함
            return FeedFragment()
        }
    }

    // 프레그먼트가 메모리에 올라갔을때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "FeedFragment - onCreate() called")

        (activity as MainActivity).FeedImg()
        (activity as MainActivity).feed_like_check()

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "새로고침 실행")
        feedsList.clear()

        feedNumArr = AutoFeed.getFeedNum(a!!)
        feedIdArr = AutoFeed.getFeedId(a!!)
        feedStyleArr = AutoFeed.getFeedStyle(a!!)
        feedImgArr = AutoFeed.getFeedName(a!!)
        Log.d("ㅁㅁㅁㅁㅁ새로고침222", feedImgArr.toString())

        var a_bitmap : Bitmap? = null
        for (i in 0 until feedImgArr.size) {
            val uThread: Thread = object : Thread() {
                override fun run() {
                    try {
                        Log.d("Closet프래그먼터리스트123", feedImgArr[i])

                        val url = URL("http://13.125.7.2/img/cody/" + feedImgArr[i])

                        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection

                        conn.setDoInput(true)
                        conn.connect()
                        val iss: InputStream = conn.getInputStream()
                        a_bitmap = BitmapFactory.decodeStream(iss)

                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            uThread.start() // 작업 Thread 실행

            try {

                uThread.join()

                var feed = Feed(feedNumArr[i], feedIdArr[i], feedStyleArr[i], a_bitmap)
                feedsList.add(feed)


            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        adapter.notifyDataSetChanged()
    }



    // 프레그먼트를 안고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            a = context
        }
        Log.d(TAG, "FeedFragment - onAttach() called")
    }
    // 뷰가 생성되었을 때 화면과 연결
    // 프레그먼트와 레이아웃을 연결시켜주는 부분이다.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "FeedFragment - onCreateView() called")
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        view.tv_community.setOnClickListener {
            Log.d("FeedFragment", "커뮤니티로 이동")
            (activity as MainActivity?)!!.replaceFragment(CommunityFragment.newInstance())
        }



//
//
//        val resources: Resources = this.resources
//        val bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.cody1)
//        val bitmap2 = BitmapFactory.decodeResource(resources, R.drawable.cody2)
//        val bitmap3 = BitmapFactory.decodeResource(resources, R.drawable.cody3)
//        val bitmap4 = BitmapFactory.decodeResource(resources, R.drawable.cody4)
//
//
//
//        var feed1 = Feed("skychoi","#데일리", bitmap1)
//        var feed2 = Feed("jj","#스트릿", bitmap2)
//        var feed3 = Feed("eunjeong","#캐주얼", bitmap3)
//        var feed4 = Feed("tndlstkxk","#데일리", bitmap4)
//        feedsList.add(feed1)
//        feedsList.add(feed2)
//        feedsList.add(feed3)
//        feedsList.add(feed4)

        return view
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val gridLayoutManager = GridLayoutManager(a, 2)
        rv_userfeed.layoutManager = gridLayoutManager


        rv_userfeed.adapter = adapter
        adapter.notifyDataSetChanged()
        //recylerview 이거 fashionista.xml에 있는 변수
    }





}