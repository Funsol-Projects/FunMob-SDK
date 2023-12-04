package com.funsol.funmob_sdk.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.funsol.funmob_sdk.R
import com.funsol.funmob_sdk.databinding.ActivityAppOpenBinding

class AppOpenActivity : AppCompatActivity() {
    private val binding: ActivityAppOpenBinding by lazy { ActivityAppOpenBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}