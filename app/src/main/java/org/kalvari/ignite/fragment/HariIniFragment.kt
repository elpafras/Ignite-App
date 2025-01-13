package org.kalvari.ignite.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import org.kalvari.ignite.R
import org.kalvari.ignite.detail.AyatDetailActivity
import org.kalvari.ignite.model.RenunganModel
import org.kalvari.ignite.model.SharedViewModel
import org.kalvari.ignite.utility.FetchUtils
import org.kalvari.ignite.utility.HtmlUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HariIniFragment : Fragment() {
    private lateinit var title: TextView
    private lateinit var content: TextView
    private lateinit var ayat: Button
    private lateinit var prevButton: ImageView
    private lateinit var nextButton: ImageView
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val firebaseDatabase by lazy {
        FirebaseDatabase.getInstance("https://kalvari-web-default-rtdb.asia-southeast1.firebasedatabase.app")
    }

    private var currentDate: String? = null
    private var currentKey: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_hari_ini, container, false)

        initializeFirebaseView(view)
        setupFirebaseListener()
        observeSharedViewModels()

        return view
    }

    private fun initializeFirebaseView(view: View) {
        FirebaseApp.initializeApp(requireContext())
        title       = view.findViewById(R.id.title)
        content     = view.findViewById(R.id.content_today)
        ayat        = view.findViewById(R.id.ayat_today)
        prevButton  = view.findViewById(R.id.prevButton)
        nextButton  = view.findViewById(R.id.nextButton)
    }

    private fun setupFirebaseListener() {
        fetchDataFromFirebase(
            query = firebaseDatabase.reference.child("notes").orderByChild("key").limitToLast(1),
            onSuccess = { model ->
                displayData(model)
                sharedViewModel.setRenungan(model)
                currentDate = model.title
                currentKey = model.key
                updateNavigationButtons()
            },
            onEmpty = {
                displayEmptyState()
                sharedViewModel.setRenungan(null)
            }
        )
    }

    private fun fetchDataByDate(selectedDate: String) {
        fetchDataFromFirebase(
            query = firebaseDatabase.reference.child("notes").orderByChild("title").equalTo(selectedDate),
            onSuccess = { model ->
                displayData(model)
                sharedViewModel.setRenungan(model)
                currentDate = model.title
                currentKey = model.key
                updateNavigationButtons()
            },
            onEmpty = {
                displayEmptyState()
                sharedViewModel.setRenungan(null)
            }
        )
    }

    private fun fetchDataFromFirebase(query: Query, onSuccess: (RenunganModel) -> Unit, onEmpty: () -> Unit ) {
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    onEmpty()
                    return
                }

                var found = false
                for (dataSnapshot in snapshot.children) {
                    val model = dataSnapshot.getValue(RenunganModel::class.java)
                    if (model != null) {
                        onSuccess(model)
                        found = true
                        break
                    }
                }

                if (!found) onEmpty()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "onCancelled called with error: " + error.message)
                AlertDialog.Builder(requireContext())
                    .setTitle("Kesalahan")
                    .setMessage("Gagal mengambil data dari server: ${error.message}")
                    .setPositiveButton("Coba Lagi") { _, _ -> setupFirebaseListener() }
                    .setNegativeButton("Tutup", null)
                    .show()
            }
        })
    }


    private fun displayData(model: RenunganModel) {
        title.apply {
            text = model.title
            setOnClickListener {
                showDatePicker()
            }
        }

        ayat.apply {
            text = getString(R.string.ayat_format, model.ayat)
            setOnClickListener { showVerseInDialog(model.ayat) }
            visibility = View.VISIBLE
        }

        content.text = model.content?.replace("\\\\n".toRegex(), "\n")

        title.visibility = View.VISIBLE
        content.visibility = View.VISIBLE
    }

    private fun displayEmptyState() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Data Tidak Tersedia")
            .setMessage("Renungan untuk tanggal hari ini atau yang ditentukan tidak tersedia.\n\nSilakan coba dengan tanggal lain.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
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
        val handler = Handler(Looper.getMainLooper())
        val dots = arrayOf("", ".", "..", "...")
        var index = 0

        val runnable = object : Runnable {
            override fun run() {
                loadingTextView.text = String.format("Memuat Ayat %s", dots[index])
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

    fun shareContent() {
        val titleText = title.text.toString()
        val ayatText = ayat.text.toString()
        val contentText = content.text.toString()

        val shareText = "Pembacaan Alkitab GKIN KALVARI\n$titleText\n$ayatText\n\n$contentText".trimIndent()

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        startActivity(Intent.createChooser(shareIntent, "Bagikan melalui"))
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        context?.let {
            DatePickerDialog(it, {_, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDayOfMonth)
                }

                val dateFormatDisplay = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
                val formattedDateDisplay = dateFormatDisplay.format(selectedDate.time)

                title.text = formattedDateDisplay
                Log.d("tfragment", "showDatePicker: $formattedDateDisplay")

                sharedViewModel.setSelectedDate(formattedDateDisplay)
                fetchDataByDate(formattedDateDisplay)
            }, year, month, dayOfMonth).show()
        }
    }

    private fun observeSharedViewModels() {
        sharedViewModel.selectedDate.observe(viewLifecycleOwner) { selectedDate ->
            fetchDataByDate(selectedDate)
        }

        sharedViewModel.renungan.observe(viewLifecycleOwner) { renungan ->
            if (renungan != null) {
                displayData(renungan) // Tampilkan data yang diperbarui
            } else {
                displayEmptyState() // Tampilkan layar kosong jika tidak ada data
            }
        }
    }


    private fun updateNavigationButtons() {
        prevButton.isEnabled = currentDate != null
        nextButton.isEnabled = currentDate != null

        prevButton.setOnClickListener {
            fetchPreviousData()
        }

        nextButton.setOnClickListener {
            fetchNextData()
        }
    }

    private fun fetchPreviousData() {
        if (currentDate != null) {
            val previousDate = getPreviousDate(currentDate!!)
            fetchDataByDate(previousDate)
        }
    }

    private fun fetchNextData() {
        if (currentDate != null) {
            val nextDate = getNextDate(currentDate!!)
            fetchDataByDate(nextDate)
        }
    }

    private fun getPreviousDate(currentDate: String): String {
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
        val calendar = Calendar.getInstance()
        val parsedDate = dateFormat.parse(currentDate)

        // Ensure the parsed date is not null
        if (parsedDate != null) {
            calendar.time = parsedDate
            calendar.add(Calendar.DATE, -1) // Mengurangi satu hari untuk mendapatkan tanggal sebelumnya
            return dateFormat.format(calendar.time)
        } else {
            // Handle parsing failure (e.g., log an error or show a message)
            Log.e("DateParsingError", "Failed to parse date: $currentDate")
            return currentDate // Return the original date or handle the error appropriately
        }
    }

    private fun getNextDate(currentDate: String): String {
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
        val calendar = Calendar.getInstance()
        val parsedDate = dateFormat.parse(currentDate)

        // Ensure the parsed date is not null
        if (parsedDate != null) {
            calendar.time = parsedDate
            calendar.add(Calendar.DATE, 1) // Menambah satu hari untuk mendapatkan tanggal setelahnya
            return dateFormat.format(calendar.time)
        } else {
            // Handle parsing failure (e.g., log an error or show a message)
            Log.e("DateParsingError", "Failed to parse date: $currentDate")
            return currentDate // Return the original date or handle the error appropriately
        }
    }
}
