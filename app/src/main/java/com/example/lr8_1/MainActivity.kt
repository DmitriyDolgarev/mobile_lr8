package com.example.lr8_1

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.lr8_1.adapter.CryptocurrencyAdapter
import com.example.lr8_1.model.CryptocurrencyModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var adapter: CryptocurrencyAdapter
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        initial()

        //var url = "https://i.pinimg.com/736x/b6/fa/73/b6fa7319362df17936f5819d8eb6cf9d.jpg"

        //loadImage(url)
        //loadRate("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initial()
    {
        recyclerView = findViewById(R.id.rv)
        adapter = CryptocurrencyAdapter()
        recyclerView.adapter = adapter

        adapter.setList(createList())
    }

    private fun createList(): ArrayList<CryptocurrencyModel>
    {
        var list = ArrayList<CryptocurrencyModel>()

        val crpt = CryptocurrencyModel("BTC", "https://i.pinimg.com/736x/b6/fa/73/b6fa7319362df17936f5819d8eb6cf9d.jpg", "https://api.bybit.com/v5/market/tickers?category=spot&symbol=BTCUSDT")
        list.add(crpt)

        val crpt_a = CryptocurrencyModel("ETH", "https://avatars.mds.yandex.net/i?id=649dce5ecddcb0ca31035c5b767c9c78_l-5236430-images-thumbs&n=13", "https://api.bybit.com/v5/market/tickers?category=spot&symbol=ETHUSDT")
        list.add(crpt_a)

        val crpt_b = CryptocurrencyModel("DOGE", "https://i.pinimg.com/736x/98/bb/77/98bb7768fa7a7a9ca88e05a2e76defe7.jpg", "https://api.bybit.com/v5/market/tickers?category=spot&symbol=DOGEUSDT")
        list.add(crpt_b)

        return list
    }

    private fun loadImage(imageUrl: String) {
        CoroutineScope(Dispatchers.Main).launch { // Запускаем Coroutine в Main Dispatcher (для UI)

            Glide.with(this@MainActivity)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        //img.setImageBitmap(resource) // Устанавливаем изображение в ImageView
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Этот метод вызывается, когда Glide очищает изображение
                        // (например, если View был переиспользован).  Можно использовать
                        // placeholder для отображения временно
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        // Обработка ошибки загрузки (например, отображение изображения ошибки)
                        // Можно использовать errorDrawable для отображения изображения об ошибке
                        // Например: imageView.setImageResource(R.drawable.error_image)
                        //img.setImageResource(R.drawable.ic_launcher_background)
                    }
                })
        }
    }

    private fun loadRate(url: String)
    {
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url(url)
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
            // Парсим JSON строку в объект BtcPrice
            //val btcPrice: BtcPrice = gson.fromJson(result, BtcPrice::class.java)

            // Извлекаем значение usd из объекта
            //val usdPrice = btcPrice.bitcoin.usd

            // Обновляем TextView в основном потоке
            //textView.text = usdPrice.toString()
        }
    }

}