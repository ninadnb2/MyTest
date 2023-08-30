package com.example.mytest

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


class MainActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var adapter: ItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridView = findViewById(R.id.gridView)
        adapter = ItemsAdapter(this, emptyList())
        gridView.adapter = adapter

        // Fetch the list of items from the API
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://db.ezobooks.in/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                Log.e("Ninad","In Coroutine")
                val api = retrofit.create(ItemsApi::class.java)
                val response = api.getItems()

                if (response.status == "success") {
                    val items = response.items

                    Log.e("Ninad","In Coroutine Success")
                    withContext(Dispatchers.Main) {
                        adapter.items = items
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("Ninad","In Coroutine ELse")

                    // Handle error response
                }
            } catch (e: Exception) {
                // Handle exception
                Log.e("Ninad","In Coroutine exception")
            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
}


class ItemsAdapter(private val context: Context, var items: List<Item>) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Item = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val item = getItem(position)
        viewHolder.bind(item)

        return view
    }

    private class ViewHolder(view: View) {
        private val imageView: ImageView = view.findViewById(R.id.imageView)
        private val textViewName: TextView = view.findViewById(R.id.textViewName)
        private val textViewPrice: TextView = view.findViewById(R.id.textViewPrice)

        fun bind(item: Item) {
            textViewName.text = item.itemName
            textViewPrice.text = "$ ${item.itemPrice}"

            Picasso.get().load(item.url).into(imageView)
        }
    }
}



data class Item(val itemName: String, val itemPrice: Int, val itemBarcode: String, val url: String)

interface ItemsApi {

    @GET("/kappa/image/task")
    suspend fun getItems(): ApiResponse
}

data class ApiResponse(val status: String, val items: List<Item>)
