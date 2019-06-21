package estudando.com.br.studykoin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import estudando.com.br.studykoin.R
import estudando.com.br.studykoin.model.State
import kotlinx.android.synthetic.main.list_adapter.view.*

typealias OnClickItem = (State) -> Unit

class ListApapter : RecyclerView.Adapter<ListApapter.MyViewHolder>() {

    var states: List<State> = listOf()
    private lateinit var onClickitem: OnClickItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_adapter, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = states.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val state: State = states[position]
        holder.view.name.text = state.name
        holder.view.information.text = state.abbr
        holder.view.setOnClickListener{
            if (::onClickitem.isInitialized && onClickitem != null) {
                onClickitem(state)
            }
        }

    }

    fun setOnClickItem(onClickItem: OnClickItem) {
        onClickitem = onClickItem
    }


    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

}