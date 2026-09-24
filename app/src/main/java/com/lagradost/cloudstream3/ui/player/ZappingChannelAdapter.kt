package com.lagradost.cloudstream3.ui.player

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.lagradost.cloudstream3.R
import com.lagradost.cloudstream3.utils.UIHelper.colorFromAttribute
import com.lagradost.cloudstream3.utils.UIHelper.toPx

/** A small, focusable channel list shared by touch devices and TV remotes. */
class ZappingChannelAdapter(
    private val onChannelClick: (Int) -> Unit,
) : RecyclerView.Adapter<ZappingChannelAdapter.ChannelViewHolder>() {
    private var channels: List<ZappingChannel> = emptyList()
    private var selectedIndex: Int = RecyclerView.NO_POSITION

    fun submit(channels: List<ZappingChannel>, selectedIndex: Int) {
        this.channels = channels
        this.selectedIndex = selectedIndex
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val text = TextView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                56.toPx
            )
            gravity = Gravity.CENTER_VERTICAL
            setPadding(16.toPx)
            textSize = 16f
            isFocusable = true
            isClickable = true
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
            setTextColor(parent.context.colorFromAttribute(R.attr.textColor))
        }
        return ChannelViewHolder(text)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channel = channels[position]
        holder.text.text = channel.name
        holder.text.setTextColor(
            if (position == selectedIndex) holder.text.context.colorFromAttribute(R.attr.colorPrimary)
            else holder.text.context.colorFromAttribute(R.attr.textColor)
        )
        holder.text.setBackgroundColor(
            if (position == selectedIndex) holder.text.context.colorFromAttribute(R.attr.primaryBlackBackground)
            else Color.TRANSPARENT
        )
        holder.text.contentDescription = channel.name
        holder.text.setOnClickListener { onChannelClick(position) }
        holder.text.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && position != selectedIndex) {
                view.setBackgroundColor(holder.text.context.colorFromAttribute(R.attr.iconGrayBackground))
            } else if (position != selectedIndex) {
                view.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    override fun getItemCount(): Int = channels.size

    class ChannelViewHolder(val text: TextView) : RecyclerView.ViewHolder(text)
}
