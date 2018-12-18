package estudando.com.br.contacts.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import estudando.com.br.contacts.Model.User
import estudando.com.br.contacts.R
import estudando.com.br.contacts.Utils.byteToBitmap
import estudando.com.br.contacts.Views.RegisterUser
import kotlinx.android.synthetic.main.list_adapter.view.*

class UserAdapter(private val users: MutableList<User>, private val context: Context, private val callback : (index: Int) -> Unit) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_adapter, parent, false) as View
        return UserViewHolder(view)

    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        holder.view.textName.setText(users.get(position).name.toString())
        holder.view.textEmail.setText(users.get(position).email.toString())
        holder.view.textPhone.setText(users.get(position).phone.toString())
        if (users.get(position).img != null) {
            holder.view.imageViewUser.setImageBitmap(users.get(position).img?.byteToBitmap())
        } else {
            holder.view.imageViewUser.setImageResource(R.drawable.person)
        }

        holder.view.cardView.setOnLongClickListener {
            showAlert(position)
            true
        }

        holder.view.cardView.setOnClickListener {
            val intent: Intent = Intent(context, RegisterUser::class.java)
            intent.putExtra("contact", users[position])
            intent.putExtra("editing", true)
            context.startActivity(intent)
        }

    }

    private fun showAlert(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.apply {
            setTitle("Atenção")
            setMessage("Deseja realmente excluir esse contato?")
            setPositiveButton("Sim") {
                v: DialogInterface, wich: Int ->
                callback(position)
            }
            setNegativeButton("Não") {v, m ->}
        }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    class UserViewHolder(val view: View) : RecyclerView.ViewHolder(view)


}