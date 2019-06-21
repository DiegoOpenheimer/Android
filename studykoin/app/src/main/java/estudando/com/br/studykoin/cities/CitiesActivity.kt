package estudando.com.br.studykoin.cities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import estudando.com.br.studykoin.R
import estudando.com.br.studykoin.cities.adapter.CityAdapter
import kotlinx.android.synthetic.main.activity_cities.*
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class CitiesActivity : AppCompatActivity() {

    private val viewModel: CityViewModel by viewModel()

    private val cityAdapter: CityAdapter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cities)

        cityRecyclerView?.let {
            it.setHasFixedSize(true)
            it.adapter = cityAdapter
            it.layoutManager = LinearLayoutManager(this)
        }
        supportActionBar?.title = "Cities"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()
    }

    private fun init() {
        val id: Int = intent.getIntExtra("id", 0)
        val json: String = resources.openRawResource(R.raw.cities)
            .bufferedReader().use { it.readText() }
        viewModel.getCity(id, json)
        viewModel.getObservable().observe(this, Observer {
            cityAdapter.cities = it
            cityAdapter.notifyDataSetChanged()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

}
