package org.kalvari.ignite.fragment

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.kalvari.ignite.R
import org.kalvari.ignite.adapter.RenunganAdapter
import org.kalvari.ignite.detail.RenunganDetailActivity
import org.kalvari.ignite.model.RenunganModel

class SemuaRenunganFragment : Fragment() {
    private lateinit var renunganModels: ArrayList<RenunganModel>
    private lateinit var renunganAdapter: RenunganAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_semua_renungan, container, false)

        initializeViews(view)
        setupRecyclerView()
        fetchFirebaseData()
        setupSearchView()

        return view
    }

    private fun initializeViews(view: View) {
        FirebaseApp.initializeApp(requireContext())
        recyclerView = view.findViewById(R.id.recyclerView)
        searchView = view.findViewById(R.id.search)
        renunganModels = ArrayList()
        renunganAdapter = RenunganAdapter(requireContext(), renunganModels)
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
            itemAnimator = DefaultItemAnimator()
            adapter = renunganAdapter
        }

        renunganAdapter.setOnItemClickListener { model ->
            model?.let { openDetailActivity(it) }
        }
    }

    private fun fetchFirebaseData() {
        val databaseReference = FirebaseDatabase.getInstance(
            "https://kalvari-web-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).reference.child("notes").orderByChild("key")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                renunganModels.clear()
                snapshot.children.mapNotNullTo(renunganModels) { dataSnapshot ->
                    dataSnapshot.getValue(RenunganModel::class.java)?.apply {
                        key = dataSnapshot.key
                    }
                }
                renunganAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSearchView() {
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        val closeIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)

        // Style customization
        val color = ContextCompat.getColor(requireContext(), R.color.black)
        searchIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        closeIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        searchEditText.setTextColor(color)
        searchEditText.setHintTextColor(color)
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        // Search query listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean = false

            override fun onQueryTextChange(newText: String): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String) {
        val filteredList = renunganModels.filter {
            it.title?.contains(query, ignoreCase = true) == true ||
                    it.content?.contains(query, ignoreCase = true) == true
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.no_data), Toast.LENGTH_SHORT).show()
        } else {
            renunganAdapter.filter.filter(query)
        }
    }

    private fun openDetailActivity(model: RenunganModel) {
        val intent = Intent(context, RenunganDetailActivity::class.java).apply {
            putExtra(RenunganDetailActivity.ITEM_EXTRA, model)
        }
        startActivity(intent)
    }
}