package org.kalvari.ignite

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class TentangAplikasiActivity : AppCompatActivity() {
    private var back: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tentang_aplikasi)

        back = findViewById(R.id.kembalipulang)
        back?.setOnClickListener {
            finish()
        }
    }
}