package com.andrew.stockinfoapp.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andrew.stockinfoapp.R
import com.andrew.stockinfoapp.databinding.FragmentInfoBinding
import com.andrew.stockinfoapp.domain.Constants
import com.andrew.stockinfoapp.domain.NetworkResult
import com.andrew.stockinfoapp.domain.Stock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfoFragment : Fragment() {
    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InfoViewModel
    private val args: InfoFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        binding.addStock.setOnClickListener {
            //Either add or remove based on if it is already in the database
            viewModel.stock.value?.let { stock ->
                if (viewModel.isInList.value == true) {
                    viewModel.removeStock(stock)
                } else {
                    viewModel.addStock(stock)
                }
            }

            findNavController().popBackStack()
        }

        viewModel = ViewModelProvider(this)[InfoViewModel::class.java]
        viewModel.symbol.value = args.symbol
        viewModel.stock.observe(viewLifecycleOwner) { stock ->
            if (stock.name == null) {
                Toast.makeText(
                    activity, getString(R.string.no_info_found),
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().popBackStack()
            } else {
                binding.name.text = stock.name
                binding.description.text = stock.description
                binding.eps.text = stock.eps
                binding.peRatio.text = stock.peRatio
                binding.yearHigh.text = stock.yearHigh
                binding.yearLow.text = stock.yearLow
            }
        }

        viewModel.matching.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                viewModel.stock.value = it[0]
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    when (val result = viewModel.loadInfo(args.symbol)) {
                        is NetworkResult.Success -> {
                            withContext(Dispatchers.Main) {
                                viewModel.stock.value = result.data as Stock
                            }
                        }
                        is NetworkResult.Failure -> {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(activity, result.errorMessage, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }
        }

        viewModel.isInList.observe(viewLifecycleOwner) { isInList ->
            binding.addStock.text = getString(if (isInList) R.string.remove else R.string.add)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}