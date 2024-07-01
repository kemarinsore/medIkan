package com.tok.medikan.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.tok.medikan.databinding.ActivityDetailHistoryBinding

class DetailHistoryActivity : AppCompatActivity() {

    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }

    private var _binding: ActivityDetailHistoryBinding? = null
    val binding get() = _binding!!
    private var treatmentString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        _binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            bar.btnBack.setOnClickListener {
                onBackPressedCallback.handleOnBackPressed()
            }

            Glide.with(this@DetailHistoryActivity).load(getDataIntent("image")).centerCrop().into(imgDetail)

            if (getDataIntent("treatment").length > 0) {
                val treatment = getDataIntent("treatment").split(",")
                treatment.forEachIndexed { index, element ->
                    if (index == 0) {
                        treatmentString = treatmentString + "\n\n \u2022 ${element}"
                    } else {
                        treatmentString = treatmentString + "\n \u2022 ${element}"
                    }
                }
            } else {
                treatmentString = getDataIntent("treatment")
            }

            txtHistory.text = getDataIntent("historyName")
            txtHistoryCreated.text = getDataIntent("createdAt")
            txtDisease.text = getDataIntent("name")
            txtDescription.text = getDataIntent("description") + treatmentString
        }
    }

    fun getDataIntent(string: String): String {
        return intent.getStringExtra(string).toString()
    }
}