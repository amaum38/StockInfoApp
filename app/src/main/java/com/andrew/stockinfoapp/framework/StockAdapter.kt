package com.andrew.stockinfoapp.framework

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.andrew.stockinfoapp.R
import com.andrew.stockinfoapp.domain.Stock
import com.robinhood.spark.SparkAdapter
import com.robinhood.spark.SparkView
import org.koin.core.component.KoinComponent

class StockAdapter(private val itemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<StockAdapter.ViewHolder>(), KoinComponent {
    private var stocks: List<Stock> = emptyList()

    fun updateData(stocks: List<Stock>) {
        this.stocks = stocks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.stock_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val stock = this.stocks[position]
        viewHolder.symbol.text = stock.symbol
        viewHolder.name.text = stock.name

        setSparklineAdapter(viewHolder, stock.dailyData)
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

    private fun setSparklineAdapter(viewHolder: ViewHolder, data: List<Float>) {
        viewHolder.chart.adapter = object : SparkAdapter() {
            override fun getCount() = data.size
            override fun getItem(index: Int) = data[index]
            override fun getY(index: Int) = data[index]
        }

        if (data.isNotEmpty()) {
            viewHolder.chart.setLineColor(
                viewHolder.itemView.context.getColor(
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