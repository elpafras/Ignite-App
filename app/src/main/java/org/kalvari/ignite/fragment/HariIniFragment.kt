package org.kalvari.ignite.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.kalvari.ignite.DashboardActivity
import org.kalvari.ignite.R
import org.kalvari.ignite.detail.AyatDetailActivity
import org.kalvari.ignite.model.RenunganModel
import org.kalvari.ignite.utility.FetchUtils
import org.kalvari.ignite.utility.HtmlUtils

class HariIniFragment : Fragment() {
    private lateinit var title: TextView
    private lateinit var content: TextView
    private lateinit var ayat: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hari_ini, container, false)

        initializeFirebaseView(view)
        setupFirebaseListener()

        return view
    }

    private fun initializeFirebaseView(view: View) {
        FirebaseApp.initializeApp(requireContext())
        title = view.findViewById(R.id.title)
        content = view.findViewById(R.id.content_today)
        ayat = view.findViewById(R.id.ayat_today)
    }

    private fun setupFirebaseListener() {
        val firebaseDatabase =
            FirebaseDatabase.getInstance("https://kalvari-web-default-rtdb.asia-southeast1.firebasedatabase.app")

        firebaseDatabase.reference.child("notes").orderByChild("key").limitToLast(1)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.children.count() == 0) {
                        displayEmptyState()
                        return
                    }

                    for (dataSnapshot in snapshot.children) {
                        val model = dataSnapshot.getValue(RenunganModel::class.java)
                        if (model != null) {
                            displayData(model)
                        } else {
                            displayEmptyState()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "onCancelled called with error: " + error.message)
                }
            })
    }

    private fun displayData(model: RenunganModel) {
        title.text = model.title
        content.text = model.content?.replace("\\\\n".toRegex(), "\n")
        ayat.apply {
            setText(R.string.ayat)
            setOnClickListener { showVerseInDialog(model.ayat) }
            visibility = View.VISIBLE
        }
        title.visibility = View.VISIBLE
        content.visibility = View.VISIBLE
    }

    private fun displayEmptyState() {
        title.visibility = View.GONE
        content.visibility = View.GONE
        ayat.visibility = View.GONE
        startActivity(Intent(requireContext(), DashboardActivity::class.java))
    }

    private fun showVerseInDialog(ayat: String?) {
        if (ayat == null){
            showErrorDialog(ayat)
            return
        }

        val loadingDialog = createLoadingDialog("Memuat ayat...")
        loadingDialog.show()

        FetchUtils.fetchVerseTexts(ayat) { result ->
            loadingDialog.dismiss() // Tutup dialog loading

            Log.d("cek ayat", "showVerseInDialog: $ayat")
            Log.d("cek Result", "showVerseInDialog: $result")

            if (result != null) {
                // Data berhasil diambil, tampilkan dalam dialog

                val message = result.entries.joinToString("\n") { "${it.key} ${it.value}" }
                AlertDialog.Builder(requireContext())
                    .setTitle("Ayat: $ayat")
                    .setMessage(HtmlUtils.removeHtmlTags(message))
                    .setPositiveButton("OK", null)
                    .create()
                    .show()
            } else {
                // Gagal mengambil data
                showErrorDialog(ayat)
            }
        }
    }

    private fun createLoadingDialog(message: String): AlertDialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)

        // Create a TextView programmatically
        val loadingTextView = TextView(requireContext()).apply {
            text = message
            textSize = 18f
            setTextColor(resources.getColor(android.R.color.black, null))
            setPadding(16, 16, 16, 16)
            gravity = android.view.Gravity.CENTER
        }

        builder.setView(loadingTextView)

        val dialog = builder.create()

        // Animation logic
        val handler = Handler()
        val dots = arrayOf("", ".", "..", "...")
        var index = 0

        val runnable = object : Runnable {
            override fun run() {
                loadingTextView.text = "Memuat Ayat" + dots[index]
                index = (index + 1) % dots.size
                handler.postDelayed(this, 500) // Update every 500ms
            }
        }

        dialog.setOnShowListener {
            handler.post(runnable)
        }

        dialog.setOnDismissListener {
            handler.removeCallbacks(runnable)
        }

        return dialog
    }

    private fun showErrorDialog(ayat: String?) {
        AlertDialog.Builder(requireContext())
            .setTitle("Kesalahan")
            .setMessage(R.string.alert_ayat)
            .setPositiveButton("Coba lihat versi Web") { _, _ ->
                val intent = Intent(requireContext(), AyatDetailActivity::class.java)
                intent.putExtra("ayat", ayat)
                startActivity(intent)
            }
            .setNegativeButton("Batal", null)
            .create()
            .show()
    }
}
