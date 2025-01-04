package org.kalvari.ignite

import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess

class DashboardActivity : AppCompatActivity() {
    private lateinit var renungan: Button
    private lateinit var nytb: Button
    private lateinit var keluar: Button
    private lateinit var option: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        initializeView()
        setButtonListener()
    }

    private fun initializeView() {
        renungan = findViewById(R.id.buttonRenungan)
        nytb = findViewById(R.id.buttonNytb)
        keluar = findViewById(R.id.buttonKeluar)
        option = findViewById(R.id.option)
    }

    private fun setButtonListener() {
        renungan.setOnClickListener {navigateTo(RenunganActivity::class.java)}
        nytb.setOnClickListener {navigateTo(KidungActivity::class.java)}
        keluar.setOnClickListener { exitApp() }
        option.setOnClickListener { view: View -> this.showPopUpMenu(view) }
    }

    private fun navigateTo(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
    }

    private fun exitApp() {
        moveTaskToBack(true)
        Process.killProcess(Process.myPid())
        exitProcess(1)
    }

    private fun showPopUpMenu(view: View) {
        PopupMenu(this, view).apply {
            menuInflater.inflate(R.menu.option_menu, menu)
            setOnMenuItemClickListener { handleMenuItemClick(it) }
            show()
        }
    }

    private fun handleMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.tentang_aplikasi -> {
                navigateTo(TentangAplikasiActivity::class.java)
                true
            }
            else -> false
        }
    }
}