package com.example.ehs.Closet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.ehs.Calendar.CalendarClothesFragment
import com.example.ehs.Calendar.CalendarClothesListAdapter
import com.example.ehs.Calendar.CalendarCodyFragment
import com.example.ehs.Closet.CodySaveActivity.Companion.codysaveActivity_Dialog
import com.example.ehs.MainActivity
import com.example.ehs.R
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.fragment_closet.view.*
import kotlinx.android.synthetic.main.fragment_cody.*
import kotlinx.android.synthetic.main.fragment_cody.view.*
import kotlinx.android.synthetic.main.fragment_cody.view.nsprogress
import kotlinx.android.synthetic.main.fragment_cody.view.nsview
import kotlinx.android.synthetic.main.fragment_cody.view.tv_mycody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class CodyFragment : Fragment() {

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(
        a!!,
        R.anim.rotate_open_anim
    )}
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(
        a!!,
        R.anim.rotate_close_anim
    )}
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(
        a!!,
        R.anim.from_bottom_anim
    )}
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(
        a!!,
        R.anim.to_bottom_anim
    )}
    private var clicked = false

    val codyList = mutableListOf<Cody>()
    var codyArr = ArrayList<String>()
    var codyStyleArr = ArrayList<String>()

    val adapter = CodyListAdapter(codyList)

    var before_page : Int = 0
    var after_page : Int = 0

    companion object {
        var a: Activity? = null
        const val TAG : String = "??????"
        fun newInstance() : CodyFragment { // newInstance()?????? ????????? ???????????? CodyFragment??? ?????????
            return CodyFragment()
        }
    }

    // ?????????????????? ???????????? ???????????????
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "CodyFragment - onCreate() called")
        AndroidThreeTen.init(a)

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "???????????? ??????")
        codyList.clear()
        codysaveActivity_Dialog?.dismiss()

        codyArr = AutoCody.getCodyName(a!!)
        codyStyleArr = AutoCody.getCodyStyle(a!!)

        if (codyArr.size <= 8) {
            after_page = codyArr.size
        } else {
            after_page = 8
        }
        var parse1 = parseResult()
        parse1.execute(before_page, after_page)

        Log.d("???????????????????????????222", codyArr.toString())
    }



    // ?????????????????? ?????? ?????? ??????????????? ????????? ???
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            a = context
        }
        Log.d(TAG, "CodyFragment - onAttach() called")
    }

    // ?????? ??????????????? ??? ????????? ??????
    // ?????????????????? ??????????????? ?????????????????? ????????????.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "CodyFragment - onCreateView() called")
        val view: View = inflater!!.inflate(R.layout.fragment_cody, container, false)

        view.tv_mycody.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                launch(Dispatchers.Main) {
                    (MainActivity.mContext as MainActivity).CodyImg()
                    MainActivity.homeProgressDialog!!.show()
                }
                delay(500L)
                (activity as MainActivity?)!!.replaceFragment(newInstance())
            }
            Log.d("FeedFragment", "????????????")
        }

        view.tv_myclothes2.setOnClickListener {
            Log.d("CodyFragment", "??? ????????? ??????")
            if (requireFragmentManager().findFragmentByTag("closet") != null) {
                requireFragmentManager().beginTransaction().show(requireFragmentManager().findFragmentByTag("closet")!!).commit()
            } else {
                GlobalScope.launch(Dispatchers.Main) {
                    launch(Dispatchers.Main) {
                        MainActivity.homeProgressDialog!!.show()
                    }
                    delay(1000L)

                    requireFragmentManager().beginTransaction().add(R.id.fragments_frame, ClosetFragment(), "closet").commit()
                }
            }
            if (requireFragmentManager().findFragmentByTag("cody") != null) {
                requireFragmentManager().beginTransaction().hide(requireFragmentManager().findFragmentByTag("cody")!!).commit()
            }
        }
        view.btn_add2.setOnClickListener { view ->
            Log.d("??????!!", "????????? ?????? ??????!!")
            onAddButtonClicked()
        }
        view.btn_addcody.setOnClickListener { view ->
            Log.d("??????!!", "???????????? ?????? ??????!!")
            onAddButtonClicked()
            val intent = Intent(a, CodyMakeActivity::class.java)
            startActivity(intent)
        }
        view.tv_addcody.setOnClickListener { view ->
            Log.d("??????!!", "???????????? ????????? ??????!!")
            onAddButtonClicked()
            val intent = Intent(context, CodyMakeActivity::class.java)
            startActivity(intent)
        }

        view.nsview.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            Log.d("????????????", "?????????")
            view.nsprogress.isVisible = true
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                Log.d("????????????1", before_page.toString())
                Log.d("????????????2", after_page.toString())
                before_page += 8
                after_page += 8
                Log.d("????????????3", before_page.toString())
                Log.d("????????????4", after_page.toString())
                if(codyArr.size <= after_page) {
                    Log.d("????????????5", codyArr.size.toString())
                    Log.d("????????????6", after_page.toString())
                    after_page = codyArr.size
                }
                if(before_page < after_page) {
                    val parse1 = parseResult()
                    parse1.execute(before_page, after_page)
                }
                if(codyArr.size == codyList.size) {
                    view.nsprogress.isVisible = false
                }

            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridLayoutManager = GridLayoutManager(a, 6)
        gridLayoutManager.setSpanSizeLookup(object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val gridPosition = position % 5
                when (gridPosition) {
                    0, 1, 2, 3, 4 -> return 3
                }
                return 0
            }
        })
        recycler_cody.layoutManager = gridLayoutManager

        recycler_cody.adapter = adapter
        recycler_cody.addItemDecoration(CalendarClothesFragment.ItemDecorator(10))
        adapter.notifyDataSetChanged()
        //recylerview ?????? fashionista.xml??? ?????? ??????
    }

    open inner class parseResult : AsyncTask<Int?, Int?, Bitmap>() {
        var a_bitmap: Bitmap? = null
        override fun onPreExecute() {
            view?.nsprogress!!.isVisible = true
        }

        override fun doInBackground(vararg pages: Int?): Bitmap {
            try {
                for (i in pages[0]!!.toInt() until pages[1]!!.toInt()) {
                    val url = URL("http://13.125.7.2/img/cody/" + codyArr[i])
                    val conn: HttpURLConnection = url.openConnection() as HttpURLConnection

                    conn.setDoInput(true)
                    conn.connect()
                    val iss: InputStream = conn.getInputStream()
                    a_bitmap = BitmapFactory.decodeStream(iss)

                    var cody = Cody(a_bitmap, codyStyleArr[i])
                    codyList.add(cody)
                }

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return a_bitmap!!

        }

        override fun onPostExecute(img: Bitmap) {
            view?.nsprogress!!.isVisible = false
            adapter.notifyDataSetChanged()
        }
    }

    fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    fun setVisibility(clicked: Boolean) {
        if(!clicked) {
            btn_addcody.visibility = View.VISIBLE
            tv_addcody.visibility = View.VISIBLE
        }else {
            btn_addcody.visibility = View.INVISIBLE
            tv_addcody.visibility = View.INVISIBLE
            btn_add2.backgroundTintList = AppCompatResources.getColorStateList(a!!, R.color.white)
        }
    }
    fun setAnimation(clicked: Boolean) {
        if(!clicked) {
            btn_addcody.startAnimation(fromBottom)
            tv_addcody.startAnimation(fromBottom)
            btn_add2.startAnimation(rotateOpen)
        } else {
            btn_addcody.startAnimation(toBottom)
            tv_addcody.startAnimation(toBottom)
            btn_add2.startAnimation(rotateClose)
        }
    }

    fun setClickable(clicked: Boolean) {
        if(!clicked) {
            btn_addcody.isClickable = true
            tv_addcody.isClickable = true
        } else {
            btn_addcody.isClickable = false
            tv_addcody.isClickable = false
        }
    }


}