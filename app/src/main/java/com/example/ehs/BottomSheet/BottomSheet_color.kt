package com.example.ehs.BottomSheet
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.example.ehs.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottomsheet_color.*


/**
 * 옷 등록 시 카테고리 지정할 때 하단에 올라올 바텀 시트 호출하는 코틀린 파일
 */
class BottomSheet_color : BottomSheetDialogFragment() {
    var colorchoice : String? = null
    var whiteclicked = false
    var creamclicked = false
    var lightgrayclicked = false
    var darkgrayclicked = false
    var blackclicked = false
    var orangeclicked = false
    var beigeclicked = false
    var yellowclicked = false
    var lightgreenclicked = false
    var skyblueclicked = false
    var pinkclicked = false
    var lightpinkclicked = false
    var greenclicked = false
    var kakiclicked = false
    var blueclicked = false
    var redclicked = false
    var wineclicked = false
    var brownclicked = false
    var purpleclicked = false
    var navyclicked = false
    lateinit var bottomSheetButtonClickListener : BottomSheetButtonClickListener




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.bottomsheet_color, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            bottomSheetButtonClickListener = context as BottomSheetButtonClickListener
        }catch (e: ClassCastException) {
            Log.d("호호홓ㅎ", "onAttach error")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.findViewById<Button>(R.id.btn_choicecom)?.setOnClickListener {
            Log.d("카테고리", "선택사항은" + colorchoice)
            bottomSheetButtonClickListener.onColorButtonClicked(colorchoice!!)
            dismiss()
        }
        view?.findViewById<ImageButton>(R.id.btn_white)?.setOnClickListener { whiteclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_cream)?.setOnClickListener { creamclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_lightgray)?.setOnClickListener { lightgrayclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_darkgray)?.setOnClickListener { darkgrayclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_black)?.setOnClickListener { blackclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_orange)?.setOnClickListener { orangeclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_beige)?.setOnClickListener { beigeclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_yellow)?.setOnClickListener { yellowclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_lightgreen)?.setOnClickListener { lightgreenclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_skyblue)?.setOnClickListener { skyblueclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_pink)?.setOnClickListener { pinkclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_lightpink)?.setOnClickListener { lightpinkclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_green)?.setOnClickListener { greenclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_kaki)?.setOnClickListener { kakiclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_blue)?.setOnClickListener { blueclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_red)?.setOnClickListener { redclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_wine)?.setOnClickListener { wineclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_brown)?.setOnClickListener { brownclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_purple)?.setOnClickListener { purpleclicked()  }
        view?.findViewById<ImageButton>(R.id.btn_navy)?.setOnClickListener { navyclicked()  }
    }

    interface BottomSheetButtonClickListener{
        fun onColorButtonClicked(text: String)
    }


    /**
     * 함수호출 : 각 카테고리 버튼 클릭 시 색깔 바꿈 & 값 받아오기
     */

    fun whiteclicked() {
        when (colorchoice) {
            null -> {
                if (!whiteclicked) {
                    btn_white.setImageResource(R.drawable.ic_check_black)
                    whiteclicked = true
                    colorchoice = "흰색"
                }
            }
            "흰색" -> {
                btn_white.setImageIcon(null)
                whiteclicked = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun creamclicked() {
        when (colorchoice) {
            null -> {
                if (!creamclicked) {
                    btn_cream.setImageResource(R.drawable.ic_check_black)
                    creamclicked = true
                    colorchoice = "크림"
                }
            }
            "크림" -> {
                btn_cream.setImageIcon(null)
                creamclicked = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun lightgrayclicked() {
        when (colorchoice) {
            null -> {
                if (!lightgrayclicked) {
                    btn_lightgray.setImageResource(R.drawable.ic_check_white)
                    lightgrayclicked = true
                    colorchoice = "연회색"
                }
            }
            "연회색" -> {
                btn_lightgray.setImageIcon(null)
                lightgrayclicked = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun darkgrayclicked() {
        when (colorchoice) {
            null -> {
                if (!darkgrayclicked) {
                    btn_darkgray.setImageResource(R.drawable.ic_check_white)
                    darkgrayclicked = true
                    colorchoice = "진회색"
                }
            }
            "진회색" -> {
                btn_darkgray.setImageIcon(null)
                darkgrayclicked = false
                colorchoice = null
            }
            else -> { Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show() }
        }
    }

    fun blackclicked() {
        when (colorchoice) {
            null -> {
                if (!blackclicked) {
                    btn_black.setImageResource(R.drawable.ic_check_white)
                    blackclicked = true
                    colorchoice = "검정"
                }
            }
            "검정" -> {
                btn_black.setImageIcon(null)
                blackclicked = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun orangeclicked() {
        when (colorchoice) {
            null -> {
                if (!orangeclicked) {
                    btn_orange.setImageResource(R.drawable.ic_check_white)
                    orangeclicked = true
                    colorchoice = "주황"
                }
            }
            "주황" -> {
                btn_orange.setImageIcon(null)
                orangeclicked = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun beigeclicked() {
        when (colorchoice) {
            null -> {
                if (!beigeclicked) {
                    btn_beige.setImageResource(R.drawable.ic_check_white)
                    beigeclicked = true
                    colorchoice = "베이지"
                }
            }
            "베이지" -> {
                btn_beige.setImageIcon(null)
                beigeclicked = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun yellowclicked() {
        when (colorchoice) {
            null -> {
                if (!yellowclicked) {
                    btn_yellow.setImageResource(R.drawable.ic_check_white)
                    yellowclicked = true
                    colorchoice = "노랑"
                }
            }
            "노랑" -> {
                btn_yellow.setImageIcon(null)
                yellowclicked = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun lightgreenclicked() {
        when (colorchoice) {
            null -> {
                if (!lightgreenclicked) {
                    btn_lightgreen.setImageResource(R.drawable.ic_check_white)
                    lightgreenclicked = true
                    colorchoice = "연두"
                }
            }
            "연두" -> {
                btn_lightgreen.setImageIcon(null)
                lightgreenclicked = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun skyblueclicked() {
        when (colorchoice) {
            null -> {
                if (!skyblueclicked) {
                    btn_skyblue.setImageResource(R.drawable.ic_check_white)
                    skyblueclicked = true
                    colorchoice = "하늘"
                }
            }
            "하늘" -> {
                btn_skyblue.setImageIcon(null)
                skyblueclicked = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun pinkclicked() {
        when (colorchoice) {
            null -> {
                if (!pinkclicked) {
                    btn_pink.setImageResource(R.drawable.ic_check_white)
                    pinkclicked = true
                    colorchoice = "분홍"
                }
            }
            "분홍" -> {
                btn_pink.setImageIcon(null)
                pinkclicked = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun lightpinkclicked () {
        when (colorchoice) {
            null -> {
                if (!lightpinkclicked ) {
                    btn_lightpink.setImageResource(R.drawable.ic_check_white)
                    lightpinkclicked  = true
                    colorchoice = "연분홍"
                }
            }
            "연분홍" -> {
                btn_lightpink.setImageIcon(null)
                lightpinkclicked  = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun greenclicked () {
        when (colorchoice) {
            null -> {
                if (!greenclicked ) {
                    btn_green.setImageResource(R.drawable.ic_check_white)
                    greenclicked  = true
                    colorchoice = "초록"
                }
            }
            "초록" -> {
                btn_green.setImageIcon(null)
                greenclicked  = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun kakiclicked () {
        when (colorchoice) {
            null -> {
                if (!kakiclicked ) {
                    btn_kaki.setImageResource(R.drawable.ic_check_white)
                    kakiclicked  = true
                    colorchoice = "카키"
                }
            }
            "카키" -> {
                btn_kaki.setImageIcon(null)
                kakiclicked  = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun blueclicked () {
        when (colorchoice) {
            null -> {
                if (!blueclicked ) {
                    btn_blue.setImageResource(R.drawable.ic_check_white)
                    blueclicked  = true
                    colorchoice = "파랑"
                }
            }
            "파랑" -> {
                btn_blue.setImageIcon(null)
                blueclicked  = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun redclicked() {
        when (colorchoice) {
            null -> {
                if (!redclicked) {
                    btn_red.setImageResource(R.drawable.ic_check_white)
                    redclicked = true
                    colorchoice = "빨강"
                }
            }
            "빨강" -> {
                btn_red.setImageIcon(null)
                redclicked = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun wineclicked() {
        when (colorchoice) {
            null -> {
                if (!wineclicked ) {
                    btn_wine.setImageResource(R.drawable.ic_check_white)
                    wineclicked = true
                    colorchoice = "와인"
                }
            }
            "와인" -> {
                btn_wine.setImageIcon(null)
                wineclicked = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun brownclicked() {
        when (colorchoice) {
            null -> {
                if (!brownclicked) {
                    btn_brown.setImageResource(R.drawable.ic_check_white)
                    brownclicked = true
                    colorchoice = "갈색"
                }
            }
            "갈색" -> {
                btn_brown.setImageIcon(null)
                brownclicked = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun purpleclicked() {
        when (colorchoice) {
            null -> {
                if (!purpleclicked) {
                    btn_purple.setImageResource(R.drawable.ic_check_white)
                    purpleclicked = true
                    colorchoice = "보라"
                }
            }
            "보라" -> {
                btn_purple.setImageIcon(null)
                purpleclicked = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun navyclicked() {
        when (colorchoice) {
            null -> {
                if (!navyclicked) {
                    btn_navy.setImageResource(R.drawable.ic_check_white)
                    navyclicked = true
                    colorchoice = "네이비"
                }
            }
            "네이비" -> {
                btn_navy.setImageIcon(null)
                navyclicked = false
                colorchoice = null
            }
            else -> {
                Toast.makeText(activity, "하나의 항목만 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }



}



