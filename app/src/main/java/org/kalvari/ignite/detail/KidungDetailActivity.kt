package org.kalvari.ignite.detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.kalvari.ignite.KidungActivity
import org.kalvari.ignite.R
import org.kalvari.ignite.model.KidungModel

class KidungDetailActivity : AppCompatActivity() {

    private lateinit var titleDetail: TextView
    private lateinit var nadaDetail: TextView
    private lateinit var baitDetail: TextView
    private lateinit var koorDetail: TextView
    private lateinit var noSong: TextView
    private lateinit var leotk: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kidung_detail)

        initViews()
        setupBackButton()
        displayContent()
    }

    private fun initViews() {
        titleDetail = findViewById(R.id.title_detail)
        nadaDetail  = findViewById(R.id.nada_detail)
        baitDetail  = findViewById(R.id.bait_detail)
        koorDetail  = findViewById(R.id.koor_detail)
        noSong      = findViewById(R.id.nolagu)
        leotk       = findViewById(R.id.leotk)

        val scrollView = findViewById<ScrollView>(R.id.scrollviuu)
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }

    private fun setupBackButton() {
        findViewById<ImageView>(R.id.back).setOnClickListener {
            navigateToNamaYesus()
        }
    }

    private fun displayContent() {
        val kidungModel: KidungModel? = getParcelableExtraCompat(ITEM_EXTRA)

        kidungModel?.let {
            noSong.text = it.no
            titleDetail.text = it.title
            nadaDetail.text = it.nada
            baitDetail.text = it.bait
            koorDetail.text = it.koor

            if (it.koor.isNullOrEmpty()) {
                koorDetail.visibility = View.GONE
                leotk.visibility = View.GONE
            }
        }
    }

    private fun navigateToNamaYesus() {
        startActivity(Intent(this@KidungDetailActivity, KidungActivity::class.java))
    }

    private inline fun <reified T> getParcelableExtraCompat(key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(key)
        }
    }

    companion object {
        const val ITEM_EXTRA: String = "item_extra"
    }
}