package net.globulus.simicalc

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import net.globulus.simi.ActiveSimi
import net.globulus.simi.SimiMapper
import net.globulus.simi.api.SimiValue
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActiveSimi.setImportResolver {
            application.assets.open(it).bufferedReader().use { it.readText() }
        }
        ActiveSimi.load("Calc.simi", "RedditStats.simi")

        val left = findViewById<EditText>(R.id.left)
        val op = findViewById<EditText>(R.id.op)
        val right = findViewById<EditText>(R.id.right)

        findViewById<Button>(R.id.go).setOnClickListener {
            val l = left.text.toString().toDouble()
            val r = right.text.toString().toDouble()
            val o = op.text.toString()
            val res = ActiveSimi.eval("Calc", "compute", SimiValue.Number(l), SimiValue.String(o), SimiValue.Number(r))
            Toast.makeText(baseContext, res.toString(), Toast.LENGTH_LONG).show()
        }

        val responseView = findViewById<EditText>(R.id.response)
        val rf = Retrofit.Builder()
                .baseUrl("https://api.reddit.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        val service = rf.create(Api::class.java)
        service.get("3").enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable?) {
                Log.e("SimICalc", t!!.localizedMessage)
            }

            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (response!!.isSuccessful) {
                    val json = response.body()
                    Log.e("SimiCalc", json)
                    ActiveSimi.evalAsync(ActiveSimi.Callback { parsed ->
                        val sb = StringBuilder("Top posters:\n\n")
                        for ((key, value) in SimiMapper.fromObject(parsed.value.`object`)) {
                            sb.append(key).append(" with ").append(value).append(" comments\n")
                        }
                        responseView.setText(sb.toString())
                    },"RedditStats", "getStats", SimiMapper.toSimiProperty(json))
                } else {
                    Log.e("SimiCalc", response.errorBody()!!.string())
                }
            }
        })
    }

    interface Api {
        @GET("/top.json") fun get(@Query("limit") limit: String): Call<String>
    }
}
