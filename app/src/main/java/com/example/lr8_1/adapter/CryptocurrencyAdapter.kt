package com.example.lr8_1.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.lr8_1.MainActivity
import com.example.lr8_1.R
import com.example.lr8_1.model.CryptocurrencyModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.URL

class CryptocurrencyAdapter: RecyclerView.Adapter<CryptocurrencyAdapter.CryptocurrencyViewHolder>() {

    data class ApiResponse(
        val retCode: Int,
        val retMsg: String,
        val result: Result,
        val retExtInfo: Any?,
        val time: Long
    )

    data class Result(
        val category: String,
        val list: List<TickerInfo>
    )

    data class TickerInfo(
        val symbol: String,
        val bid1Price: String,
        val bid1Size: String,
        val ask1Price: String,
        val ask1Size: String,
        val lastPrice: String,
        val prevPrice24h: String,
        val price24hPcnt: String,
        val highPrice24h: String,
        val lowPrice24h: String,
        val turnover24h: String,
        val volume24h: String,
        val usdIndexPrice: String
    )

    private var cryptocurrencyList = ArrayList<CryptocurrencyModel>()

    class CryptocurrencyViewHolder(view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptocurrencyViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_cryptocurrency_layout, parent, false)
        return CryptocurrencyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cryptocurrencyList.size
    }

    override fun onBindViewHolder(holder: CryptocurrencyViewHolder, position: Int) {

        loadImage(position, holder)

        holder.itemView.findViewById<TextView>(R.id.cryptocurrencyName).text = cryptocurrencyList[position].name
        //holder.itemView.findViewById<TextView>(R.id.cryptocurrencyName).text = "Имя"

        loadValue(position, holder)
        //holder.itemView.findViewById<TextView>(R.id.value).text = loadValue(position)
        //holder.itemView.findViewById<TextView>(R.id.value).text = "Курс"
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: ArrayList<CryptocurrencyModel>)
    {
        cryptocurrencyList = list
        notifyDataSetChanged()
    }

    private fun loadValue(position: Int, holder: CryptocurrencyViewHolder)
    {
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url(cryptocurrencyList[position].valueUrl)
                        .build()

                    val response = client.newCall(request).execute()

                    if (response.isSuccessful) {
                        response.body?.string() ?: "Ошибка: пустой ответ"  // Получаем тело ответа как строку или сообщение об ошибке
                    } else {
                        "Ошибка: ${response.code}" // Сообщение об ошибке, если запрос не успешен
                    }
                } catch (e: IOException) {
                    "Ошибка: ${e.message}" // Обрабатываем исключения, например, отсутствие сети
                }
            }

            var gson = Gson()

            val apiResponse: ApiResponse = gson.fromJson(result, ApiResponse::class.java)

            val tickerInfo = apiResponse.result.list[0]

            // Получаем значение bid1Price
            val bid1Price = tickerInfo.bid1Price

            holder.itemView.findViewById<TextView>(R.id.value).text = bid1Price
        }
    }

    private fun loadImage(position: Int, holder: CryptocurrencyViewHolder) {

        var url = cryptocurrencyList[position].imageUrl

        Thread { // Создаем новый поток
            var bitmap: Bitmap? = null
            try {
                val url = URL(url)
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: Exception) {
                // Обработка ошибки загрузки изображения
                e.printStackTrace()
            }

            // Обновляем UI в основном потоке
            (holder.itemView.context as? AppCompatActivity)?.runOnUiThread {
                holder.itemView.findViewById<ImageView>(R.id.cryptocurrencyImage).setImageBitmap(bitmap)
            }
        }.start() // Запускаем поток
    }
}