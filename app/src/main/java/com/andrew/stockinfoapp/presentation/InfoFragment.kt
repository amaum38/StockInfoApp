package com.andrew.stockinfoapp.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.andrew.stockinfoapp.R
import com.andrew.stockinfoapp.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {
    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InfoViewModel
    private lateinit var symbol: String

    fun setSymbol(symbol: String) {
        this.symbol = symbol
        if (this::viewModel.isInitialized) {
            viewModel.symbol.value = symbol
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)

        if (savedInstanceState != null) {
            symbol = savedInstanceState.getString("symbol", "")
        }

        binding.addStock.setOnClickListener {
            //Either add or remove based on if it is already in the database
            viewModel.stock.value?.let { it -> if (viewModel.isInList.value == true)
                viewModel.removeStock(it) else viewModel.addStock(it) }

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.info, activity?.supportFragmentManager
                    ?.findFragmentByTag("list") ?: StockListFragment(), "List")
                ?.commit()
        }

        viewModel = ViewModelProvider(this)[InfoViewModel::class.java]
        viewModel.stock.observe(viewLifecycleOwner, { stock ->
            if (stock.name == null) {
                Toast.makeText(activity, getString(R.string.no_info_found), Toast.LENGTH_SHORT).show()

                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.info, activity?.supportFragmentManager
                        ?.findFragmentByTag("list") ?: StockListFragment(), "List")
                    ?.commit()
            } else {
                binding.name.text = stock.name
                binding.description.text = stock.description
                binding.eps.text = stock.eps
                binding.peRatio.text = stock.peRatio
                binding.yearHigh.text = stock.yearHigh
                binding.yearLow.text = stock.yearLow
            }
        })

        viewModel.matching.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                viewModel.stock.value = it[0]
            } else {
                val code = viewModel.loadInfo(symbol)
                if (code != "")
                    Toast.makeText(activity, code, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.isInList.observe(viewLifecycleOwner, {
            binding.addStock.text = getString(if (it) R.string.remove else R.string.add)
        })

        setSymbol(symbol)

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("symbol", symbol)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}