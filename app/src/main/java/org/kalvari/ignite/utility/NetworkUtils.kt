package org.kalvari.ignite.utility

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import org.kalvari.ignite.DashboardActivity
import org.kalvari.ignite.R

object NetworkUtils {
    @JvmStatic
    fun showOfflineDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.alert_title)
        builder.setMessage(R.string.alert_message)
        builder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            val intent = Intent(context, DashboardActivity::class.java)
            context.startActivity(intent)
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun isOffline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        return if (networkCapabilities != null) {
            !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ||
                    !(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
        } else {
            true
        }
    }
}
