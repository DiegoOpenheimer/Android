package estudando.com.br.studykoin.cities.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import estudando.com.br.studykoin.R
import estudando.com.br.studykoin.model.City
import kotlinx.android.synthetic.main.list_adapter.view.*

class CityAdapter : RecyclerView.Adapter<CityAdapter.MyViewHolder>() {

    var cities: List<City> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_adapter, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = cities.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val city: City = cities[position]
        holder.view.name.text = city.name
        holder.view.information.text = city.id.toString()
    }

    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

}