package com.example.snapstock1

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FaqActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.faq_layout)

        val faqTextView = findViewById<TextView>(R.id.faqTextView)
        val faqText = getString(R.string.faq_text)

        faqTextView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(faqText, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(faqText)
        }
    }
}
