package com.voicemod.videorecorder.view.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.voicemod.videorecorder.view.adapter.view_holders.EmptyViewHolder
import com.voicemod.videorecorder.view.adapter.view_holders.VideoViewHolder
import com.voicemod.videorecorder.view.common.extension.convertDpToPixel
import com.voicemod.videorecorder.R

class VMLibraryAdapter(private val items: List<LibraryItem>,
                       private val onClickListener: (String) -> Unit,
                       private val onDeleteClickListener: (LibraryItem) -> Unit,
                       private val emptyView: Int = R.layout.library_empty_item,
                       private val videoView: Int = R.layout.library_video_item
) : RecyclerView.Adapter<VMLibraryAdapter.ViewHolder>() {

    private lateinit var context: Context

    enum class ItemTypes { EMPTY, VIDEO, PICTURE }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ItemTypes.EMPTY.ordinal -> EmptyViewHolder(LayoutInflater.from(context).inflate(emptyView, parent, false))
            ItemTypes.VIDEO.ordinal -> VideoViewHolder(LayoutInflater.from(context).inflate(videoView, parent, false), onClickListener, onDeleteClickListener)
            ItemTypes.PICTURE.ordinal -> EmptyViewHolder(LayoutInflater.from(context).inflate(emptyView, parent, false))
            else -> EmptyViewHolder(LayoutInflater.from(context).inflate(emptyView, parent, false))
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is LibraryItem.Empty -> ItemTypes.EMPTY.ordinal
            is LibraryItem.Video -> ItemTypes.VIDEO.ordinal
            is LibraryItem.Picture -> ItemTypes.PICTURE.ordinal
        }
    }

    open class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val defaultPaddingTop = itemView.paddingTop
        val defaultPaddingBottom = itemView.paddingBottom
        val defaultPaddingStart = itemView.paddingStart
        val defaultPaddingEnd = itemView.paddingEnd

        open fun bind(detailsItem: LibraryItem) {
            itemView.setPadding(detailsItem.paddingStart.toPixel() ?: defaultPaddingStart,
                    detailsItem.paddingTop.toPixel() ?: defaultPaddingTop,
                    detailsItem.paddingEnd.toPixel() ?: defaultPaddingEnd,
                    detailsItem.paddingBottom.toPixel() ?: defaultPaddingBottom)
            /* override with view binding implementation in child class */
        }
        fun Float?.toPixel(): Int? {
            return if(this == null) this else this.convertDpToPixel()
        }
    }
}

sealed class LibraryItem(var paddingStart: Float? = null, var paddingTop: Float? = null, var paddingEnd: Float? = null, var paddingBottom: Float? = null) {
    data class Empty(val title: String? = null) : LibraryItem()
    data class Video(val uuid: String? = null, val title: String? = null, val path: String? = null, val thumbnail: Bitmap? = null) : LibraryItem()
    data class Picture(val title: String? = null) : LibraryItem()

    fun setPaddings(paddingStart: Float? = null, paddingTop: Float? = null, paddingEnd: Float? = null, paddingBottom: Float? = null) : LibraryItem {
        this.paddingStart = paddingStart
        this.paddingTop = paddingTop
        this.paddingEnd = paddingEnd
        this.paddingBottom = paddingBottom
        return this
    }
}
