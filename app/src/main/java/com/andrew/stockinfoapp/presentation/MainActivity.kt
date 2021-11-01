package com.andrew.stockinfoapp.presentation

import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.andrew.stockinfoapp.framework.Endpoints
import com.andrew.stockinfoapp.R
import com.andrew.stockinfoapp.databinding.ActivityMainBinding
import com.andrew.stockinfoapp.domain.SearchableStockItems
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.widget.*
import androidx.fragment.app.Fragment
import com.andrew.stockinfoapp.domain.SearchableStock

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchView: SearchView
    private lateinit var mStocks: List<SearchableStock>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.info,  StockListFragment(), "list")
                .commit()
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

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun setupSearch() {
        val api: Endpoints by inject()
        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.item_label)
        val cursorAdapter = SimpleCursorAdapter(this, R.layout.suggestion_item,
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

                val call = api.getSymbols(query, getString(R.string.api_key))
                call.enqueue(object : Callback<SearchableStockItems> {
                    override fun onResponse(call: Call<SearchableStockItems>,
                                            response: Response<SearchableStockItems>) {
                        if (response.isSuccessful) {
                            val cursor = MatrixCursor(arrayOf(BaseColumns._ID,
                                SearchManager.SUGGEST_COLUMN_TEXT_1))
                            val stocks = response.body()?.bestMatches
                            if (stocks != null && stocks.isNotEmpty()) {
                                mStocks = stocks
                                stocks.forEachIndexed { index, item ->
                                    cursor.addRow(arrayOf(index,
                                        item.name + " (" + item.symbol + ")"))
                                }
                            }

                            cursorAdapter.changeCursor(cursor)
                        } else {
                            Toast.makeText(this@MainActivity,
                                "${response.code()}",
                                Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<SearchableStockItems>, t: Throwable) {
                        Toast.makeText(this@MainActivity,
                            "${t.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                })

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
                val cursor = searchView.suggestionsAdapter.getItem(position) as Cursor
                val selection = cursor.getString(
                    cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                showInfoFragment(mStocks[position].symbol)
                return true
            }
        })
    }

    fun showInfoFragment(symbol: String) {
        val fragment: Fragment? = supportFragmentManager.findFragmentByTag("info")
        if (fragment != null && (fragment as InfoFragment).isVisible) {
            fragment.setSymbol(symbol)
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.info,  InfoFragment().also {
                    it.setSymbol(symbol)
                }, "info").commit()
        }
    }

    override fun onBackPressed() {
        val fragment: Fragment? = supportFragmentManager.findFragmentByTag("info")
        if (fragment != null && (fragment as InfoFragment).isVisible) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.info,  StockListFragment(), "list")
                .commit()
        } else {
            finish()
        }

    }
}