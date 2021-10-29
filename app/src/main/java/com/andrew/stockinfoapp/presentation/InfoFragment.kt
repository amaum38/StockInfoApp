package com.andrew.stockinfoapp.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import androidx.lifecycle.ViewModelProvider
import com.andrew.stockinfoapp.R
import com.andrew.stockinfoapp.framework.Endpoints
import com.andrew.stockinfoapp.databinding.FragmentInfoBinding
import com.andrew.stockinfoapp.domain.Stock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

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
            GlobalScope.launch {
                //Either add or remove based on if it is alread in the database
                viewModel.stock.value?.let { it -> if (viewModel.isInList.value == true)
                    viewModel.removeStock(it) else viewModel.addStock(it) }

                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.info, activity?.supportFragmentManager
                        ?.findFragmentByTag("list") ?: StockListFragment(), "List")
                    ?.commit()
            }
        }

        viewModel = ViewModelProvider(this)[InfoViewModel::class.java]
        viewModel.stock.observe(viewLifecycleOwner, { stock ->
            if (stock.name == null) {
                setViewAndChildrenVisible(binding.root, View.INVISIBLE)
                Toast.makeText(activity, getString(R.string.no_info_found), Toast.LENGTH_SHORT).show()

                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.info, activity?.supportFragmentManager
                        ?.findFragmentByTag("list") ?: StockListFragment(), "List")
                    ?.commit()
            } else {
                setViewAndChildrenVisible(binding.root, View.VISIBLE)
                binding.name.text = stock.name
                binding.description.text = stock.description
                binding.eps.text = stock.eps
                binding.peRatio.text = stock.peRatio
                binding.yearHigh.text = stock.yearHigh
                binding.yearLow.text = stock.yearLow
            }
        })

        viewModel.symbol.observe(viewLifecycleOwner, {
            GlobalScope.launch {
                val matching = viewModel.interactors.getStocksBySymbol(symbol)
                if (matching.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        viewModel.stock.value = matching[0]
                    }
                } else {
                    loadInfo()
                }
            }
        })

        viewModel.isInList.observe(this, {
            binding.addStock.text = getString(if (it) R.string.remove else R.string.add)
        })

        setSymbol(symbol)

        return binding.root
    }
    private fun loadInfo() {
        val api: Endpoints by inject()
        val call = api.getOverview(symbol, getString(R.string.api_key2))
        call.enqueue(object : Callback<Stock> {
            override fun onResponse(call: Call<Stock>, response: Response<Stock>) {
                if (response.isSuccessful) {
                    val stock = response.body()
                    stock?.lastUpdate = SimpleDateFormat("dd/M/yyyy hh:mm:ss",
                        Locale.US).format(Date())
                    stock?.dailyData = emptyList()
                    viewModel.stock.value = stock
                } else {
                    setViewAndChildrenVisible(binding.root, View.INVISIBLE)
                    Toast.makeText(activity, "${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Stock>, t: Throwable) {
                setViewAndChildrenVisible(binding.root, View.INVISIBLE)
                Toast.makeText(activity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setViewAndChildrenVisible(view: View, visible: Int) {
        view.visibility = visible
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                setViewAndChildrenVisible(child, visible)
            }
        }
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