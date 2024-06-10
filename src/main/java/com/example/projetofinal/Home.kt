package com.example.projetofinal

import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class Home : AppCompatActivity() {
    private var baseCurrency = "BRL"
    private var convertedToCurrency = "USD"
    private var exchangeRate = 0f
    private val apiKey = "b73e31276b1ee44024c6d1bf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        spinnerSetup()
        findViewById<Button>(R.id.button_convert).setOnClickListener {
            getApiResult()
        }
    }

    private fun changetext() {
        val baseCurrencyValue = findViewById<EditText>(R.id.et_firstconversion).text.toString().toFloat()
        val convertedCurrencyValue = baseCurrencyValue * exchangeRate
        findViewById<EditText>(R.id.et_secondconversion).setText(convertedCurrencyValue.toString())
    }

    private fun getApiResult() {
        val client = OkHttpClient()
        val url = "https://v6.exchangerate-api.com/v6/$apiKey/latest/$baseCurrency"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.string()?.let {
                    val jsonObject = JSONObject(it)
                    exchangeRate = jsonObject.getJSONObject("conversion_rates").getString(convertedToCurrency).toFloat()
                    runOnUiThread {
                        changetext()
                    }
                }
            }
        })
    }

    private fun spinnerSetup() {
        val spinnerBaseCurrency = findViewById<Spinner>(R.id.spinner_firstconversion)
        val spinnerConvertedCurrency = findViewById<Spinner>(R.id.spinner_secondconversion)

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerBaseCurrency.adapter = adapter
            spinnerConvertedCurrency.adapter = adapter
        }

        spinnerBaseCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                baseCurrency = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerConvertedCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                convertedToCurrency = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}
