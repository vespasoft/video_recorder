package com.voicemod.videorecorder.view.adapter.view_holders

import android.view.View
import com.voicemod.videorecorder.view.adapter.LibraryItem
import com.voicemod.videorecorder.view.adapter.VMLibraryAdapter

class EmptyViewHolder(view: View) : VMLibraryAdapter.ViewHolder(view) {

    override fun bind(item: LibraryItem) {
        super.bind(item)
        item as LibraryItem.Empty
    }
}