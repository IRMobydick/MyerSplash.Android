package com.juniperphoton.myersplash.activity

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "BaseActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
            val enter = resources.getIdentifier("android:activity_open_enter", "anim", null)
            val exit = resources.getIdentifier("android:activity_open_exit", "anim", null)
            if (enter != 0 && exit != 0) {
                overridePendingTransition(enter, exit)
            }
        }
    }

    override fun finish() {
        super.finish()
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
            val enter = resources.getIdentifier("android:activity_close_enter", "anim", null)
            val exit = resources.getIdentifier("android:activity_close_exit", "anim", null)
            if (enter != 0 && exit != 0) {
                overridePendingTransition(enter, exit)
            }
        }
    }
}
