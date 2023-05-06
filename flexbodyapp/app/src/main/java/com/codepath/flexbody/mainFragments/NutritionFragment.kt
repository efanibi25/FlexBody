package com.codepath.flexbody

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.codepath.flexbody.Nutrition.Nutrition
import com.codepath.flexbody.Nutrition.NutritionAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.Headers


class NutritionFragment : Fragment() {
    lateinit var nutritionlist:MutableList<Nutrition>
    var search:String?=null
    lateinit var adapter: NutritionAdapter
    private lateinit var searchProgressBar: ContentLoadingProgressBar
    private lateinit var emptySearchText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    fun request(offset:Int=0,term: String? =null) {
        emptySearchText.visibility = View.INVISIBLE

        val client = AsyncHttpClient()
        val params = RequestParams()
        params["format"] = "json"
        params["limit"] = "100"
        params["offset"] = offset.toString()
        var url = "https://wger.de/api/v2/ingredientinfo"

        if (term != null) {
            if (term != this.search) {
                Log.d("changed", "array changed")

                this.search = term
                var size = nutritionlist.size
                for (i in 0 until size) {
                    nutritionlist.removeAt(0)
                }
                adapter.notifyItemRangeRemoved(0, size)

            }
            params["term"] = term
            url = "https://wger.de/api/v2/ingredient/search"
            client[url, params, object :
                JsonHttpResponseHandler()  {
                override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                    // called when response HTTP status is "200 OK"
//                    searchProgressBar.hide()
                    if(json.jsonObject!=null){
                        var resultList=json.jsonObject["suggestions"]
                        Log.d("list",resultList.toString())


                        val nutritionArray= object : TypeToken<MutableList<Nutrition>>() {}.type
                        val gsonBuilder = GsonBuilder()
                        val gson: Gson = gsonBuilder.create()
                        val new_nutritionItems: List<Nutrition> = gson.fromJson( resultList.toString(), nutritionArray)
                        for (item in new_nutritionItems){
                            if(item.data?.image!=null){
                                item.data?.image ="https://wger.de/${item.data?.image}"
                            }

                        }

                        for (item in new_nutritionItems){
                            Log.d("item insert",item.toString())
                            Log.d("arraysize",nutritionlist.size.toString())
                            nutritionlist.add(item)
                        }
                        adapter.notifyItemRangeInserted (nutritionlist.size-new_nutritionItems.size,new_nutritionItems.size)







                    }


                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    errorResponse: String,
                    t: Throwable?
                ) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.d("fail",errorResponse)
                    val empty:List<Nutrition> =emptyList()
//                    searchProgressBar.hide()



                }
            }]



        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nutrition_search_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        val item =
            menu.findItem(R.id.action_nutrition_search).actionView as androidx.appcompat.widget.SearchView
        item.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
//                searchProgressBar.show()
                request(0,query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvNutrition = view?.findViewById<View>(R.id.nutritionresults) as RecyclerView
        this.nutritionlist= arrayListOf()
        adapter = NutritionAdapter (this.context,this.nutritionlist)
        rvNutrition .adapter = adapter
        rvNutrition.layoutManager = LinearLayoutManager(context)
//        val searchView: SearchView = view.findViewById(R.id.nutritionsearch)
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                return false
//            }
//
//            override fun onQueryTextSubmit(query: String): Boolean {
//                request(0,query)
//                searchView.clearFocus();
//                return true
//
//
            }








    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.nutrition_fragment, container, false)

        activity?.title = getString(R.string.action_bar_nutrition_search)
        searchProgressBar = view.findViewById<View>(R.id.nutritionSearchProgressBar) as ContentLoadingProgressBar
        searchProgressBar.hide()
        emptySearchText = view.findViewById<View>(R.id.emptySearch) as TextView
        return view
    }


    companion object {
        fun newInstance(): NutritionFragment {
            return NutritionFragment()
        }
    }
}