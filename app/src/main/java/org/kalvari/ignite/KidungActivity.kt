package org.kalvari.ignite

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.kalvari.ignite.adapter.KidungAdapter
import org.kalvari.ignite.detail.KidungDetailActivity
import org.kalvari.ignite.model.KidungModel
import org.kalvari.ignite.utility.NetworkUtils.isOffline
import org.kalvari.ignite.utility.NetworkUtils.showOfflineDialog
import java.util.Locale

class KidungActivity : AppCompatActivity() {
    private lateinit var back: ImageView
    private lateinit var kidungAdapter: KidungAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private val kidungModels: ArrayList<KidungModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kidung)

        if (isOffline(this)) showOfflineDialog(this)

        initalizeView()
        setupFirebaseRecycler()
        setupSearchView()
    }

    private fun initalizeView() {
        FirebaseApp.initializeApp(this)
        recyclerView    = findViewById(R.id.recyclerView)
        back            = findViewById(R.id.comeback)
        searchView      = findViewById(R.id.search)

        kidungAdapter   = KidungAdapter(this, kidungModels)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = kidungAdapter

        back.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        })
    }

    private fun setupFirebaseRecycler() {
        val firebaseDatabase = FirebaseDatabase.getInstance("https://kalvari-web-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("nytb").orderByChild("key")

        firebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedData = ArrayList<KidungModel>()

                for (dataSnapshot in snapshot.children) {
                    val kidungModel = dataSnapshot.getValue(KidungModel::class.java)
                    if (kidungModel != null) {
                        kidungModel.key = dataSnapshot.key
                        updatedData.add(kidungModel)
                    }
                }

                kidungAdapter.updateData(updatedData)
                kidungAdapter.setOnKidungClickListener { selectedKidung ->
                    val intent = Intent(this@KidungActivity, KidungDetailActivity::class.java)
                    intent.putExtra(KidungDetailActivity.ITEM_EXTRA, selectedKidung)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@KidungActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSearchView() {
        val sredittext  = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        val icon        = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        val close       = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)

        icon.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_IN)
        close.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_IN)
        sredittext.setTextColor(ContextCompat.getColor(this, R.color.black))
        sredittext.setHintTextColor(ContextCompat.getColor(this, R.color.black))
        searchView.clearFocus()
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean = false

            override fun onQueryTextChange(newText: String): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String) {
        val filtered = kidungModels.filter {
            it.title?.lowercase(Locale.getDefault())?.contains(query.lowercase(Locale.getDefault())) == true ||
                    it.no?.lowercase(Locale.getDefault())?.contains(query.lowercase(Locale.getDefault())) == true
        }

        if (filtered.isEmpty()) {
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show()
        } else {
            kidungAdapter.updateData(filtered)
        }
    }
}
