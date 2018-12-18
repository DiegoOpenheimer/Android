package estudando.com.br.contacts

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.miguelcatalan.materialsearchview.MaterialSearchView
import estudando.com.br.contacts.Adapter.UserAdapter
import estudando.com.br.contacts.Model.User
import estudando.com.br.contacts.Repository.UserRepository
import estudando.com.br.contacts.Views.RegisterUser
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

fun View.hideKeyboard() {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

class MainActivity : AppCompatActivity() {

    private lateinit var user: UserRepository
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recyclerView: RecyclerView
    private var users: MutableList<User> = mutableListOf()
    private var usersAll: MutableList<User> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fabMain.setOnClickListener {
            val intent: Intent = Intent(this, RegisterUser::class.java)
            startActivity(intent)
            if (searchView.isSearchOpen) {
                searchView.closeSearch()
            }
            true
        }
        user = UserRepository(applicationContext)
        setSupportActionBar(toolbar)
        searchView.setVoiceSearch(true)
        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val listUsers: MutableList<User> = usersAll.filter { it.name!!.contains(newText.toString(), true) }.toMutableList()
                if (listUsers.isNotEmpty()) {
                    users.clear()
                    users.addAll(listUsers)
                    viewAdapter.notifyDataSetChanged()
                } else {
                    users.clear()
                    users.addAll(usersAll)
                    viewAdapter.notifyDataSetChanged()
                }
                return false
            }
        })

        searchView.setOnSearchViewListener(object:MaterialSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                users.clear()
                users.addAll(usersAll)
                viewAdapter.notifyDataSetChanged()
            }

            override fun onSearchViewShown() {
            }
        })
        getUser()
        builderRecyclerView()
    }

    fun getUser() {
        try {
            usersAll = user.getAll()
            users.clear()
            users.addAll(usersAll)
            viewAdapter.notifyDataSetChanged()
            if (users.isEmpty()) {
                mRecyclerView.visibility = View.GONE
                mView.visibility = View.VISIBLE
            } else {
                mRecyclerView.visibility = View.VISIBLE
                mView.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()
        getUser()
        builderRecyclerView()
    }

    private fun builderRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = UserAdapter(users, this, ::removeUser)
        recyclerView = mRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun removeUser(index: Int) {
        try {
            user.deleteUser(users.get(index))
            users.removeAt(index)
            viewAdapter.notifyDataSetChanged()
            if (users.isEmpty()) {
                mRecyclerView.visibility = View.GONE
                mView.visibility = View.VISIBLE
            } else {
                mRecyclerView.visibility = View.VISIBLE
                mView.visibility = View.GONE
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.a_z -> {
                users.sortBy { it.name?.toUpperCase() }
                viewAdapter.notifyDataSetChanged()
                true
            }
            R.id.z_a -> {
                users.sortByDescending { it.name?.toUpperCase() }
                viewAdapter.notifyDataSetChanged()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = MenuInflater(this)
        inflater.inflate(R.menu.main_options, menu)
        searchView.setMenuItem(menu?.findItem(R.id.action_search))
        return true
    }

    override fun onBackPressed() {
        if (searchView.isSearchOpen) {
            searchView.closeSearch()
        } else {
            super.onBackPressed()
        }
    }
}
