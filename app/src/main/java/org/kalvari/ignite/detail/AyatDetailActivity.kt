package org.kalvari.ignite.detail

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import org.kalvari.ignite.R

class AyatDetailActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var backButton: ImageView
    private val ayatKey = "ayat"

    private val kitabMap = mapOf(
        "Kejadian" to "Kej", "Keluaran" to "Kel", "Imamat" to "Ima", "Bilangan" to "Bil",
        "Ulangan" to "Ula", "Yosua" to "Yos", "Hakim-Hakim" to "Hak", "Rut" to "Rut",
        "1 Samuel" to "1Sa", "2 Samuel" to "2Sa", "1 Raja-Raja" to "1Ra", "2 Raja-Raja" to "2Ra",
        "1 Tawarikh" to "1Ta", "2 Tawarikh" to "2Ta", "Ezra" to "Ezr", "Nehemia" to "Neh",
        "Ester" to "Est", "Ayub" to "Ayb", "Mazmur" to "Mzm", "Amsal" to "Ams",
        "Pengkhotbah" to "Pkh", "Kidung Agung" to "Kid", "Yesaya" to "Yes", "Yeremia" to "Yer",
        "Ratapan" to "Rat", "Yehezkiel" to "Yeh", "Daniel" to "Dan", "Hosea" to "Hos",
        "Yoel" to "Yoe", "Amos" to "Amo", "Obaja" to "Oba", "Yunus" to "Yun",
        "Mikha" to "Mik", "Nahum" to "Nah", "Habakuk" to "Hab", "Zefanya" to "Zef",
        "Hagai" to "Hag", "Zakharia" to "Zak", "Maleakhi" to "Mal", "Matius" to "Mat",
        "Markus" to "Mrk", "Lukas" to "Luk", "Yohanes" to "Yoh", "Kisah Para Rasul" to "Kis",
        "Roma" to "Rom", "1 Korintus" to "1Ko", "2 Korintus" to "2Ko", "Galatia" to "Gal",
        "Efesus" to "Efe", "Filipi" to "Flp", "Kolose" to "Kol", "1 Tesalonika" to "1Te",
        "2 Tesalonika" to "2Te", "1 Timotius" to "1Ti", "2 Timotius" to "2Ti", "Titus" to "Tit",
        "Filemon" to "Flm", "Ibrani" to "Ibr", "Yakobus" to "Yak", "1 Petrus" to "1Pt",
        "2 Petrus" to "2Pt", "1 Yohanes" to "1Yo", "2 Yohanes" to "2Yo", "3 Yohanes" to "3Yo",
        "Yudas" to "Yud", "Wahyu" to "Why"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ayat_detail)

        webView = findViewById(R.id.webView)
        webView.webViewClient = WebViewClient()
        backButton = findViewById(R.id.back)

        backButton.setOnClickListener {
            finish()
        }

        intent.getStringExtra(ayatKey)?.let { ayat ->
            val modifiedAyat = prepareAyat(ayat)
            loadAyatInWebView(modifiedAyat)
        }
    }

    private fun prepareAyat(ayat: String): String {
        val withSingkatan = replaceKitabWithSingkatan(ayat)
        return replaceCharacters(withSingkatan)
    }

    private fun replaceKitabWithSingkatan(ayat: String): String {
        val pattern = kitabMap.keys.joinToString("|") { Regex.escape(it) }.toRegex()
        return pattern.replace(ayat) { matchResult ->
            kitabMap[matchResult.value] ?: matchResult.value
        }
    }

    private fun replaceCharacters(ayat: String): String {
        return ayat.replace(" ", "+").replace(",", ";")
    }

    private fun loadAyatInWebView(ayat: String) {
        val url = "https://alkitab.mobi/tb/passage/$ayat"
        webView.loadUrl(url)
    }
}