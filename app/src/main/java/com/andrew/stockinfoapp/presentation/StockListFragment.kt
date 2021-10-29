package com.andrew.stockinfoapp.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrew.stockinfoapp.databinding.FragmentStockListBinding
import com.andrew.stockinfoapp.domain.Stock
import com.andrew.stockinfoapp.framework.StockAdapter

class StockListFragment : Fragment() {
    private var _binding: FragmentStockListBinding? = null
    private val binding get() = _binding!!
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentStockListBinding.inflate(inflater, container, false)

        binding.stockList.addItemDecoration(
            DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        val adapter = StockAdapter(mContext, object: StockAdapter.OnItemClickListener {
            override fun onItemClicked(stock: Stock) {
                (mContext as MainActivity).showInfoFragment(stock.symbol)
            }
        })

        binding.stockList.adapter = adapter
        binding.stockList.layoutManager = LinearLayoutManager(activity)

        val model: StockListViewModel by viewModels()
        model.stocks.observe(this, { stocks ->
            adapter.updateData(stocks)
        })

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}
