package com.example.ehs.Calendar

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ehs.Closet.Cody
import com.example.ehs.Home.HomeFragment
import com.example.ehs.R
import kotlinx.android.synthetic.main.calendar_cell.view.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*


class CalendarAdapter(private val calendar: ArrayList<Calendar>) :
    RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return calendar.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.calendar_cell, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.14).toInt()
        return ViewHolder(view)


    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
        var item = calendar[position]
        val listener = View.OnClickListener { it ->
            Toast.makeText(holder.itemView.context,
                "호로로롤 ${calendar[position].day}",
                Toast.LENGTH_SHORT).show()
        }

        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var selectmonth: String? = (CalendarActivity.context_calendar as CalendarActivity).todaymonth // 지금 화면에 보여지는 월, calendar액티비에서 가져온 변수
        private var view: View = v

        fun bind(listener: View.OnClickListener, item: Calendar) {
            view.cellDayText.text = item.day
            // val day = itemView?.findViewById<TextView>(R.id.cellDayText) // 캘린더 날짜
            // val daycody = itemView?.findViewById<ImageView>(R.id.iv_calendarcody) // 캘린더 코디

            /* dogPhoto의 setImageResource에 들어갈 이미지의 id를 파일명(String)으로 찾고,
                이미지가 없는 경우 안드로이드 기본 아이콘을 표시한다.*/

            var today: LocalDate = LocalDate.now() // 현재 날짜 받아오기
            val ddformatter = DateTimeFormatter.ofPattern("dd")

            val nowday = today.format(ddformatter).toString() // 현재날짜에서의 일만 표시
            val nowmonth = today.monthValue.toString()


            if (item.day == nowday && selectmonth.toString() == nowmonth ) { // 현재 일이고 뿌려지는 월과 오늘 월이 같다면
                view.cellDayText.setTextColor(Color.parseColor("#521b93")) // 날짜 보라색으로 표시
            }

            var a_bitmap : Bitmap? = null
            for (i in 0 until CalendarActivity.calendarNameArr.size) {
                val uThread: Thread = object : Thread() {
                    override fun run() {
                        try {
//                            Log.d("달력", CalendarActivity.calendarNameArr[i])
                            val url = URL("http://13.125.7.2/img/calendar/" + CalendarActivity.calendarNameArr[i])

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

                    //지정한 날짜에 이미지 넣기
                    if(selectmonth.toString() == CalendarActivity.calendarMonthArr[i] &&  item.day == CalendarActivity.calendarDayArr[i]) {
                        view.iv_calendarcody.setImageBitmap(a_bitmap)
                    }


                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }

        }

    }

    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener: OnItemClickListener

}