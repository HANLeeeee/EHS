package com.example.ehs.Closet

import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import java.io.*


open class ClothesUpload_Request(
    method: Int,
    url: String?,
    listener: Response.Listener<NetworkResponse>?,
    errorListener: Response.ErrorListener
) : Request<NetworkResponse?>(method, url, errorListener) {


    private val twoHyphens = "--"
    private val lineEnd = "\r\n"
    private val boundary = "apiclient-" + System.currentTimeMillis()

    private var mListener: Response.Listener<NetworkResponse>? = null
    private var mErrorListener: Response.ErrorListener? = null
    private val mHeaders: Map<String, String>? = null


    init {
        this.mListener = listener
        this.mErrorListener = errorListener
    }


    @Throws(AuthFailureError::class)
    override fun getHeaders(): Map<String, String> {
        return mHeaders ?: super.getHeaders()
    }
    override fun getBodyContentType(): String? {
        return "multipart/form-data;boundary=$boundary"
    }
    // populate data byte payload



    // close multipart form data after text and file data

    @Throws(AuthFailureError::class)
    override fun getBody() : ByteArray? {
        val bos = ByteArrayOutputStream()
        val dos = DataOutputStream(bos)
        try {
            // populate text payload
            val params = params
            if (params != null && params.size > 0) {
                textParse(dos, params, paramsEncoding)
            }

            // populate data byte payload
            // populate data byte payload
            val data: Map<String, DataPart>? = getByteData()
            if (data != null && data.size > 0) {
                dataParse(dos, data)
            }

            // close multipart form data after text and file data
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)
            return bos.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Custom method handle data payload.
     *
     * @return Map data part label with data byte
     * @throws AuthFailureError
     */

    protected open fun getByteData(): Map<String, DataPart>? {
        return null
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<NetworkResponse?>? {
        return try {
            Response.success(
                response,
                HttpHeaderParser.parseCacheHeaders(response)
            )
        } catch (e: Exception) {
            Response.error(ParseError(e))
        }
    }

    override fun deliverResponse(response: NetworkResponse?) {
        mListener!!.onResponse(response)
    }

    override fun deliverError(error: VolleyError?) {
        mErrorListener!!.onErrorResponse(error)
    }

    /**
     * Parse string map into data output stream by key and value.
     *
     * @param dataOutputStream data output stream handle string parsing
     * @param params           string inputs collection
     * @param encoding         encode the inputs, default UTF-8
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun textParse(
        dataOutputStream: DataOutputStream,
        params: Map<String, String>,
        encoding: String
    ) {
        try {
            for ((key, value) in params) {
                buildTextPart(dataOutputStream, key, value)
            }
        } catch (uee: UnsupportedEncodingException) {
            throw RuntimeException("Encoding not supported: $encoding", uee)
        }
    }

    /**
     * Parse data into data output stream.
     *
     * @param dataOutputStream data output stream handle file attachment
     * @param data             loop through data
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun dataParse(dataOutputStream: DataOutputStream, data: Map<String, DataPart>) {
        for ((key, value) in data) {
            buildDataPart(dataOutputStream, value, key)
        }
    }

    /**
     * Write string data into header and data output stream.
     *
     * @param dataOutputStream data output stream handle string parsing
     * @param parameterName    name of input
     * @param parameterValue   value of input
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun buildTextPart(
        dataOutputStream: DataOutputStream,
        parameterName: String,
        parameterValue: String
    ) {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"$parameterName\"$lineEnd")
        dataOutputStream.writeBytes(lineEnd)
        dataOutputStream.writeBytes(parameterValue + lineEnd)
    }

    /**
     * Write data file into header and data output stream.
     *
     * @param dataOutputStream data output stream handle data parsing
     * @param dataFile         data byte as DataPart from collection
     * @param inputName        name of data input
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun buildDataPart(
        dataOutputStream: DataOutputStream,
        dataFile: DataPart,
        inputName: String
    ) {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
        dataOutputStream.writeBytes(
            "Content-Disposition: form-data; name=\"" +
                    inputName + "\"; filename=\"" + dataFile.fileName + "\"" + lineEnd
        )
        if (dataFile.type.trim { it <= ' ' }.isNotEmpty()) {
            dataOutputStream.writeBytes("Content-Type: " + dataFile.type + lineEnd)
        }
        dataOutputStream.writeBytes(lineEnd)
        val fileInputStream = ByteArrayInputStream(dataFile.content)
        var bytesAvailable: Int = fileInputStream.available()
        val maxBufferSize = 1024 * 1024
        var bufferSize = Math.min(bytesAvailable, maxBufferSize)
        val buffer = ByteArray(bufferSize)
        var bytesRead: Int = fileInputStream.read(buffer, 0, bufferSize)
        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize)
            bytesAvailable = fileInputStream.available()
            bufferSize = Math.min(bytesAvailable, maxBufferSize)
            bytesRead = fileInputStream.read(buffer, 0, bufferSize)
        }
        dataOutputStream.writeBytes(lineEnd)
    }

    class DataPart {
        var fileName: String = ""
        lateinit var content: ByteArray
        val type: String = ""

        constructor() {}

        constructor(name: String, data: ByteArray) {
            fileName = name
            content = data
        }


    }



}