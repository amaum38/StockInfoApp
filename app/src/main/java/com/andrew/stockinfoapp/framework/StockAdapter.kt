package com.andrew.stockinfoapp.framework

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.andrew.stockinfoapp.R
import com.andrew.stockinfoapp.domain.Stock
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.robinhood.spark.SparkAdapter
import com.robinhood.spark.SparkView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class StockAdapter(private val context: Context, val itemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<StockAdapter.ViewHolder>(), KoinComponent {
    private var stocks: List<Stock> = emptyList()

    fun updateData(stocks: List<Stock>) {
        this.stocks = stocks
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = stocks[position]

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.stock_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val stock = this.stocks[position]
        viewHolder.symbol.text = stock.symbol
        viewHolder.name.text = stock.name

        //Get a fresh data list if older than 24 hours or empty, otherwise go to the database
        val lastUpdate = SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.US)
            .parse(stock.lastUpdate)
        val daysOld = (Date().time - lastUpdate.time)/86400000;
        if (stock.dailyData.isEmpty() || daysOld >= 1) {
            getDailyInfo(stock.symbol, object : ResponseInterface {
                override fun onSucess(reversed: List<Float>) {
                    GlobalScope.launch {
                        //update database
                        stock.dailyData = reversed
                        stock.lastUpdate = SimpleDateFormat(
                            "dd/M/yyyy hh:mm:ss", Locale.US).format(Date())
                        val interactors: Interactors by inject()
                        interactors.updateStock(stock)
                    }

                    setSparklineAdapter(viewHolder, reversed)
                }

                override fun onFailure(code: String) {}
            })
        } else {
            setSparklineAdapter(viewHolder, stock.dailyData)
        }

        viewHolder.itemView.setOnClickListener {
            itemClickListener.onItemClicked(stocks[position])
        }
    }

    override fun getItemCount() = stocks.size

    override fun getItemId(position: Int): Long = position.toLong()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val symbol: TextView = view.findViewById(R.id.item_label)
        val name: TextView = view.findViewById(R.id.item_name)
        val price: TextView = view.findViewById(R.id.last_price)
        val chart: SparkView = view.findViewById(R.id.sparkview)
    }

    /**
     * Get the daily price info to populate sparkline
     */
    private fun getDailyInfo(symbol: String, responseInterface: ResponseInterface) {
        var data: MutableList<Float> = mutableListOf()
        val api: Endpoints by inject()
        val call = api.getPriceData(symbol, "15min", context.getString(R.string.api_key2))
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful) {
                    if (response.body() != null && response.body()!!.isJsonObject) {
                    val responseObject: JsonObject = response.body() as JsonObject
                        if (responseObject.has("Time Series (15min)")) {
                            val timeSeries = responseObject.get("Time Series (15min)") as JsonObject
                            val keySet = timeSeries.keySet()

                            for (key in keySet) {
                                val series = timeSeries.get(key) as JsonObject;
                                data.add(series.get("4. close").asFloat)
                            }
                        }
                    }

                    responseInterface.onSucess(data.reversed())
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                t.message?.let { responseInterface.onFailure(it) }
            }
        })
    }

    private fun setSparklineAdapter(viewHolder: ViewHolder, data: List<Float>) {
        viewHolder.chart.adapter = object : SparkAdapter() {
            override fun getCount() = data.size
            override fun getItem(index: Int) = data[index]
            override fun getY(index: Int) = data[index]
        }

        if (data.isNotEmpty()) {
            viewHolder.chart.setLineColor(
                context.getColor(
                    if (data.last() > data.first())
                        android.R.color.holo_green_light else
                        android.R.color.holo_red_light
                )
            )

            viewHolder.price.text = String.format("%.2f", data.last())
        }
    }

    interface OnItemClickListener{
        fun onItemClicked(stock: Stock)
    }
}