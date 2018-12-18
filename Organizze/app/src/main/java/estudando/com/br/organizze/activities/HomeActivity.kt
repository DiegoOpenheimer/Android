package estudando.com.br.organizze.activities

import adapter.AdapterMovement
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import estudando.com.br.organizze.R
import helper.stringToBase64

import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.floating_menu.*
import model.Movement
import model.User
import services.HandlerFirebase
import services.HandlerUserDatabaseFirebase
import java.text.DecimalFormat

class HomeActivity : AppCompatActivity() {

    private lateinit var listenUserEvents: ValueEventListener
    private lateinit var listenMovementsEvents: ValueEventListener
    private val movements: MutableList<Movement> = mutableListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<AdapterMovement.MyViewHolder>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var total: String? = null
    private var currentDate: String? = null
    private val firebaseDatabase: DatabaseReference = HandlerFirebase.getFirebaseDatabase()
    private val firebaseAuth: FirebaseAuth = HandlerFirebase.getFirebaseAuth()
    private val firebaseAuthLambda = { firebaseAuth: FirebaseAuth ->
        val user: FirebaseUser? = firebaseAuth.currentUser
        if ( user == null ) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        fabReceita.setOnClickListener {
            if ( menu.isOpened ) {
                menu.close(true)
            }
            startActivity(Intent(this, ReceitaActivity::class.java))
        }
        fabDespesa.setOnClickListener {
            if ( menu.isOpened ) {
                menu.close(true)
            }
            startActivity(Intent(this,  DespesaActivity::class.java))
        }
        firebaseAuth.addAuthStateListener(firebaseAuthLambda)
        getCurrentDate()
        buildList()
        buildSwipe()
    }

    private fun buildList() {
        viewManager = LinearLayoutManager( this )
        viewAdapter = AdapterMovement( movements )
        recyclerView = recyclerViewHome.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.logout) {
            firebaseAuth.signOut()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        firebaseAuth.removeAuthStateListener(firebaseAuthLambda)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if ( menu.isOpened ) {
            menu.close(true)
        } else {
            super.onBackPressed()
        }
    }

    private fun getCurrentDate() {
        mountCalendar()
        val calendarDay: CalendarDay = calendarView.currentDate
        currentDate = calendarDay.month.toString() + "" + calendarDay.year
        calendarView.setOnMonthChangedListener {
            materialCalendarView: MaterialCalendarView, calendarDay: CalendarDay ->
            currentDate = calendarDay.month.toString() + "" + calendarDay.year
            listenEventsFirebase()
        }
    }

    private fun mountCalendar() {
        val months: Array<String> =
            arrayOf("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro")
        calendarView.setTitleMonths( months )

    }

    private fun listenMovements() {

    }

    override fun onStart() {
        super.onStart()
        listenEventsFirebase()
    }


    override fun onStop() {
        super.onStop()
        clearEvents()
    }

    private fun listenEventsFirebase() {
        clearEvents()
        val user: FirebaseUser? = firebaseAuth.currentUser
        if ( user != null ) {
            listenUserEvents = firebaseDatabase.child("users")
                .child(user.email.toString().stringToBase64())
                .addValueEventListener( object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val mUser: User? = snapshot.getValue( User::class.java )
                        if ( mUser != null ) {
                            total = (mUser.totalIncoming - mUser.totalExpenditure).toString()
                            textGeneralBalance.setText("R$ " + DecimalFormat("0.00").format(total?.toDouble()))
                            textUserName.setText(mUser.name)
                        }
                    }

                } )
            listenMovementsEvents = firebaseDatabase.child("Movement")
                .child(user.email.toString().stringToBase64())
                .child(currentDate.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        movements.clear()
                        for ( children: DataSnapshot in snapshot.children ) {
                            val movement: Movement? = children.getValue( Movement::class.java )
                            if ( movement != null ) {
                                movement.key = children.key
                                movements.add( movement )
                            }
                        }
                        viewAdapter.notifyDataSetChanged()
                    }
                })
        }
    }

    private fun clearEvents() {
        if ( ::listenUserEvents.isInitialized ) {
            firebaseDatabase.removeEventListener(listenUserEvents)
        }
        if ( ::listenMovementsEvents.isInitialized ) {
            firebaseDatabase.removeEventListener(listenMovementsEvents)
        }
    }

    private fun buildSwipe() {
        val itemTouchCallback: ItemTouchHelper.Callback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(p0: RecyclerView, p1: RecyclerView.ViewHolder): Int {
                val flags = ItemTouchHelper.START or ItemTouchHelper.END
                return makeMovementFlags(0, flags)
            }

            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(holder: RecyclerView.ViewHolder, direction: Int) {
                val index: Int = holder.adapterPosition
                buildAlert(movements.get(index))
            }

        }
        ItemTouchHelper( itemTouchCallback ).attachToRecyclerView( recyclerView )
    }

    private fun buildAlert(movement: Movement) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this).apply {
            setCancelable( false )
            setTitle("Atenção")
            setMessage("Você realmente deseja remover esse movimento? essa ação não pode ser refeita")
            setPositiveButton("confirmar") {
                dialog: DialogInterface?, which: Int ->
                removeMovement(movement)
            }
            setNegativeButton("cancelar") {
                dialog: DialogInterface?, which: Int ->
                viewAdapter.notifyDataSetChanged()
            }
        }
        builder.create().show()
    }

    private fun removeMovement(movement: Movement) {
        val user: FirebaseUser? = firebaseAuth.currentUser
        firebaseDatabase.child("Movement")
            .child(user?.email.toString().stringToBase64())
            .child(currentDate.toString())
            .child(movement.key.toString())
            .removeValue()
            .addOnCompleteListener {
                if ( it.isSuccessful ) {
                    updateInformation(movement, user!!)
                    var message: String?
                    if ( movement.type.equals("r") ) {
                        message = "Receita removida"
                    } else {
                        message = "Despesa removida"
                    }
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateInformation(movement: Movement, user: FirebaseUser) {
        firebaseDatabase.child("users")
            .child(user.email.toString().stringToBase64())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val u: User? = snapshot.getValue( User::class.java )
                    var operation: String? = null
                    var value: Double? = null
                    if ( movement.type.equals("r") ) {
                        operation = "totalIncoming"
                        value = u!!.totalIncoming - movement.value!!.toDouble()
                    } else {
                        operation = "totalExpenditure"
                        value = u!!.totalExpenditure - movement.value!!.toDouble()
                    }
                    firebaseDatabase.child("users")
                        .child(user.email.toString().stringToBase64())
                        .child(operation)
                        .setValue( value )
                }
            })
    }

}
