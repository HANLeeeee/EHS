package com.example.ehs.Mypage

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.ehs.Calendar.AutoCalendar
import com.example.ehs.Closet.AutoCloset
import com.example.ehs.Closet.AutoCody
import com.example.ehs.Fashionista.AutoPro
import com.example.ehs.Feed.AutoFeed
import com.example.ehs.Home.AutoHome
import com.example.ehs.Login.AutoLogin
import com.example.ehs.Login.LoginActivity
import com.example.ehs.MainActivity
import com.example.ehs.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.android.synthetic.main.fragment_mypage.view.*


class MypageFragment : Fragment() {
    private var a: Context? = null

    var userColorArr = ArrayList<String>()
    var userColorCntArr = ArrayList<String>()

    lateinit var userId :String
    lateinit var userPw :String
    lateinit var userName :String
    lateinit var userEmail :String
    lateinit var userBirth :String
    lateinit var userGender :String
    lateinit var userLevel2 :String
    lateinit var userLevel :String
    var totallikecnt : Int = 0

    var userProfile : Bitmap? =null


    companion object {
        const val TAG: String = "??????????????? ???????????????"
        fun newInstance(): MypageFragment { // newInstance()?????? ????????? ???????????? HomeFragment??? ?????????
            return MypageFragment()
        }
    }

    // ?????????????????? ???????????? ???????????????
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MypageFragment - onCreate() called")

        userColorArr = AutoCloset.getClothesColor(a!!)
        userColorCntArr = AutoCloset.getColorCnt(a!!)
        Log.d("????????????1", userColorArr.toString())
        Log.d("????????????1", userColorCntArr.toString())

        userId = AutoLogin.getUserId(a!!)
        Log.d("???????????????1111", userId)
        userPw = AutoLogin.getUserPw(a!!)
        userName = AutoLogin.getUserName(a!!)
        userEmail = AutoLogin.getUserEmail(a!!)
        userBirth = AutoLogin.getUserBirth(a!!)
        userGender = AutoLogin.getUserGender(a!!)
        userLevel2 = AutoLogin.getUserLevel2(a!!)
        userLevel = AutoLogin.getUserLevel(a!!)
        totallikecnt = AutoFeed.getFeedLikeTotalcnt(a!!).toInt()

        var userProfileImg = AutoLogin.getUserProfileImg(a!!)
        userProfile = StringToBitmap(userProfileImg)

        (activity as MainActivity).GetColor()
    }

    override fun onResume() {
        super.onResume()

        MainActivity.homeProgressDialog?.dismiss()

        setupPieChart()
        loadPieChartData()
    }


    // ?????????????????? ?????? ?????? ??????????????? ????????? ???
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "MypageFragment - onAttach() called")

        if (context is Activity) {
            a = context
        }
    }


    // ?????? ??????????????? ??? ????????? ??????
    // ?????????????????? ??????????????? ?????????????????? ????????????.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d(TAG, "MypageFragment - onCreateView() called")
        val view: View = inflater!!.inflate(R.layout.fragment_mypage, container, false)

        view.btn_modify.setOnClickListener {
            val intent = Intent(context, UserModifyActivity::class.java)
            startActivity(intent)
        }


        view.btn_logout.setOnClickListener { view ->
            Log.d("??????!!", "???????????? ?????? ??????!!")
            var logoutalert = AlertDialog.Builder(a!!)
            logoutalert.setTitle("???????????? ?????????")
            logoutalert.setMessage("'Onmonemo'?????? ???????????? ???????????????????")

            // ?????? ???????????? ?????? ????????? ??? ?????????!
            var listener = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    when (p1) {
                        // ?????? ?????? ?????? ???
                        DialogInterface.BUTTON_POSITIVE -> {
                            // ???????????? ??????

                            AutoLogin.setUserId(a!!, null)
                            AutoLogin.clearUser(a!!)
                            AutoHome.clearHome(a!!)
                            AutoPro.clearPro(a!!)
                            AutoCody.clearCody(a!!)
                            AutoCloset.clearCloset(a!!)
                            AutoFeed.clearFeed(a!!)
                            AutoCalendar.clearCalendar(a!!)

                            //??????????????? finish() ?????????????????? ????????????
                            activity?.supportFragmentManager?.beginTransaction()?.remove(this@MypageFragment)?.commit()
                            (MainActivity.mContext as MainActivity).finish()
                            val myintent = Intent(a, LoginActivity::class.java)
                            startActivity(myintent)
                        }
                    }
                }
            }
            logoutalert.setPositiveButton("??????", listener)
            logoutalert.setNegativeButton("??????", null)
            logoutalert.show()
        }

        //fragment1??? TextView??? ?????? ?????? text ?????????
        view.tv_id.text = userId
        view.tv_name.text = userName
        view.tv_name2.text = userName
        view.tv_name3.text = userName
        view.tv_email.text = userEmail
        view.tv_level.text = userLevel

