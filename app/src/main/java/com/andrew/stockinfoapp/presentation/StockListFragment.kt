package com.andrew.stockinfoapp.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrew.stockinfoapp.databinding.FragmentStockListBinding
import com.andrew.stockinfoapp.domain.Stock
import com.andrew.stockinfoapp.framework.StockAdapter
import kotlinx.coroutines.launch

class StockListFragment : Fragment() {
    private var _binding: FragmentStockListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStockListBinding.inflate(inflater, container, false)

        binding.stockList.addItemDecoration(
            DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        val adapter = StockAdapter(object: StockAdapter.OnItemClickListener {
            override fun onItemClicked(stock: Stock) {
                findNavController().navigate(
                    StockListFragmentDirections.openStockDetails(stock.symbol)
                )
            }
        })

        binding.stockList.adapter = adapter
        binding.stockList.layoutManager = LinearLayoutManager(activity)

        val model: StockListViewModel by viewModels()
        model.stocks.observe(viewLifecycleOwner) { stocks ->
            adapter.updateData(stocks)
            model.checkForUpdates(adapter)
            binding.notice.visibility = if (model.stocks.value?.isEmpty() == true)
                View.VISIBLE else View.INVISIBLE
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val model: StockListViewModel by viewModels()
        lifecycleScope.launch {
            model.loadStockList()
        }
    }
}
