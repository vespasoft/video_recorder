package com.voicemod.videorecorder.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter : FragmentPagerAdapter {
    private var fragments: List<Fragment> = emptyList()

    constructor(fm: FragmentManager) : super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {}

    constructor(fm: FragmentManager, fragments: List<Fragment>) : super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        this.fragments = fragments
    }

    override fun getItem(position: Int): Fragment {
        return this.fragments[position]
    }

    override fun getCount(): Int {
        // Show 3 total pages.
        return this.fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "LIBRARY"
            1 -> return "VIDEO RECORDER"
            2 -> return "CAMERA"
        }
        return null
    }
}