//        iv_profileimg.setImageResource(R.drawable.exfirst)
        view.iv_profileimg.setImageBitmap(userProfile)

        Thread {
            for (i in 0..totallikecnt) {
                try {
                    Thread.sleep(20)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                view.tv_name3.post(Runnable {
                    view.progressbar.progress = i
                    view.tv_percent.text = "$i%"
                    if (i == 100) {
                        view.tv_name3_2.isVisible=false
                        view.tv_name3.text = "?????????!! ??????????????????"
                    }
                })
            }
        }.start()

        if(userLevel2 == "LV1") {
            view.iv_beforeLV.setImageResource(R.drawable.lv1)
            view.iv_afterLV.setImageResource(R.drawable.lv2)

        } else if(userLevel2 == "LV2") {
            view.iv_beforeLV.setImageResource(R.drawable.lv2)
            view.tv_beforeLV.text = "LV2"
            view.iv_afterLV.setImageResource(R.drawable.lv3)
            view.tv_afterLV.text = "LV3"

        } else if(userLevel2 == "LV3") {
            view.iv_beforeLV.setImageResource(R.drawable.lv3)
            view.tv_beforeLV.text = "LV3"
            view.iv_afterLV.setImageResource(R.drawable.lv4)
            view.tv_afterLV.text = "LV4"

        } else if(userLevel2 == "LV4") {
            view.iv_beforeLV.setImageResource(R.drawable.lv4)
            view.tv_beforeLV.text = "LV4"
            view.iv_afterLV.setImageResource(R.drawable.lv5)
            view.tv_afterLV.text = "LV5"

        } else if(userLevel2 == "LV5"){

            view.tv_level5.isVisible=true

            view.tv_percent.isVisible=false
            view.progressbar.isVisible=false
            view.iv_beforeLV.isVisible=false
            view.tv_beforeLV.isVisible=false
            view.iv_afterLV.isVisible=false
            view.tv_afterLV.isVisible=false

            if(userLevel == "?????????") {
                view.tv_level5.text = "???????????? ????????? ??????????????????."
            }
        }

        return view
    }


    fun StringToBitmap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte: ByteArray = Base64.decode(encodedString,
                Base64.DEFAULT) // String ??? ??? ????????????  base64???????????? ??????????????? byte????????? ??????
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size) //???????????? bitmap??? return
        } catch (e: Exception) {
            e.message
            null
        }
    }

    private fun setupPieChart() {
        view?.piechart_mypage!!.isDrawHoleEnabled = true
        view?.piechart_mypage!!.setUsePercentValues(true)
        view?.piechart_mypage!!.setEntryLabelTextSize(10f)
        view?.piechart_mypage!!.setEntryLabelColor(Color.BLACK)
        view?.piechart_mypage!!.setCenterTextSize(15f)
        view?.piechart_mypage!!.description.isEnabled = false
        view?.piechart_mypage!!.setExtraOffsets(5f, 10f, 5f, 5f)
        view?.piechart_mypage!!.transparentCircleRadius = 61f

        val legend: Legend = view?.piechart_mypage!!.legend
        legend.isEnabled = false

    }


    fun loadPieChartData() {

        val colorstwo: ArrayList<Int> = ArrayList()

        lateinit var colorname : String

        val entries: ArrayList<PieEntry> = ArrayList()
        with(entries) {

            for (i in 0 until userColorArr.size) {

                val f: Float = userColorCntArr[i].toFloat()
                add(PieEntry(f, userColorArr[i]))

                when(userColorArr[i]) {
                    "??????" -> colorname = "#FFFFFF"
                    "??????" -> colorname = "#fefcec"
                    "?????????" -> colorname = "#e7e7e7"
                    "?????????" -> colorname = "#7a7a7a"
                    "??????" -> colorname = "#000000"

                    "??????" -> colorname = "#fe820d"
                    "?????????" -> colorname = "#e2c79c"
                    "??????" -> colorname = "#ffe600"
                    "??????" -> colorname = "#c4db88"
                    "??????" -> colorname = "#c5e2ff"

                    "??????" -> colorname = "#ff8290"
                    "?????????" -> colorname = "#fee0de"
                    "??????" -> colorname = "#00a03e"
                    "??????" -> colorname = "#666b16"
                    "??????" -> colorname = "#1f4ce2"

                    "??????" -> colorname = "#ed1212"
                    "??????" -> colorname = "#9d2140"
                    "??????" -> colorname = "#844f1e"
                    "??????" -> colorname = "#7119ac"
                    "?????????" -> colorname = "#060350"
                }


                with(colorstwo) {
                    add(Color.parseColor(colorname))
                }


            }

        }

        val dataSet = PieDataSet(entries, "Expense Category")


        dataSet.colors = colorstwo

        val data: PieData = PieData(dataSet)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(view?.piechart_mypage!!))
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.BLACK)

        view?.piechart_mypage!!.setData(data)
        view?.piechart_mypage!!.invalidate()

        // ???????????????
        view?.piechart_mypage!!.animateY(1400, Easing.EaseInOutQuad)


    }


}