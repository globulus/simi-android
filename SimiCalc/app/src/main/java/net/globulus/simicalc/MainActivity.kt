package net.globulus.simicalc

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import net.globulus.simi.ActiveSimi
import net.globulus.simi.api.SimiValue

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActiveSimi.load(readAssets("Stdlib.simi") + "\n\n" + readAssets("Calc.simi"))

        val left = findViewById<EditText>(R.id.left)
        val op = findViewById<EditText>(R.id.op)
        val right = findViewById<EditText>(R.id.right)

        findViewById<Button>(R.id.go).setOnClickListener {
            val l = left.text.toString().toDouble()
            val r = right.text.toString().toDouble()
            val o = op.text.toString()
            val res = ActiveSimi.eval("Calc", "compute", SimiValue.Number(l), SimiValue.String(o), SimiValue.Number(r));
            Toast.makeText(baseContext, res.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private fun readAssets(filename: String): String {
        return application.assets.open(filename).bufferedReader().use { it.readText() }
    }
}
