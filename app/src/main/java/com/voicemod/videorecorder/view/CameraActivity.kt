package com.voicemod.videorecorder.view

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.voicemod.videorecorder.R
import com.voicemod.videorecorder.view.adapter.SectionsPagerAdapter
import com.voicemod.videorecorder.view.fragments.VideoLibraryFragment
import com.voicemod.videorecorder.view.fragments.VideoRecorderFragment
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.tab_layout_menu.view.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class CameraActivity : AppCompatActivity() {
    private val mHideHandler = Handler()
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreen_content_controls.visibility = View.VISIBLE
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_camera)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        mVisible = true

        setupViews()
    }

    private fun setupViews() {
        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.viewpager)
        var mSectionsPagerAdapter: SectionsPagerAdapter? = SectionsPagerAdapter(
            supportFragmentManager, listOf(
                VideoLibraryFragment.newInstance(),
                VideoRecorderFragment.newInstance()
            )
        )
        viewPager.adapter = mSectionsPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
                when (tab.position) {
                    0 -> {
                        val fragment = mSectionsPagerAdapter?.getItem(tab.position) as VideoLibraryFragment
                        fragment.viewModel.getViewStatus()
                    }
                    1 -> {
                        val fragment = mSectionsPagerAdapter?.getItem(tab.position) as VideoRecorderFragment
                        fragment.viewModel.getViewStatus()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        setupTabIcons()
    }

    private fun setupTabIcons() {
        val libraryView = LayoutInflater.from(this).inflate(R.layout.tab_layout_menu, null) as ImageView
        libraryView.mTabItem.setImageResource(R.drawable.ic_video_library)
        tabLayout.getTabAt(0)?.customView = libraryView

        val cameraView = LayoutInflater.from(this).inflate(R.layout.tab_layout_menu, null) as ImageView
        cameraView.setImageDrawable(getDrawable(R.drawable.ic_video_recorder))
        tabLayout.getTabAt(1)?.customView = cameraView
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            showToolbar()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        fullscreen_content_controls.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
    }

    fun showToolbar() {
        mVisible = true

        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        val UI_ANIMATION_DELAY = 300
    }
}
