package com.voicemod.videorecorder.view.common

import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpViews()
        observeViewState()
    }

    protected abstract fun initViewModel()
    protected abstract fun setUpViews()
    protected abstract fun observeViewState()
}
