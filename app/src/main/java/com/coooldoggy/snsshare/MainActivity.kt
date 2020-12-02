package com.coooldoggy.snsshare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.coooldoggy.snsshare.common.PLATFORM_ALL_AVAILABLE
import com.coooldoggy.snsshare.common.PLATFORM_KAKAO
import com.coooldoggy.snsshare.common.PLATFORM_KEY
import com.coooldoggy.snsshare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        setView()
    }

    private fun setView() {
        mainBinding.ivKakao.setOnClickListener {
            Intent(this@MainActivity, ShareActivity::class.java).apply {
                putExtra(
                    PLATFORM_KEY,
                    PLATFORM_KAKAO
                )
            }.runCatching {
                startActivity(this)
            }
        }
        mainBinding.ivAll.setOnClickListener {
            Intent(this@MainActivity, ShareActivity::class.java).apply {
                putExtra(
                    PLATFORM_KEY,
                    PLATFORM_ALL_AVAILABLE
                )
            }.runCatching {
                startActivity(this)
            }
        }
    }
}