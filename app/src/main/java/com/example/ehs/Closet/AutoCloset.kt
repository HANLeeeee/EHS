package com.example.ehs.Closet

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONArray
import org.json.JSONException


object AutoCloset {
    private val MY_CLOSET : String = "closet"

    fun setColorCnt(context: Context, input: ArrayList<String>) {
        val prefs : SharedPreferences = context.getSharedPreferences(MY_CLOSET, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val a = JSONArray()
        for (i in 0 until input.size) {
            a.put(input[i])
        }
        if (input.isNotEmpty()) {
            editor.putString("MY_ColorCnt", a.toString())
        } else {
            editor.putString("MY_ColorCnt", null)
        }
        editor.apply()

    }

    fun getColorCnt(context: Context): ArrayList<String> {
        val prefs : SharedPreferences = context.getSharedPreferences(MY_CLOSET, Context.MODE_PRIVATE)
        val arr = prefs.getString("MY_ColorCnt", "")
        val urls = ArrayList<String>()
        if (arr != null) {
            try {
                val a = JSONArray(arr)
                for (i in 0 until a.length()) {
                    val url = a.optString(i)
                    urls.add(url)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return urls

    }


    fun setClothesColor(context: Context, input: ArrayList<String>) {
        val prefs : SharedPreferences = context.getSharedPreferences(MY_CLOSET, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val a = JSONArray()
        for (i in 0 until input.size) {
            a.put(input[i])
        }
        if (input.isNotEmpty()) {
            editor.putString("MY_ClothesColor", a.toString())
        } else {
            editor.putString("MY_ClothesColor", null)
        }
        editor.apply()

    }

    fun getClothesColor(context: Context): ArrayList<String> {
        val prefs : SharedPreferences = context.getSharedPreferences(MY_CLOSET, Context.MODE_PRIVATE)
        val arr = prefs.getString("MY_ClothesColor", "")
        val urls = ArrayList<String>()
        if (arr != null) {
            try {
                val a = JSONArray(arr)
                for (i in 0 until a.length()) {
                    val url = a.optString(i)
                    urls.add(url)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return urls

    }

    fun setClothesName(context: Context, input: ArrayList<String>) {
        val prefs : SharedPreferences = context.getSharedPreferences(MY_CLOSET, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val a = JSONArray()
        for (i in 0 until input.size) {
            a.put(input[i])
        }
        if (input.isNotEmpty()) {
            editor.putString("MY_clothesName", a.toString())
        } else {
            editor.putString("MY_clothesName", null)
        }
        editor.apply()

    }

    fun getClothesName(context: Context): ArrayList<String> {
        val prefs : SharedPreferences = context.getSharedPreferences(MY_CLOSET, Context.MODE_PRIVATE)
        val arr = prefs.getString("MY_clothesName", "")
        val urls = ArrayList<String>()
        if (arr != null) {
            try {
                val a = JSONArray(arr)
                for (i in 0 until a.length()) {
                    val url = a.optString(i)
                    urls.add(url)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return urls

    }

    fun setClothesCategory(context: Context, input: ArrayList<String>) {
        val prefs : SharedPreferences = context.getSharedPreferences(MY_CLOSET, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val a = JSONArray()
        for (i in 0 until input.size) {
            a.put(input[i])
        }
        if (input.isNotEmpty()) {
            editor.putString("MY_ClothesCategory", a.toString())
        } else {
            editor.putString("MY_ClothesCategory", null)
        }
        editor.apply()

    }

    fun getClothesCategory(context: Context): ArrayList<String> {
        val prefs : SharedPreferences = context.getSharedPreferences(MY_CLOSET, Context.MODE_PRIVATE)
        val arr = prefs.getString("MY_ClothesCategory", "")
        val urls = ArrayList<String>()
        if (arr != null) {
            try {
                val a = JSONArray(arr)
                for (i in 0 until a.length()) {
                    val url = a.optString(i)
                    urls.add(url)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return urls

    }

    fun clearCloset(context: Context) {
        val prefs : SharedPreferences = context.getSharedPreferences(MY_CLOSET, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.clear()
        editor.commit()
    }

}