package com.example.androidtrivial

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OpcionsActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var gestureDetector: GestureDetectorCompat   // usa la versión compat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opcions)

        viewPager = findViewById(R.id.viewPagerOptions)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayoutOptions)

        viewPager.adapter = OptionsPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            tab.text = if (pos == 0) "Sonido" else "Efectos"
        }.attach()

        // --- GestureDetector que decide pestaña según la mitad de pantalla ------------------
        gestureDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                val half = resources.displayMetrics.widthPixels / 2
                if (e.x > half) {
                    // derecha → siguiente pestaña
                    val last = (viewPager.adapter?.itemCount ?: 1) - 1
                    if (viewPager.currentItem < last) viewPager.currentItem += 1
                } else {
                    // izquierda → anterior
                    if (viewPager.currentItem > 0) viewPager.currentItem -= 1
                }
                return true
            }
        })
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }
}
