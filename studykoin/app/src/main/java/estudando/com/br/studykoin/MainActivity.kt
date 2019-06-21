package estudando.com.br.studykoin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import estudando.com.br.studykoin.adapter.ListApapter
import estudando.com.br.studykoin.cities.CitiesActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    val mainViewModel: MainViewModel by viewModel()

    val listAdapter: ListApapter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val self = this

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(self)
            adapter = listAdapter.also {
                it.setOnClickItem { state ->
                    val intent = Intent(self, CitiesActivity::class.java)
                    intent.putExtra("id", state.id)
                    startActivity(intent)
                }
            }
        }

        init()
    }

    private fun init() {
        mainViewModel.getObservable().observe(this, Observer {
            it?.let { cities ->
                listAdapter.states = cities
                listAdapter.notifyDataSetChanged()
            }
        })
        val cityJson = resources.openRawResource(R.raw.states)
            .bufferedReader().use { it.readText() }
        mainViewModel.getCities(cityJson)
    }
}
