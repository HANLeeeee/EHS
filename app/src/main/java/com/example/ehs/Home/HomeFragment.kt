package com.example.ehs.Home

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.toolbox.Volley
import com.example.ehs.AI.Main_AIActivity
import com.example.ehs.BottomSheet.BottomSheet_tpo
import com.example.ehs.Calendar.AutoCalendar
import com.example.ehs.Calendar.CalendarActivity
import com.example.ehs.Calendar.CalendarChoiceActivity
import com.example.ehs.Closet.CodySaveActivity
import com.example.ehs.Fashionista.AutoPro
import com.example.ehs.Fashionista.ProRecommendActivity
import com.example.ehs.Fashionista.ProRecommend_Request
import com.example.ehs.Loading
import com.example.ehs.Login.AutoLogin
import com.example.ehs.MainActivity
import com.example.ehs.R
import com.example.ehs.Weather.WeatherActivity
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_cody_save.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.loading.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class HomeFragment : Fragment(){

    private var a: Activity? = null
    val now: LocalDateTime = LocalDateTime.now()
    var Strnow = now?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    var tpochoice : String? = null

    // ?????? ????????????
    var sun: String? = null
    var mon: String? = null
    var tue: String? = null
    var wed: String? = null
    var thu: String? = null
    var fri: String? = null
    var sat: String? = null

    // ??? ????????????
    var sun2: String? = null
    var mon2: String? = null
    var tue2: String? = null
    var wed2: String? = null
    var thu2: String? = null
    var fri2: String? = null
    var sat2: String? = null

    var getLatitude : String = ""
    var getLongitude : String = ""

    var cAdapter : CalendarlistAdapter? = null
    lateinit var userId: String

    companion object {
        const val TAG : String = "??? ???????????????"
        fun newInstance() : HomeFragment { // newInstance()?????? ????????? ???????????? HomeFragment??? ?????????
            return HomeFragment()
        }
        var calendarNameArr = ArrayList<String>()
        var calendarYearArr = ArrayList<String>()
        var calendarMonthArr = ArrayList<String>()
        var calendarDayArr = ArrayList<String>()
        var homeloading : Loading? = null

        var customProgressDialog: ProgressDialog? = null
    }

    var random_clothesCategoryArr = ArrayList<String>()
    var random_clothesNameArr = ArrayList<String>()
    var random_clothesCategory_DetailArr = ArrayList<String>()

    var saveBitmap : Bitmap? = null

    // ?????????????????? ???????????? ???????????????
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "HomeFragment - onCreate() called")
        AndroidThreeTen.init(a)
        userId = AutoLogin.getUserId(a!!)

        getLongitude = AutoHome.getLongitude(a!!)
        getLatitude = AutoHome.getLatitude(a!!)

        homeloading = Loading(a!!)
        getweather()

    }
    // ?????????????????? ?????? ?????? ??????????????? ????????? ???
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            a = context
        }
        Log.d(TAG, "HomeFragment - onAttach() called")
    }

    // ?????? ??????????????? ??? ????????? ??????
    // ?????????????????? ??????????????? ?????????????????? ????????????.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        week(Strnow!!)
        Log.d(TAG, "HomeFragment - onCreateView() called")
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        customProgressDialog = ProgressDialog(a)
        customProgressDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val formatter2 = DateTimeFormatter.ofPattern("MM")
        var nowmonth = now.format(formatter2).toString() // ????????????????????? ?????? ??????
        Log.d("nowmoth", nowmonth)

        view.btn_calendar.setOnClickListener{
            GlobalScope.launch(Dispatchers.Main) {
                launch(Dispatchers.Main) {
                    customProgressDialog!!.show()
                    (MainActivity.mContext as MainActivity).CalendarImg(nowmonth)
                }
                delay(1000L)

                val intent = Intent(a, CalendarActivity::class.java)
                startActivity(intent)
            }

        }
        view.btn_weathergo.setOnClickListener{
            val intent = Intent(a, WeatherActivity::class.java)
            startActivity(intent)
        }
        view.tv_weathergo.setOnClickListener{
            val intent = Intent(a, WeatherActivity::class.java)
            startActivity(intent)
        }
        view.iv_recoai.setOnClickListener {
            val intent = Intent(a, Main_AIActivity::class.java)
            startActivity(intent)
        }
        view.iv_recotag.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                launch(Dispatchers.Main) {
                    homeloading!!.init("????????? ??????")
                }
                delay(2500L)

                val intent = Intent(a, StyleRecommendActivity::class.java)
                startActivity(intent)
            }

        }
        view.iv_recocolor.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                launch(Dispatchers.Main) {
                    homeloading!!.init("?????? ??????")
                }
                delay(2500L)

                colorRecommend()
            }

        }
        view.iv_recopro.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                launch(Dispatchers.Main) {
                    homeloading!!.init("????????? ??????")
                }
                delay(2500L)

                recommend()
            }
        }

        view.btn_updateH.setOnClickListener {
            Toast.makeText(a, "?????? ????????????", Toast.LENGTH_SHORT).show()
            (activity as MainActivity).getLocation()

            getLongitude = AutoHome.getLongitude(a!!)
            getLatitude = AutoHome.getLatitude(a!!)

            // MainActivity????????? ??????, ?????? ????????????
            Log.d("HomeFragment", "?????? : ${getLatitude}")
            Log.d("HomeFragment", "?????? : ${getLongitude}")
            getweather()
        }

        view.btn_retry.setOnClickListener {
            Log.d("??????????????????", tv_tpo.text.toString())
            if(tv_tpo.text == "TPO ??????") {
                Log.d("??????????????????1", tv_tpo.text.toString())
                (MainActivity.mContext as MainActivity).CodyRandom(MainActivity.basic_detail_top,
                    MainActivity.basic_detail_bottom, MainActivity.basic_detail_shoes,
                    MainActivity.basic_detail_outer, MainActivity.basic_detail_bag)
            } else {
                Log.d("??????????????????2", tv_tpo.text.toString())
                (MainActivity.mContext as MainActivity).CodyRandom(BottomSheet_tpo.detail_top,
                    BottomSheet_tpo.detail_bottom,
                    BottomSheet_tpo.detail_shoes,
                    BottomSheet_tpo.detail_outer,
                    BottomSheet_tpo.detail_bag)
            }
            Log.d("????????????", "????????????")
            randomCody_Img()
        }

        view.btn_saveRandomCody.setOnClickListener {
            saveBitmap = null

            if(CodySaveActivity.codySaveContext == null) {
                Toast.makeText(a!!, "????????? ?????? ?????? ??? ??????????????????", Toast.LENGTH_SHORT).show()
            } else {
                var savealert = AlertDialog.Builder(a!!)
                savealert.setTitle("????????????")
                savealert.setMessage("??? ????????? ?????????????????????????")

                // ?????? ???????????? ?????? ????????? ??? ?????????!
                var listener = object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        when (p1) {
                            // ?????? ?????? ?????? ???
                            DialogInterface.BUTTON_POSITIVE -> {
                                // ???????????? ??????
                                view.ll_randomCody.setDrawingCacheEnabled(true)
                                view.ll_randomCody.buildDrawingCache()

                                //????????? ????????? ???????????? ??????????????? ??????
                                saveBitmap = view.ll_randomCody.getDrawingCache()
                                (MainActivity.mContext as MainActivity).Codycolor(saveBitmap!!)
                            }
                        }
                    }
                }
                savealert.setPositiveButton("??????", listener)
                savealert.setNegativeButton("??????", null)
                savealert.show()
            }
        }
        view.tv_tpo.setOnClickListener {
            val BottomSheet_tpo = BottomSheet_tpo {
                tpochoice = it
                when(it) {
                    tpochoice -> Log.d("0???", tpochoice!!)
                }
                tv_tpo.text = tpochoice
                Log.d("?????????", ",,")
                randomCody_Img()
            }
            BottomSheet_tpo.show((activity as AppCompatActivity).supportFragmentManager,
                BottomSheet_tpo.tag)

        }


        /**
         * ????????? ?????????????????? ??????
         */
        var calendarList = arrayListOf(
            Calendarlist("", sun!!, "???"),
            Calendarlist("", mon!!, "???"),
            Calendarlist("", tue!!, "???"),
            Calendarlist("", wed!!, "???"),
            Calendarlist("", thu!!, "???"),
            Calendarlist("", fri!!, "???"),
            Calendarlist("", sat!!, "???"),
            Calendarlist("", "????????????", "??????")
        )
        /**
         * ????????? ??????????????? ???
         */
        cAdapter = CalendarlistAdapter(a!!, calendarList)
        val gridLayoutManager = GridLayoutManager(a, 4)
        view.rv_homecalendar.layoutManager = gridLayoutManager

        view.rv_homecalendar.adapter = cAdapter
        view.rv_homecalendar.setHasFixedSize(true)

        cAdapter!!.notifyDataSetChanged()
        cAdapter!!.setItemClickListener(object :
            CalendarlistAdapter.OnItemClickListener { // ?????????????????? ????????? ?????? ???
            override fun onClick(v: View, position: Int) {
                if (calendarList[position].day == "????????????") {
                    val intent = Intent(a, CalendarActivity::class.java)
                    startActivity(intent)
                } else {
                    var now = now?.format(DateTimeFormatter.ofPattern("yyyy??? M???"))
                    var selectday =
                        now + " " + calendarList[position].day + "???"
                    Toast.makeText(activity!!, selectday, Toast.LENGTH_SHORT).show()

                    AutoCalendar.setSelectday(a!!, selectday)

                    val intent = Intent(a!!, CalendarChoiceActivity::class.java)
                    startActivity(intent)

                }
            }
        })

        // RecyclerView Adapter????????? ???????????? ????????? (LayoutManager) ??? ??????
        // recyclerView??? setHasFixedSize ????????? true ?????? ??????.

        return view
    } // oncreateview ???

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        randomCody_Img()
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "HomeFragment - onResume() called")
        calendarNameArr = AutoCalendar.getCalendarName(a!!)
        calendarYearArr = AutoCalendar.getCalendarYear(a!!)
        calendarMonthArr = AutoCalendar.getCalendarMonth(a!!)
        calendarDayArr = AutoCalendar.getCalendarDay(a!!)
        Log.d("zzzggzzdf", calendarMonthArr.toString())
        Log.d("zzzggzzdf", calendarDayArr.toString())

        cAdapter!!.notifyDataSetChanged()
        MainActivity.homeProgressDialog?.dismiss()

    }

    fun randomCody_Img() {
        iv_top.setImageResource(0)
        iv_bottom.setImageResource(0)
        iv_bottom2.setImageResource(0)
        iv_shoes.setImageResource(0)
        iv_shoes2.setImageResource(0)
        iv_outer.setImageResource(0)
        iv_bag.setImageResource(0)
        iv_onepiece.setImageResource(0)

        random_clothesCategoryArr = AutoHome.getRandom_clothesCategory(a!!)
        random_clothesNameArr = AutoHome.getRandom_clothesName(a!!)
        random_clothesCategory_DetailArr = AutoHome.getRandom_clothesCategory_Detail(a!!)

        Log.d("????????????", random_clothesCategoryArr.toString())
        Log.d("????????????", random_clothesNameArr.toString())
        Log.d("????????????", random_clothesCategory_DetailArr.toString())

        var a_bitmap : Bitmap? = null
        for (i in 0 until random_clothesNameArr.size) {
            val uThread: Thread = object : Thread() {
                override fun run() {
                    try {
                        Log.d("??????????????????", random_clothesNameArr[i])

                        val url = URL("http://13.125.7.2/img/clothes/" + random_clothesNameArr[i])

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
            uThread.start() // ?????? Thread ??????

            try {

                uThread.join()

                when(random_clothesCategoryArr[i]) {
                    "??????" -> {
                        iv_top.setImageBitmap(a_bitmap)
                    }
                    "??????" -> {
                        if (random_clothesCategoryArr[0] == "?????????") {
                            iv_bottom.setImageResource(0)
                            iv_bottom2.setImageResource(0)
                        } else if (random_clothesCategory_DetailArr[i] == "???????????????" || random_clothesCategory_DetailArr[i] == "?????????") {
                            iv_bottom2.setImageBitmap(a_bitmap)
                        } else {
                            iv_bottom.setImageBitmap(a_bitmap)
                        }
                    }
                    "?????????" -> {
                        iv_onepiece.setImageBitmap(a_bitmap)
                    }
                    "??????" -> {
                        if (random_clothesCategoryArr[0] == "?????????" || random_clothesCategory_DetailArr[i] == "???????????????" || random_clothesCategory_DetailArr[i] == "?????????") {
                            iv_shoes2.setImageBitmap(a_bitmap)
                        } else {
                            iv_shoes.setImageBitmap(a_bitmap)
                        }
                    }
                    "?????????" -> {
                        iv_outer.setImageBitmap(a_bitmap)
                    }
                    "??????" -> {
                        iv_bag.setImageBitmap(a_bitmap)
                    }

                }



            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

    }

    fun getweather() {
        //Create Retrofit Builder
        val retrofit = Retrofit.Builder()
            .baseUrl(WeatherActivity.BaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherActivity.WeatherService::class.java)
        val call = service.getCurrentWeatherData(getLatitude, getLongitude,
            WeatherActivity.AppId
        )
        call.enqueue(object : Callback<WeatherActivity.WeatherResponse> {
            override fun onFailure(call: Call<WeatherActivity.WeatherResponse>, t: Throwable) {
                Log.d("HomeFragment", "result :" + t.message)
            }

            override fun onResponse(
                call: Call<WeatherActivity.WeatherResponse>,
                response: Response<WeatherActivity.WeatherResponse>,
            ) {
                if (response.code() == 200) {
                    val weatherResponse = response.body()
                    Log.d("HomeFragment", "result: " + weatherResponse.toString())
                    val cTemp = weatherResponse!!.current!!.Ctemp - 273.15  //????????? ????????? ??????
                    val minTemp = weatherResponse!!.daily[0].Dtemp!!.Dmin - 273.15
                    val maxTemp = weatherResponse!!.daily[0].Dtemp!!.Dmax - 273.15

                    val intcTemp = cTemp.roundToInt()
                    val intMinTemp = minTemp.roundToInt()
                    val intMaxTemp = maxTemp.roundToInt()
                    val weatherIMG = weatherResponse.hourly[0].hweather[0].icon.toString()

                    when (weatherIMG) { // ????????? ?????? ????????? ??????
                        "01d" -> view!!.img_weatherH.setImageResource(R.drawable.ic_sun)
                        "01n" -> view!!.img_weatherH.setImageResource(R.drawable.ic_sun_night)
                        "02d" -> view!!.img_weatherH.setImageResource(R.drawable.ic_sun_c)
                        "02n" -> view!!.img_weatherH.setImageResource(R.drawable.ic_suncloud_night)
                        "03n", "03d", "04d", "04n" -> view!!.img_weatherH.setImageResource(R.drawable.ic_cloud_many)
                        "09d", "09n", "10d", "10n" -> {
                            view!!.img_weatherH.setImageResource(R.drawable.ic_rain)
                            view!!.iv_umbrella.isVisible = true
                        }
                        "11d", "11n" -> view!!.img_weatherH.setImageResource(R.drawable.ic_thunder)
                        "13d", "13n" -> view!!.img_weatherH.setImageResource(R.drawable.ic_snow)
                        "50n", "50d" -> view!!.img_weatherH.setImageResource(R.drawable.ic_mist)
                    }
                    tv_cityH.text = AutoHome.getLocation(a!!)
                    tv_MinMaxH.text =
                        intMinTemp.toString() + "\u00B0" + "/ " + intMaxTemp.toString() + "\u00B0"
                    tv_cTempH.text = intcTemp.toString() + "\u00B0"
                } else {
                    Log.d("???????????????", "????????????")
                }
            }

        })
    }


    /**
     * ?????? ????????? ?????? ??? ????????? ?????? ??????
     */

    fun week(eventDate: String) {
        val dateArray = eventDate.split("-").toTypedArray()
        val cal = Calendar.getInstance()
        cal[dateArray[0].toInt(), dateArray[1].toInt() - 1] = dateArray[2].toInt()

        // ???????????? ????????? ???????????? ??????
        cal.firstDayOfWeek = Calendar.SUNDAY

        // ???????????? ??????????????? ????????? ?????????
        val dayOfWeek = cal[Calendar.DAY_OF_WEEK] - cal.firstDayOfWeek

        // ?????? ????????? ???????????? ????????????
        cal.add(Calendar.DAY_OF_MONTH, -dayOfWeek)
        val sf = SimpleDateFormat("yyyy-MM-dd")

        // ?????? ????????? ?????? ??????
        val startDt = sf.format(cal.time)
        Log.d("zzz?????????", startDt)

        // ?????? ??????
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val twoDt = sf.format(cal.time)
        // ?????? ??????
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val threeDt = sf.format(cal.time)
        // ?????? ??????
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val fourDt = sf.format(cal.time)
        // ????????? ??????
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val fiveDt = sf.format(cal.time)
        // ????????? ??????
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val sixDt = sf.format(cal.time)
        // ?????? ????????? ????????? ?????? ??????
        cal.add(Calendar.DAY_OF_MONTH, 1)
        // ?????? ????????? ????????? ??????
        val endDt = sf.format(cal.time)

        var token = startDt.split('-')
        sun = token[2] //???
        sun2 = token[1] //2????????? ???
        Log.d("zzz?????????333", sun2!!)

        token = twoDt.split('-')
        mon = token[2]
        mon2 = token[1]

        token = threeDt.split('-')
        tue = token[2]
        tue2 = token[1]

        token = fourDt.split('-')
        wed = token[2]
        wed2 = token[1]

        token = fiveDt.split('-')
        thu = token[2]
        thu2 = token[1]

        token = sixDt.split('-')
        fri = token[2]
        fri2 = token[1]

        token = endDt.split('-')
        sat = token[2]
        sat2 = token[1]

        Log.d(TAG, "?????? ?????? = [$eventDate] >> ?????? ?????? = [$startDt], ?????? ?????? = [$endDt], ????????? = [$twoDt]")
        Log.d("???", "??????" + sun)
        Log.d("???", "??????" + mon)
    }


    fun recommend() {
        var proStyle : String
        var prouserId : String
        var proprofileImg : String
        var proplusImgPath : String
        var proplusImgName : String

        var proIdArr = ArrayList<String>()
        var proImgArr = ArrayList<String>()
        var proplusImgPathArr = ArrayList<String>()
        var proplusImgNameArr = ArrayList<String>()

        val responseListener: com.android.volley.Response.Listener<String?> =
            com.android.volley.Response.Listener<String?> { response ->
                try {

                    var jsonObject = JSONObject(response)
                    var response = jsonObject.toString()

                    val arr: JSONArray = jsonObject.getJSONArray("response")

                    Log.d("~~1", response)
                    Log.d("~~2", arr.toString())
                    Log.d("~~22", arr.length().toString())


                    for (i in 0 until arr.length()) {
                        val proObject = arr.getJSONObject(i)
                        Log.d("~~3", arr[i].toString())

                        prouserId = proObject.getString("userId")
                        proprofileImg = proObject.getString("userProfileImg")
                        proplusImgPath = proObject.getString("plusImgPath")
                        proplusImgName =  proObject.getString("plusImgName")
                        proStyle = proObject.getString("plusImgStyle")

                        proIdArr.add(prouserId)
                        proImgArr.add(proprofileImg)
                        proplusImgPathArr.add(proplusImgPath)
                        proplusImgNameArr.add(proplusImgName)

                        AutoPro.setStyle(a!!, proStyle)
                    }

                    val proIdArr2: HashSet<String> = HashSet(proIdArr)
                    val proIdArr3: ArrayList<String> = ArrayList(proIdArr2)

                    Log.d("?????? ??????", proIdArr3.size.toString())
                    if(proIdArr3.size <= 1) {
                        Log.d("?????? ??????", proIdArr3.size.toString())
                        Toast.makeText(a!!, "??????????????????????????? ????????? ?????????????????? ????????????.", Toast.LENGTH_SHORT).show()
                        homeloading?.finish()
                        return@Listener
                    } else {
                        AutoPro.setProProfileId(a!!, proIdArr)
                        AutoPro.setProProfileId2(a!!, proIdArr3)
                        AutoPro.setProProfileImg(a!!, proImgArr)
                        AutoPro.setProplusImgPath(a!!, proplusImgPathArr)
                        AutoPro.setProplusImgName(a!!, proplusImgNameArr)

                        // ?????? ??????????????? ??????
                        val intent = Intent(a!!, ProRecommendActivity::class.java)
                        startActivity(intent)
                    }


                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(a!!, "????????? ???????????? ??????????????????", Toast.LENGTH_SHORT).show()
                    homeloading?.finish()

                }
            }
        val proRecommendRequest = ProRecommend_Request(userId!!, responseListener)
        val queue = Volley.newRequestQueue(a)
        queue.add(proRecommendRequest)

    }

    fun colorRecommend() {
        var color: String
        var coloruserId: String
        var colorplusImgPath: String
        var colorplusImgName: String
        var colorplusImgStyle: String

        var coloruserIdArr = ArrayList<String>()
        var colorplusImgPathArr = ArrayList<String>()
        var colorplusImgNameArr = ArrayList<String>()
        var colorplusImgStyleArr = ArrayList<String>()

        val responseListener: com.android.volley.Response.Listener<String?> =
            com.android.volley.Response.Listener<String?> { response ->
                try {

                    var jsonObject = JSONObject(response)
                    var response = jsonObject.toString()

                    val arr: JSONArray = jsonObject.getJSONArray("response")

                    if (arr.length() == 0) {
                        Toast.makeText(a!!, "??????????????????????????? ????????? ?????????????????? ????????????.", Toast.LENGTH_SHORT).show()
                        homeloading?.finish()
                        return@Listener
                    } else {
                        for (i in 0 until arr.length()) {
                            val proObject = arr.getJSONObject(i)

                            color = proObject.getString("plusImgColor")
                            coloruserId = proObject.getString("userId")
                            colorplusImgPath = proObject.getString("plusImgPath")
                            colorplusImgName = proObject.getString("plusImgName")
                            colorplusImgStyle = proObject.getString("plusImgStyle")


                            coloruserIdArr.add(coloruserId)
                            colorplusImgPathArr.add(colorplusImgPath)
                            colorplusImgNameArr.add(colorplusImgName)
                            colorplusImgStyleArr.add(colorplusImgStyle)

                            AutoHome.setColorcody(a!!, color)
                        }

                        AutoHome.setColoruserId(a!!, coloruserIdArr)
                        AutoHome.setColorplusImgPath(a!!, colorplusImgPathArr)
                        AutoHome.setColorplusImgName(a!!, colorplusImgNameArr)
                        AutoHome.setColorplusImgStyle(a!!, colorplusImgStyleArr)

                        val intent = Intent(a!!, ColorRecommendActivity::class.java)
                        startActivity(intent)
                    }


                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(a!!, "????????? ???????????? ??????????????????", Toast.LENGTH_SHORT).show()
                    homeloading?.finish()
                }
            }
        val colorRecommend_Request = ColorRecommend_Request(userId!!, responseListener)
        val queue = Volley.newRequestQueue(a!!)
        queue.add(colorRecommend_Request)
    }
//    // ?????????????????? ????????? ?????? ????????????
//    fun onTpoButtonClicked(text: String) {
//        tv_tpo.text = text
//    }


}