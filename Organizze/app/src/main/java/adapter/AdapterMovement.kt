package adapter

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import estudando.com.br.organizze.R
import kotlinx.android.synthetic.main.movements_list.view.*
import model.Movement
import java.text.DecimalFormat

class AdapterMovement(private val movements: MutableList<Movement>) : RecyclerView.Adapter<AdapterMovement.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.movements_list, parent, false) as View
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = movements.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val movement: Movement = movements[position]
        holder.itemView.textCategory.setText( movement.category )
        holder.itemView.textDescription.setText( movement.description )
        if ( movement.type.equals("r") ) {
            holder.itemView.textValue.setTextColor(Color.parseColor("#00D39E"))
            holder.itemView.textValue.setText( "R$ " + DecimalFormat("0.00").format(movement.value!!.toDouble()) )
        } else {
            holder.itemView.textValue.setText( "R$ - " + DecimalFormat("0.00").format(movement.value!!.toDouble()) )
            holder.itemView.textValue.setTextColor(Color.parseColor("#FF7064"))
        }

    }

    class MyViewHolder(val v: View) : RecyclerView.ViewHolder(v)

}