package com.codepath.flexbody.Nutrition

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.codepath.flexbody.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class NutritionDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gsonBuilder = GsonBuilder()
        val gson: Gson = gsonBuilder.create()
        setContentView(R.layout.nutrition_detail_activity)

        val item: Nutrition= gson.fromJson(intent.getStringExtra("item"),Nutrition::class.java)
        val nutritionName: TextView =findViewById(R.id.nutritionDetailName)
        val nutritionImage: ImageView =findViewById(R.id.nutritionDetailImage)
        nutritionName.text= item.data?.name

        Glide.with(nutritionImage)
            .load(item.data?.image)
            .placeholder(R.drawable.placeholder_small)
            .centerInside()
            .into(nutritionImage)














    }
}