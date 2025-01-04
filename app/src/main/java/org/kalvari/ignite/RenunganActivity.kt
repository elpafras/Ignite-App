package org.kalvari.ignite

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.kalvari.ignite.fragment.HariIniFragment
import org.kalvari.ignite.fragment.SemuaRenunganFragment
import org.kalvari.ignite.utility.NetworkUtils.isOffline
import org.kalvari.ignite.utility.NetworkUtils.showOfflineDialog

class RenunganActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var titleTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_renungan)

        if (isOffline(this)) showOfflineDialog(this)

        initializeViews()
        setupBottomNavigation()
        loadFragment(HariIniFragment())
    }

    private fun initializeViews() {
        titleTextView = findViewById(R.id.title)
        backButton = findViewById(R.id.back)
        backButton.setOnClickListener {
            navigateToDashboard()
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.apply {
            setOnItemSelectedListener { handleNavigationItemSelected(it) }
            itemIconTintList            = ColorStateList.valueOf(Color.WHITE)
            itemTextColor               = ColorStateList.valueOf(Color.WHITE)
            itemActiveIndicatorColor    = ColorStateList.valueOf(Color.parseColor("#80BDBCBC"))
        }
    }

    private fun handleNavigationItemSelected(item: MenuItem): Boolean {
        val fragment = when (item.itemId) {
            R.id.hariIni -> HariIniFragment().also { titleTextView.setText(R.string.renungan_hari_ini) }
            R.id.semuaRenungan -> SemuaRenunganFragment().also { titleTextView.setText(R.string.semua_renungan) }
            else -> null
        }
        return loadFragment(fragment)
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        fragment?.let {
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, it).commit()
            return true
        }
        return false
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }
}