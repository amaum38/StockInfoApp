package com.andrew.stockinfoapp.presentation

import android.app.SearchManager
import android.content.Context
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CursorAdapter
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.andrew.stockinfoapp.R
import com.andrew.stockinfoapp.databinding.ActivityMainBinding
import com.andrew.stockinfoapp.domain.NetworkResult
import com.andrew.stockinfoapp.domain.SearchableStock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchView: SearchView
    private lateinit var mStocks: List<SearchableStock>
    private lateinit var viewModel: MainViewModel
    private lateinit var cursorAdapter: CursorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.searchString.observe(this) { query ->
            if (query.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    when (val result = viewModel.getStocksFromQuery()) {
                        is NetworkResult.Success -> {
                            val cursor = MatrixCursor(
                                arrayOf(
                                    BaseColumns._ID,
                                    SearchManager.SUGGEST_COLUMN_TEXT_1
                                )
                            )
                            mStocks = (result.data as? List<SearchableStock>) ?: emptyList()
                            mStocks.forEachIndexed { index, item ->
                                cursor.addRow(
                                    arrayOf(
                                        index,
                                        item.name + " (" + item.symbol + ")"
                                    )
                                )
                            }

                            withContext(Dispatchers.Main) {
                                cursorAdapter.changeCursor(cursor)
                            }
                        }

                        is NetworkResult.Failure -> {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@MainActivity,
                                    result.errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val searchItem = menu?.findItem(R.id.app_bar_search)
        searchView = searchItem?.actionView as SearchView

        searchView.queryHint = getString(R.string.search)
        setupSearch()
        return super.onCreateOptionsMenu(menu)
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Setup the searchview with a cursor adapter and set text change listener
     */
    private fun setupSearch() {
        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.item_label)
        cursorAdapter = SimpleCursorAdapter(this, R.layout.suggestion_item,
            null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)

        searchView.suggestionsAdapter = cursorAdapter
        searchView.queryHint = getString(R.string.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard(searchView)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                if (query.length < 2)
                    return true

                //update the live data string which will trigger a new query
                viewModel.searchString.value = query
                return true
            }
        })

        searchView.setOnSuggestionListener(object: SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                hideKeyboard(searchView)
                searchView.onActionViewCollapsed()
                supportFragmentManager.primaryNavigationFragment?.let {
                    findNavController(it).navigate(StockListFragmentDirections.openStockDetails(
                        mStocks[position].symbol ?: ""))
                }

                return true
            }
        })
    }
}