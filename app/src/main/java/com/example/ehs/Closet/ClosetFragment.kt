package com.example.ehs.Closet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.ehs.Closet.ClothesSaveActivity.Companion.clothesSaveActivity_Dialog
import com.example.ehs.Loading
import com.example.ehs.Login.AutoLogin
import com.example.ehs.MainActivity
import com.example.ehs.R
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.fragment_closet.*
import kotlinx.android.synthetic.main.fragment_closet.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ClosetFragment : Fragment() {

    val Fragment.packageManager get() = activity?.packageManager // 패키지 매니저 적용

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            a!!,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            a!!,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            a!!,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            a!!,
            R.anim.to_bottom_anim
        )
    }
    private var clicked = false


    val REQUEST_IMAGE_CAPTURE = 1 // 카메라 사진 촬영 요청코드, 한번 지정되면 값이 바뀌지 않음
    val REQUEST_OPEN_GALLERY = 2

    lateinit var currentPhotoPath: String // 문자열 형태의 사진 경로 값 (초기 값을 null로 시작하고 싶을 때)


    lateinit var bmp: Bitmap
    lateinit var uploadImgName: String
    lateinit var originImgName: String

    val clotheslistfiter = mutableListOf<Clothes>()
    val clothesList = mutableListOf<Clothes>()
    val clothesList2 = mutableListOf<Clothes>()

    lateinit var userId: String

    val adapter = ClothesListAdapter(clothesList)
    var bgremoveloading: Loading? = null

    var before_page : Int = 0
    var after_page : Int = 0

    companion object {
        var a: Activity? = null
        const val TAG: String = "클로젯 프레그먼트"
        var clothesArr = ArrayList<String>()
        var clothesCategoryArr = ArrayList<String>()

        fun newInstance(): ClosetFragment { // newInstance()라는 함수를 호출하면 ClosetFragment를 반환함
            return ClosetFragment()
        }
    }

    // 프레그먼트가 메모리에 올라갔을때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ClosetFragment - onCreate() called")
        AndroidThreeTen.init(a)

        userId = AutoLogin.getUserId(a!!)
        bgremoveloading = Loading(a!!)


        //clothes테이블에서 나의 데이터가져오기
        //현재는 날씨
        clothesResponse()


    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "새로고침 실행")
        clothesList.clear()
        clothesSaveActivity_Dialog?.dismiss()

        clothesArr = AutoCloset.getClothesName(a!!)
        clothesCategoryArr = AutoCloset.getClothesCategory(a!!)

        if(clothesArr.size <= 18) {
            after_page = clothesArr.size
        } else {
            after_page = 18
        }
