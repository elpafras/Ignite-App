package org.kalvari.ignite.detail

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.kalvari.ignite.R
import org.kalvari.ignite.RenunganActivity
import org.kalvari.ignite.model.RenunganModel
import org.kalvari.ignite.utility.FetchUtils
import org.kalvari.ignite.utility.HtmlUtils

class RenunganDetailActivity : AppCompatActivity() {

    private lateinit var titleDetail: TextView
    private lateinit var contentDetail: TextView
    private lateinit var tglDetail: TextView
    private lateinit var ayatButton: Button
    private lateinit var backButton: ImageView
    private var renunganModel: RenunganModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_renungan_detail)

        initializeViews()
        initializeData()
        setupListener()
        showData()
    }

    private fun initializeViews() {
        titleDetail = findViewById(R.id.title_detail)
        contentDetail = findViewById(R.id.content)
        ayatButton = findViewById(R.id.ayat)
        tglDetail = findViewById(R.id.title)
        backButton = findViewById(R.id.back)
    }

    private fun initializeData() {
        renunganModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(ITEM_EXTRA, RenunganModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(ITEM_EXTRA)
        }
    }

    private fun setupListener() {
        backButton.setOnClickListener {
            startActivity(Intent(this@RenunganDetailActivity, RenunganActivity::class.java))
        }

        ayatButton.setOnClickListener { renunganModel?.ayat?.let { viewAyat(it) } }
    }

    private fun showData() {
        renunganModel?.let { model ->
            tglDetail.text = getString(R.string.renungan_format, model.title)
            titleDetail.text = model.title
            ayatButton.text = getString(R.string.ayat_format, model.ayat)
            contentDetail.text = model.content?.replace("\\\\n".toRegex(), "\n")
        }

        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun viewAyat(ayat: String) {
        val loadingDialog = createLoadingDialog("Memuat ayat...")
        loadingDialog.show()

        FetchUtils.fetchVerseTexts(ayat) { result ->
            loadingDialog.dismiss()

            if (result != null) {
                val message = result.entries.joinToString("\n") { "${it.key} ${it.value}" }
                showDialog(
                    title = "Ayat: $ayat",
                    message = HtmlUtils.removeHtmlTags(message)
                )
            } else {
                showErrorDialog(ayat)
            }
        }
    }

    private fun createLoadingDialog(message: String): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)

        // Create a TextView programmatically
        val loadingTextView = TextView(this).apply {
            text = message
            textSize = 18f
            setTextColor(resources.getColor(android.R.color.black, theme))
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
        AlertDialog.Builder(this)
            .setTitle("Kesalahan")
            .setMessage(R.string.alert_ayat)
            .setPositiveButton("Coba lihat versi Web") { _, _ ->
                val intent = Intent(this, AyatDetailActivity::class.java)
                intent.putExtra("ayat", ayat)
                startActivity(intent)
            }
            .setNegativeButton("Batal", null)
            .create()
            .show()
    }

    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create()
            .show()
    }


    companion object {
        const val ITEM_EXTRA: String = "item_extra"
    }
}