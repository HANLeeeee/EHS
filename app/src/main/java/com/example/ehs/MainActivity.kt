package com.example.ehs

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.ehs.Closet.*
import com.example.ehs.Fashionista.AutoPro
import com.example.ehs.Fashionista.FashionistaFragment
import com.example.ehs.Fashionista.FashionistaUser_Request
import com.example.ehs.Fashionista.FavoriteCheck_Request
import com.example.ehs.Feed.*
import com.example.ehs.Home.AutoHome
import com.example.ehs.Home.HomeFragment
import com.example.ehs.Login.AutoLogin
import com.example.ehs.Mypage.ClothesColor_Response
import com.example.ehs.Mypage.MypageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_closet.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    val TAG: String = "메인페이지"
    companion object {
        var mContext: Context? = null

    }

    lateinit var getLatitude : String
    lateinit var getLongitude : String
    lateinit var city: String

    private val REQUEST_ACCESS_FINE_LOCATION = 1000

    // 메인액티비티 클래스가 가지고 있는 멤버들
    private lateinit var homeFragment: HomeFragment
    private lateinit var fashionistaFragment: FashionistaFragment
    private lateinit var closetFragment: ClosetFragment
    private lateinit var feedFragment: FeedFragment
    private lateinit var mypageFragment: MypageFragment


    var userId: String? = ""
    val bundle = Bundle()

    private var backKeyPressedTime: Long = 0



    override fun onBackPressed() {
        //super.onBackPressed();
        // 기존 뒤로 가기 버튼의 기능을 막기 위해 주석 처리 또는 삭제

        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지났으면 Toast 출력
        // 2500 milliseconds = 2.5 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 1500) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show()
            return
        }
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지나지 않았으면 종료
        if (System.currentTimeMillis() <= backKeyPressedTime + 1500) {
            moveTaskToBack(true)
            finish()

        }
    }

    // 화면이 메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidThreeTen.init(this)
        mContext = this

        userId = AutoLogin.getUserId(this)

        // 바텀 네비게이션
        bottom_nav.setOnNavigationItemSelectedListener(onBottomNavItemSeletedListener)

        homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.fragments_frame, homeFragment)
            .commit() // add는 프레그먼트 추가해주는 것



        //권한설정
        setPermission()
        setLocation_Permission()


        FashionistaUser()
        favorite_check()
        feed_like_check()
        ClosetImg()
        CodyImg()
        FeedImg()
        feed_ranking()
        getColor()
    }

    // 바텀 네비게이션 아이템 클릭 리스너 설정
    private val onBottomNavItemSeletedListener =
        BottomNavigationView.OnNavigationItemSelectedListener {
            // when은 코틀린에서 switch문
            when (it.itemId) {
                R.id.menu_home -> {
                    Log.d(TAG, "MainActivity - 홈버튼 클릭!")
                    homeFragment = HomeFragment.newInstance()
                    replaceFragment(homeFragment)
                }
                R.id.menu_fashionista -> {
                    Log.d(TAG, "MainActivity - 패셔니스타 버튼 클릭!")

                    //여기에다 실행하면 처음에 안뜸
                    FashionistaUser()
                    favorite_check()
                    fashionistaFragment = FashionistaFragment.newInstance()
                    replaceFragment(fashionistaFragment)
                }
                R.id.menu_closet -> {
                    Log.d(TAG, "MainActivity - 옷장 버튼 클릭!")

                    ClosetImg()
                    CodyImg()
                    closetFragment = ClosetFragment.newInstance()
                    replaceFragment(closetFragment)
                    closetFragment.arguments = bundle

                }
                R.id.menu_feed -> {
                    Log.d(TAG, "MainActivity - 피드 버튼 클릭!")
                    FeedImg()
                    feed_like_check()
                    feedFragment = FeedFragment.newInstance()
                    replaceFragment(feedFragment)
                }
                R.id.menu_mypage -> {
                    Log.d(TAG, "MainActivity - 마이페이지 버튼 클릭!")

                    getColor()
                    mypageFragment = MypageFragment.newInstance()
                    replaceFragment(mypageFragment)

                }
            } // when문 끝
            true
        }


    fun replaceFragment(fragment: Fragment?) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragments_frame, fragment!!)
        fragmentTransaction.commit()
    }

    /**
     * 테드 퍼미션 설정
     */
    private fun setPermission() {
        val permission = object : PermissionListener {

            override fun onPermissionGranted() { // 설정해놓은 위험 권한들이 허용되었을 경우 이곳을 수행함.
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) { // 설정해놓은 위험 권한들 중 거부를 한 경우 이곳을 수행함.
                Toast.makeText(this@MainActivity, "권한이 거부 되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permission)
//            .setRationaleMessage("카메라 앱을 사용하시려면 권한을 허용해주세요.")
            .setDeniedMessage("권한을 거부하셨습니다. [앱 설정] -> [권한] 항목에서 허용해주세요.")
            .setPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            ).check()
    }


    private fun setLocation_Permission() {
        // OS가 Marshmallow 이상일 경우 권한체크를 해야 합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionCheck =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {

                // 권한 없음
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION
                )
            } else {
                getLocation()
                // ACCESS_FINE_LOCATION 에 대한 권한이 이미 있음.
            }
        } else {

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // grantResults[0] 거부 -> -1
        // grantResults[0] 허용 -> 0 (PackageManager.PERMISSION_GRANTED)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // ACCESS_FINE_LOCATION 에 대한 권한 획득.
            Log.d("권한", "4")
            getLocation()
        } else {
            // ACCESS_FINE_LOCATION 에 대한 권한 거부.
            Log.d("권한", "5")
        }
    }


    fun getLocation() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var isGPSEnabled: Boolean = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        var isNetworkEnabled: Boolean = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )

        } else {

            when { //프로바이더 제공자 활성화 여부 체크
                isNetworkEnabled -> {
                    val location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) //인터넷기반으로 위치를 찾음
                    getLongitude = location?.longitude.toString()
                    getLatitude = location?.latitude.toString()

                    AutoHome.setLongitude(this, getLongitude)
                    AutoHome.setLatitude(this, getLatitude)


                    Log.d(
                        "호롤",
                        "죽여라" + "위도" + getLatitude + "경도" + getLongitude + "zz" + gpsLocationListener
                    )

                    val mGeoCoder = Geocoder(applicationContext, Locale.KOREAN)
                    var mResultList: List<Address>? = null
                    try {
                        mResultList = mGeoCoder.getFromLocation(location?.latitude!!,
                            location?.longitude!!,
                            1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    if (mResultList != null) {
                        // 내 주소 가져오기
                        city = mResultList[0].getAddressLine(0)
                        Log.d("MainActivity 내 주소 ", mResultList[0].getAddressLine(0))
                        var cutting = city?.split(' ') // 공백을 기준으로 리스트 생성해서 필요한 주소값만 출력하기
                        city = cutting[1] + " " + cutting[2] + " " + cutting[3]
                        AutoHome.setLocation(this@MainActivity, city)

                    }
                }
                isGPSEnabled -> {
                    val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) //GPS 기반으로 위치를 찾음
                    getLongitude = location?.longitude.toString()
                    getLatitude = location?.latitude.toString()

                    Toast.makeText(this, "현재위치를 불러옵니다.", Toast.LENGTH_SHORT).show()
                    Log.d("호롤", "죽여라" + "위도" + getLatitude + "경도" + getLongitude)

                    val mGeoCoder = Geocoder(applicationContext, Locale.KOREAN)
                    var mResultList: List<Address>? = null
                    try {
                        mResultList = mGeoCoder.getFromLocation(
                            location?.latitude!!,
                            location?.longitude!!,
                            1
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    if (mResultList != null) {
                        // 내 주소 가져오기
                        city = mResultList[0].getAddressLine(0)
                        Log.d("내 주소 ", mResultList[0].getAddressLine(0))
                        var cutting = city?.split(' ') // 공백을 기준으로 리스트 생성해서 필요한 주소값만 출력하기
                        city = cutting[1] + " " + cutting[2] + " " + cutting[3]
                        AutoHome.setLocation(this@MainActivity, city)

                    }
                }

            }

        }
    }

    val gpsLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val provider: String = location.provider
            val longitude: Double = location.longitude
            val latitude: Double = location.latitude
            val altitude: Double = location.altitude
        }

        //아래 3개함수는 형식상 필수부분
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }


    /**
     * 전문가 리스트 출력
     */
    fun FashionistaUser() {

        var fuserId: String
        var fuserLevel: String
        var fuserProfileImg : String

        var fuserIdArr = ArrayList<String>()
        var fuserLevelArr = ArrayList<String>()
        var fuserProImgArr = ArrayList<String>()

        val responseListener: Response.Listener<String?> = object : Response.Listener<String?> {
            override fun onResponse(response: String?) {
                try {

                    var jsonObject = JSONObject(response)

                    val arr: JSONArray = jsonObject.getJSONArray("response")

                    for (i in 0 until arr.length()) {
                        val fuserObject = arr.getJSONObject(i)

                        fuserId = fuserObject.getString("userId")
                        fuserLevel = fuserObject.getString("userLevel")
                        fuserProfileImg = fuserObject.getString("userProfileImg")
//                        fuserProfile = AutoLogin.StringToBitmap(fuserProfileImg)

                        fuserIdArr.add(fuserId)
                        fuserLevelArr.add(fuserLevel)
                        fuserProImgArr.add(fuserProfileImg)
                    }
                    AutoPro.setProuserId(this@MainActivity, fuserIdArr)
                    AutoPro.setProuserLevel(this@MainActivity, fuserLevelArr)
                    AutoPro.setProuserProImg(this@MainActivity, fuserProImgArr)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
        val fashionistaUserRequest = FashionistaUser_Request(responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(fashionistaUserRequest)
    }


    fun ClosetImg() {

        var cuserId: String
        var cclothesName: String
        var clothesArr = mutableListOf<String>()

        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {

                    var jsonObject = JSONObject(response)
                    var response = jsonObject.toString()

                    val arr: JSONArray = jsonObject.getJSONArray("response")


                    for (i in 0 until arr.length()) {
                        val clothesObject = arr.getJSONObject(i)
                        cuserId = clothesObject.getString("userId")
                        cclothesName = clothesObject.getString("clothesName")


                        clothesArr.add(cclothesName)

                        AutoCloset.setClothesName(this, clothesArr as ArrayList<String>)
                        Log.d("ㅁㅁㅁㅁㅁ메인함수", clothesArr.toString())

                    }
                    if(ClothesSaveActivity.clothesSaveContext!=null) {
                        (ClothesSaveActivity.clothesSaveContext as ClothesSaveActivity).finish()
                    }


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val closetServer_Request = ClosetServer_Request(userId!!, responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(closetServer_Request)
    }

    fun CodyImg() {

        var cuserId: String
        var codyImgName: String
        var codyArr = mutableListOf<String>()

        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {

                    var jsonObject = JSONObject(response)
                    var response = jsonObject.toString()

                    val arr: JSONArray = jsonObject.getJSONArray("response")

                    for (i in 0 until arr.length()) {
                        val codyObject = arr.getJSONObject(i)

                        cuserId = codyObject.getString("userId")
                        codyImgName = codyObject.getString("codyImgName")

                        codyArr.add(codyImgName)
                    }
                    if(CodySaveActivity.codySaveContext!=null) {
                        (CodySaveActivity.codySaveContext as CodySaveActivity).finish()
                    }
                    AutoCody.setCodyName(this, codyArr as ArrayList<String>)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val codyServer_Request = CodyServer_Request(userId!!, responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(codyServer_Request)
    }


    fun FeedImg() {

        var feed_userId: String
        var feedNum: String
        var feed_ImgName: String
        var feed_style: String
        var feed_likecnt : String
        var feed_nolikecnt : String
        var feed_userprofileImg : String

        var feedNumArr = ArrayList<String>()
        var feedIdArr = ArrayList<String>()
        var feedStyleArr = ArrayList<String>()
        var feedImgArr = ArrayList<String>()
        var feedlikecntArr = ArrayList<String>()
        var feednolikecntArr = ArrayList<String>()
        var feed_userprofileImgArr = ArrayList<String>()

        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {

                    var jsonObject = JSONObject(response)
                    var response = jsonObject.toString()

                    val arr: JSONArray = jsonObject.getJSONArray("response")

                    for (i in 0 until arr.length()) {
                        val feedObject = arr.getJSONObject(i)


                        feedNum = feedObject.getString("feedNum")
                        feed_userId = feedObject.getString("feed_userId")
                        feed_ImgName = feedObject.getString("feed_ImgName")
                        feed_style = feedObject.getString("feed_style")
                        feed_likecnt = feedObject.getString("feed_likecnt")
                        feed_nolikecnt = feedObject.getString("feed_nolikecnt")
                        feed_userprofileImg = feedObject.getString("feed_userprofileImg")

                        feedNumArr.add(feedNum)
                        feedIdArr.add(feed_userId)
                        feedImgArr.add(feed_ImgName)
                        feedStyleArr.add(feed_style)
                        feedlikecntArr.add(feed_likecnt)
                        feednolikecntArr.add(feed_nolikecnt)
                        feed_userprofileImgArr.add(feed_userprofileImg)

                    }
                    AutoFeed.setFeedNum(this, feedNumArr)
                    AutoFeed.setFeedId(this, feedIdArr)
                    AutoFeed.setFeedName(this, feedImgArr)
                    AutoFeed.setFeedStyle(this, feedStyleArr)
                    AutoFeed.setFeedLikeCnt(this, feedlikecntArr)
                    AutoFeed.setFeednoLikeCnt(this, feednolikecntArr)
                    AutoFeed.setFeeduserprofileImg(this, feed_userprofileImgArr)
                    Log.d("1112222", feedlikecntArr.toString())


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val feedServer_Request = FeedServer_Request(userId!!, responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(feedServer_Request)
    }

    fun favorite_check() {

        var cuserId: String
        var favoriteuserId: String
        var favorite_true: String
        var favoriteuserIdArr = mutableListOf<String>()

        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {

                    var jsonObject = JSONObject(response)
                    var response = jsonObject.toString()

                    val arr: JSONArray = jsonObject.getJSONArray("response")

                    Log.d("기분크기", arr.length().toString())

                    for (i in 0 until arr.length()) {
                        val Object = arr.getJSONObject(i)

                        cuserId = Object.getString("userId")
                        favoriteuserId = Object.getString("prouserId")
                        favorite_true = Object.getString("favorite_true")

                        favoriteuserIdArr.add(favoriteuserId)
                        Log.d("기분?", favoriteuserId)

                        AutoPro.setFavoriteuserId(this, favoriteuserIdArr as java.util.ArrayList<String>)

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val favoritecheck_Request = FavoriteCheck_Request(userId!!, responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(favoritecheck_Request)
    }

    fun feed_like_check() {

        var feedNum: String
        var feed_like_true: String
        var feed_like_false: String

        var feedNumArr = mutableListOf<String>()
        var feedliketrueArr = mutableListOf<String>()
        var feedlikefalseArr = mutableListOf<String>()

        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {

                    var jsonObject = JSONObject(response)
                    var response = jsonObject.toString()

                    val arr: JSONArray = jsonObject.getJSONArray("response")
                    if(arr.length() == 0 ) {
                        feedNumArr.clear()
                        feedliketrueArr.clear()
                        feedlikefalseArr.clear()
                    }
                    for (i in 0 until arr.length()) {
                        val Object = arr.getJSONObject(i)

                        feedNum = Object.getString("feedNum")
                        feed_like_true = Object.getString("feed_like_true")
                        feed_like_false = Object.getString("feed_like_false")

                        feedNumArr.add(feedNum)
                        feedliketrueArr.add(feed_like_true)
                        feedlikefalseArr.add(feed_like_false)

                    }
                    AutoFeed.setFeedNumlike(this, feedNumArr as java.util.ArrayList<String>)
                    AutoFeed.setFeedliketrue(this, feedliketrueArr as java.util.ArrayList<String>)
                    AutoFeed.setFeedlikefalse(this, feedlikefalseArr as java.util.ArrayList<String>)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        Log.d("zzzz호호호", userId!!)
        val feedLikeCheck_Request = FeedLikeCheck_Request(userId!!, responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(feedLikeCheck_Request)
    }

    fun feed_ranking() {

        var feedNum: String
        var feed_userId: String
        var like_cnt: String
        var feed_ImgName: String

        var feedNumArr = ArrayList<String>()
        var feed_userIdArr = ArrayList<String>()
        var like_cntArr = ArrayList<String>()
        var feed_ImgNameArr = ArrayList<String>()

        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {

                    var jsonObject = JSONObject(response)

                    val arr: JSONArray = jsonObject.getJSONArray("response")

                    if(arr.length() == 0 ) {
                        feedNumArr.clear()
                        feed_userIdArr.clear()
                        like_cntArr.clear()
                        feed_ImgNameArr.clear()
                    }
                    for (i in 0 until arr.length()) {
                        val Object = arr.getJSONObject(i)

                        feedNum = Object.getString("feedNum")
                        feed_userId = Object.getString("feed_userId")
                        like_cnt = Object.getString("like_cnt")
                        feed_ImgName = Object.getString("feed_ImgName")

                        feedNumArr.add(feedNum)
                        feed_userIdArr.add(feed_userId)
                        like_cntArr.add(like_cnt)
                        feed_ImgNameArr.add(feed_ImgName)

                    }
                    AutoFeed.setFeedRank_feedNum(this, feedNumArr)
                    AutoFeed.setFeedRank_feed_userId(this, feed_userIdArr)
                    AutoFeed.setFeedRank_like_cnt(this, like_cntArr)
                    AutoFeed.setFeedRank_feed_ImgName(this, feed_ImgNameArr)

                    Log.d("크크크", feedNumArr.toString())
                    Log.d("크크크", feed_userIdArr.toString())
                    Log.d("크크크", like_cntArr.toString())
                    Log.d("크크크", feed_ImgNameArr.toString())

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val feedRanking_Request = FeedRanking_Request(responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(feedRanking_Request)
    }




    fun getColor() {
        var cColor : String
        var cCnt : String
        var cColorArr = mutableListOf<String>()
        var cCntArr = mutableListOf<String>()

        val responseListener: Response.Listener<String?> = object : Response.Listener<String?> {
            override fun onResponse(response: String?) {
                try {

                    var jsonObject = JSONObject(response)

                    val arr: JSONArray = jsonObject.getJSONArray("response")

                    for (i in 0 until arr.length()) {
                        val proObject = arr.getJSONObject(i)

                        cColor = proObject.getString("clothesColor")
                        cCnt = proObject.getString("cnt")

                        cColorArr.add(cColor)
                        cCntArr.add(cCnt)

                        Log.d("유저색", cColor)
                        Log.d("유저색갯수", cCnt)
                        AutoCloset.setClothesColor(this@MainActivity, cColorArr as ArrayList<String>)
                        AutoCloset.setColorCnt(this@MainActivity, cCntArr as ArrayList<String>)
                    }


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
        val clothescolorResponse = ClothesColor_Response(userId!!, responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(clothescolorResponse)

    }



}