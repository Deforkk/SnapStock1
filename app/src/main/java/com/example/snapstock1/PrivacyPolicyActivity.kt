package com.example.snapstock1

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PrivacyPolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.privacy_policy_layout)

        val privacyPolicyTextView = findViewById<TextView>(R.id.privacyPolicyTextView)
        val privacyPolicyText = getString(R.string.privacy_policy_text)

        privacyPolicyTextView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(privacyPolicyText, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(privacyPolicyText)
        }
    }
}