//        parseResult(before_page, after_page)
        var parse1 = parseResult()
        parse1.execute(before_page, after_page)

        Log.d("ㅁㅁㅁㅁㅁ새로고침222", clothesArr.toString())

    }


    // 프레그먼트를 안고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            a = context
        }
        Log.d(TAG, "ClosetFragment - onAttach() called")

    }

    // 뷰가 생성되었을 때 화면과 연결
    // 프레그먼트와 레이아웃을 연결시켜주는 부분이다.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d(TAG, "ClosetFragment - onCreateView() called")
        val view: View = inflater!!.inflate(R.layout.fragment_closet, container, false)

        view.tv_myclothes.setOnClickListener {
            (activity as MainActivity?)!!.replaceFragment(newInstance())
            Log.d("FeedFragment", "새로고침")

        }

        view.tv_mycody.setOnClickListener { view ->
            Log.d("ClosetFragment", "내 코디로 이동")
            if (requireFragmentManager().findFragmentByTag("cody") != null) {
                requireFragmentManager().beginTransaction().show(requireFragmentManager().findFragmentByTag("cody")!!).commit()
            } else {

                requireFragmentManager().beginTransaction().add(R.id.fragments_frame, CodyFragment(), "cody").commit()
            }
            if (requireFragmentManager().findFragmentByTag("closet") != null) {
                requireFragmentManager().beginTransaction().hide(requireFragmentManager().findFragmentByTag("closet")!!).commit()
            }
        }

        view.btn_add.setOnClickListener { view ->
            Log.d("클릭!!", "플러스 버튼 클릭!!")
            onAddButtonClicked()
        }
        view.btn_gallery.setOnClickListener { view ->
            Log.d("클릭!!", "갤러리 버튼 클릭!!")
            openGallery()
            onAddButtonClicked()
        }
        view.tv_gallery.setOnClickListener { view ->
            Log.d("클릭!!", "갤러리 텍스트 클릭!!")
            openGallery()
            onAddButtonClicked()
        }
        view.btn_camera.setOnClickListener { view ->
            Log.d("클릭!!", "카메라 버튼 클릭!!")
            takeCapture() // 기본 카메라 앱을 실행하여 사진 촬영
            onAddButtonClicked()
        }
        view.tv_camera.setOnClickListener { view ->
            Log.d("클릭!!", "카메라 텍스트 클릭!!")
            takeCapture() // 기본 카메라 앱을 실행하여 사진 촬영
            onAddButtonClicked()
        }
        view.btn_basic.setOnClickListener { view ->
            Log.d("클릭!!", "기본템 버튼 클릭!!")
            onAddButtonClicked()
            val intent = Intent(context, BasicClothesActivity::class.java)
            startActivity(intent)

        }
        view.tv_basic.setOnClickListener { view ->
            Log.d("클릭!!", "기본템 텍스트 클릭!!")
            onAddButtonClicked()
            val intent = Intent(context, BasicClothesActivity::class.java)
            startActivity(intent)

        }

        view.nsview.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            Log.d("피드갯수", "스크롤")
            view.nsprogress.isVisible = true
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {

                Log.d("피드갯수1", before_page.toString())
                Log.d("피드갯수2", after_page.toString())
                before_page += 18
                after_page += 18
                Log.d("피드갯수3", before_page.toString())
                Log.d("피드갯수4", after_page.toString())
                if(clothesArr.size <= after_page) {
                    Log.d("피드갯수5", clothesArr.size.toString())
                    Log.d("피드갯수6", after_page.toString())
                    after_page = clothesArr.size
                }

                if(before_page < after_page) {
                    val parse1 = parseResult()
                    parse1.execute(before_page, after_page)
                }

                if(clothesArr.size == clothesList.size) {
                    Log.d("하잇", clothesArr.size.toString())
                    Log.d("하잇", clothesList.size.toString())
                    Log.d("하잇", "시11qkfkfkfk")
                    clotheslistfiter.clear()
                    clotheslistfiter.addAll(clothesList)
                    view.nsprogress.isVisible = false
                }
            }
        })




        view.tv_all.setOnClickListener {
            clothesList.clear()
            clothesList.addAll(clotheslistfiter)
            adapter.notifyDataSetChanged()

            tv_change_ourcolor(view.tv_all)
            tv_change_dargray(view.tv_top)
            tv_change_dargray(view.tv_bottom)
            tv_change_dargray(view.tv_outer)
            tv_change_dargray(view.tv_onepiece)
            tv_change_dargray(view.tv_shoes)
            tv_change_dargray(view.tv_cap)
            tv_change_dargray(view.tv_bag)
            tv_change_dargray(view.tv_etc)

        }
        view.tv_top.setOnClickListener{
            filter("상의")
            view.nsprogress.isVisible = false
            tv_change_ourcolor(view.tv_top)
            tv_change_dargray(view.tv_all)
            tv_change_dargray(view.tv_bottom)
            tv_change_dargray(view.tv_outer)
            tv_change_dargray(view.tv_onepiece)
            tv_change_dargray(view.tv_shoes)
            tv_change_dargray(view.tv_cap)
            tv_change_dargray(view.tv_bag)
            tv_change_dargray(view.tv_etc)
        }
        view.tv_outer.setOnClickListener{
            filter("아우터")
            view.nsprogress.isVisible = false
            tv_change_ourcolor(view.tv_outer)
            tv_change_dargray(view.tv_all)
            tv_change_dargray(view.tv_top)
            tv_change_dargray(view.tv_bottom)
            tv_change_dargray(view.tv_onepiece)
            tv_change_dargray(view.tv_shoes)
            tv_change_dargray(view.tv_cap)
            tv_change_dargray(view.tv_bag)
            tv_change_dargray(view.tv_etc)
        }
        view.tv_bottom.setOnClickListener{
            filter("하의")
            view.nsprogress.isVisible = false
            tv_change_ourcolor(view.tv_bottom)
            tv_change_dargray(view.tv_all)
            tv_change_dargray(view.tv_top)
            tv_change_dargray(view.tv_outer)
            tv_change_dargray(view.tv_onepiece)
            tv_change_dargray(view.tv_shoes)
            tv_change_dargray(view.tv_cap)
            tv_change_dargray(view.tv_bag)
            tv_change_dargray(view.tv_etc)
        }
        view.tv_onepiece.setOnClickListener{
            filter("원피스")
            view.nsprogress.isVisible = false
            tv_change_ourcolor(view.tv_onepiece)
            tv_change_dargray(view.tv_all)
            tv_change_dargray(view.tv_top)
            tv_change_dargray(view.tv_outer)
            tv_change_dargray(view.tv_bottom)
            tv_change_dargray(view.tv_shoes)
            tv_change_dargray(view.tv_cap)
            tv_change_dargray(view.tv_bag)
            tv_change_dargray(view.tv_etc)
        }
        view.tv_shoes.setOnClickListener{
            filter("신발")
            view.nsprogress.isVisible = false
            tv_change_ourcolor(view.tv_shoes)
            tv_change_dargray(view.tv_all)
            tv_change_dargray(view.tv_top)
            tv_change_dargray(view.tv_outer)
            tv_change_dargray(view.tv_bottom)
            tv_change_dargray(view.tv_onepiece)
            tv_change_dargray(view.tv_cap)
            tv_change_dargray(view.tv_bag)
            tv_change_dargray(view.tv_etc)
        }
        view.tv_cap.setOnClickListener{
            filter("모자")
            view.nsprogress.isVisible = false
            tv_change_ourcolor(view.tv_cap)
            tv_change_dargray(view.tv_all)
            tv_change_dargray(view.tv_top)
            tv_change_dargray(view.tv_outer)
            tv_change_dargray(view.tv_bottom)
            tv_change_dargray(view.tv_onepiece)
            tv_change_dargray(view.tv_shoes)
            tv_change_dargray(view.tv_bag)
            tv_change_dargray(view.tv_etc)
        }
        view.tv_bag.setOnClickListener{
            filter("가방")
            view.nsprogress.isVisible = false
            tv_change_ourcolor(view.tv_bag)
            tv_change_dargray(view.tv_all)
            tv_change_dargray(view.tv_top)
            tv_change_dargray(view.tv_outer)
            tv_change_dargray(view.tv_bottom)
            tv_change_dargray(view.tv_onepiece)
            tv_change_dargray(view.tv_shoes)
            tv_change_dargray(view.tv_cap)
            tv_change_dargray(view.tv_etc)
        }
        view.tv_etc.setOnClickListener{
            filter("기타")
            view.nsprogress.isVisible = false
            tv_change_ourcolor(view.tv_etc)
            tv_change_dargray(view.tv_all)
            tv_change_dargray(view.tv_top)
            tv_change_dargray(view.tv_outer)
            tv_change_dargray(view.tv_bottom)
            tv_change_dargray(view.tv_onepiece)
            tv_change_dargray(view.tv_shoes)
            tv_change_dargray(view.tv_cap)
            tv_change_dargray(view.tv_bag)
        }
        return view
    }

    fun tv_change_ourcolor(textView : TextView) {
        textView.setTextColor(ContextCompat.getColor(a!!, R.color.ourcolor))
        textView.setTypeface(null, Typeface.BOLD)
        textView.textSize = 18F
    }

    fun tv_change_dargray(textView : TextView) {
        textView.setTextColor(ContextCompat.getColor(a!!, R.color.darkgray))
        textView.setTypeface(null, Typeface.NORMAL)
        textView.textSize = 16F
    }

    fun filter(category: String) {
        // 리스트의 모든 데이터를 검색한다.
        clothesList.clear()
        for (i in 0 until clotheslistfiter.size) {
            // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true 를 반환한다.
            if (clotheslistfiter[i].clothesCategory == category) {
                // 검색된 데이터를 리스트에 추가한다.
                clothesList.add(clotheslistfiter[i]
                )
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridLayoutManager = GridLayoutManager(a, 3)
        recyclerView.layoutManager = gridLayoutManager

        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        //recylerview 이거 fashionista.xml에 있는 변수
    }

    open inner class parseResult : AsyncTask<Int?, Int?, Bitmap>() {
        var a_bitmap: Bitmap? = null
        override fun onPreExecute() {
            view?.nsprogress!!.isVisible = true
        }

        override fun doInBackground(vararg pages: Int?): Bitmap {
            try {
                Log.d("널생각해~1~", pages[0].toString())
                Log.d("널생각해~2~", pages[1].toString())

                for (i in pages[0]!!.toInt() until pages[1]!!.toInt()) {
                    val url = URL("http://13.125.7.2/img/clothes/" + clothesArr[i])

                    val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
                    conn.setDoInput(true) //Server 통신에서 입력 가능한 상태로 만듦
                    conn.connect() //연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)
                    val iss: InputStream = conn.getInputStream() //inputStream 값 가져오기
                    a_bitmap = BitmapFactory.decodeStream(iss) // Bitmap으로 반환

                    var clothes = Clothes(a_bitmap, clothesCategoryArr[i])
                    clothesList.add(clothes)
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
        if (!clicked) {
            btn_gallery.visibility = View.VISIBLE
            btn_camera.visibility = View.VISIBLE
            btn_basic.visibility = View.VISIBLE
            tv_camera.visibility = View.VISIBLE
            tv_gallery.visibility = View.VISIBLE
            tv_basic.visibility = View.VISIBLE
        } else {
            btn_gallery.visibility = View.INVISIBLE
            btn_camera.visibility = View.INVISIBLE
            btn_basic.visibility = View.INVISIBLE
            tv_camera.visibility = View.INVISIBLE
            tv_gallery.visibility = View.INVISIBLE
            tv_basic.visibility = View.INVISIBLE
            btn_add.backgroundTintList = AppCompatResources.getColorStateList(a!!, R.color.white)
        }
    }

    fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            btn_gallery.startAnimation(fromBottom)
            btn_camera.startAnimation(fromBottom)
            btn_basic.startAnimation(fromBottom)
            tv_gallery.startAnimation(fromBottom)
            tv_camera.startAnimation(fromBottom)
            tv_basic.startAnimation(fromBottom)
            btn_add.startAnimation(rotateOpen)
        } else {
            btn_gallery.startAnimation(toBottom)
            btn_camera.startAnimation(toBottom)
            btn_basic.startAnimation(toBottom)
            tv_gallery.startAnimation(toBottom)
            tv_camera.startAnimation(toBottom)
            tv_basic.startAnimation(toBottom)
            btn_add.startAnimation(rotateClose)
        }
    }

    fun setClickable(clicked: Boolean) {
        if (!clicked) {
            btn_gallery.isClickable = true
            btn_camera.isClickable = true
            btn_basic.isClickable = true
            tv_camera.isClickable = true
            tv_gallery.isClickable = true
            tv_basic.isClickable = true
        } else {
            btn_gallery.isClickable = false
            btn_camera.isClickable = false
            btn_basic.isClickable = false
            tv_camera.isClickable = false
            tv_gallery.isClickable = false
            tv_basic.isClickable = false
        }
    }


    fun takeCapture() {
        // 기본 카메라 앱 실행
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager!!)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        a!!,
                        "com.example.closet.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }


    /**
     * 이미지 파일 생성
     */
    private fun createImageFile(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getActivity()?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("PNG_${timestamp}_", ".png", storageDir)
            .apply { currentPhotoPath = absolutePath }
    }


    // startAcitivityForResult를 통해서 기본 카메라 앱으로부터 받아온 사진 결과 값
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) { //resultCode가 Ok이고
                REQUEST_IMAGE_CAPTURE -> { // requestcode가 REQUEST_IMAGE_CAPTURE이면
                    val bitmap: Bitmap
                    val file = File(currentPhotoPath)
                    var fileuri = Uri.fromFile(file)
//                    uploadImgName = getName(fileuri)
                    if (Build.VERSION.SDK_INT < 28) { // 안드로이드 9.0 (Pie) 버전보다 낮을 경우
                        bitmap = MediaStore.Images.Media.getBitmap(a!!.contentResolver, fileuri)
//                        bmp = Bitmap.createScaledBitmap(bitmap!!, 400, 400, true)

//                        bmp = bitmap.rotate(90F) // value must be float
                        bmp = Bitmap.createScaledBitmap(bitmap, 400, 400, true)

                        Log.d("zz카메라", bmp.toString())

                    } else { // 안드로이드 9.0 (Pie) 버전보다 높을 경우
                        val decode = ImageDecoder.createSource(a!!.contentResolver, fileuri)
                        bitmap = ImageDecoder.decodeBitmap(decode)
//                        bmp = Bitmap.createScaledBitmap(bitmap!!, 400, 400, true)

//                        bmp = bitmap.rotate(90F) // value must be float
                        bmp = Bitmap.createScaledBitmap(bitmap, 400, 400, true)

                        Log.d("zz카메라", bmp.toString())


                    }

                    savePhoto(bitmap)
                    if (file.exists()) {
                        file.delete()
                    }
                    if (fileuri != null) {
                        fileuri = null
                    }
                }
                REQUEST_OPEN_GALLERY -> { // requestcode가 REQUEST_OPEN_GALLERY이면
                    val currentImageUrl: Uri? = data?.data // data의 data형태로 들어옴
                    uploadImgName = getName(currentImageUrl)

                    try {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(a!!.contentResolver, currentImageUrl)

                        bmp = bitmap.rotate(90F) // value must be float
                        bmp = Bitmap.createScaledBitmap(bmp, 400, 400, true)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            GlobalScope.launch(Dispatchers.Main) {
                launch(Dispatchers.Main) {
                    bgremoveloading!!.init("배경제거중")
                }
                delay(3000L)

                uploadBitmap(bmp)
            }

        } else {
            Toast.makeText(a!!, "취소하였습니다.", Toast.LENGTH_SHORT).show()
        }

    }

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
    }

    /**
     * 갤러리에 저장
     */
    private fun savePhoto(bitmap: Bitmap) {
        val folderPath =
            Environment.getExternalStorageDirectory().absolutePath + "/Pictures/Omonemo/" // 사진폴더로 저장하기 위한 경로 선언
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "${timestamp}.png"
        val folder = File(folderPath)
        if (!folder.isDirectory) { // 현재 해당 경로에 폴더가 존재하지 않는다면
            folder.mkdir() // make diretory 줄임말로 해당 경로에 폴더를 자동으로 새로 만든다
        }
        // 실제적인 저장처리
        val out = FileOutputStream(folderPath + fileName)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.close()
        Toast.makeText(a!!, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show()

    }

    /**
     * 갤러리 오픈 함수
     */
    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, REQUEST_OPEN_GALLERY)
    }


    // 파일명 찾기
    private fun getName(uri: Uri?): String {
        val projection = arrayOf(MediaStore.Images.ImageColumns.DISPLAY_NAME)
        val cursor = a!!.managedQuery(uri, projection, null, null, null)
        val column_index: Int = cursor
            .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }


    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }


    fun uploadBitmap(bitmap: Bitmap) {
        val clothesUploadRequest: ClothesUpload_Request = object : ClothesUpload_Request(
            Method.POST, "http://13.125.7.2/upload4.php",
            Response.Listener<NetworkResponse> { response ->
                try {

                    val obj = JSONObject(String(response!!.data))
                    originImgName = obj.get("file_name") as String

                    Log.d("서버에 저장되어진 파일이름", originImgName)


//                    val intent = Intent(a, ClothesSaveActivity::class.java)
//                    intent.putExtra("originImgName", originImgName);
//                    Log.d(TAG, originImgName)
//                    startActivity(intent)

                    bgremove(originImgName)


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(a, error.message, Toast.LENGTH_LONG).show()
                Log.e("GotError", "" + error.message)
            }) {
            override fun getByteData(): Map<String, DataPart>? {
                val params: MutableMap<String, DataPart> = HashMap()
                val imagename = System.currentTimeMillis()
                val uploadImgName = imagename.toString()
                Log.d("은정이는 민재이모", uploadImgName)
                params["image"] = DataPart(
                    "$uploadImgName.PNG",
                    getFileDataFromDrawable(bitmap)!!
                )
                return params
            }
        }

        //adding the request to volley
        Volley.newRequestQueue(a).add(clothesUploadRequest)

    }


    fun bgremove(originImgName: String) {

        val responseListener: Response.Listener<String?> = Response.Listener<String?> { response ->
            try {

                var jsonObject = JSONObject(response)
                var success = jsonObject.getBoolean("success")
                Log.d(TAG, userId)

                if (success) {

                    bgremoveloading?.finish()
                    val intent = Intent(a, ClothesSaveActivity::class.java)
                    intent.putExtra("originImgName", originImgName);
                    Log.d(TAG, originImgName)
                    startActivity(intent)

                }

            } catch (e: JSONException) {
                Toast.makeText(a!!, "배경제거실패 ㅜ", Toast.LENGTH_SHORT).show()
            }
        }
        val clothesBgremove_Request = ClothesBgremove_Request(originImgName, responseListener)
        val queue = Volley.newRequestQueue(a!!)
        queue.add(clothesBgremove_Request)

    }


    fun clothesResponse() {

        var cuserId: String
        var cColor: String
        var cColorArr = mutableListOf<String>()
        val responseListener: Response.Listener<String?> = object : Response.Listener<String?> {
            override fun onResponse(response: String?) {
                try {

                    var jsonObject = JSONObject(response)

                    val arr: JSONArray = jsonObject.getJSONArray("response")

                    for (i in 0 until arr.length()) {
                        val proObject = arr.getJSONObject(i)

                        cuserId = proObject.getString("userId")
                        cColor = proObject.getString("clothesColor")

                        cColorArr.add(cColor)

                        //이거해주면 마이페이지프래그먼트랑 겹쳐서 오토클로젯에 하나더 만들어줘야할듯
//                        AutoCloset.setClothesColor(a!!, cColorArr as ArrayList<String>)

                    }


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
        val clothesResponse = Clothes_Response(userId, responseListener)
        val queue = Volley.newRequestQueue(a!!)
        queue.add(clothesResponse)


    }


}