package com.lagradost.cloudstream3.ui.player

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lagradost.cloudstream3.R
import com.lagradost.cloudstream3.utils.ImageLoader.loadImage
import com.lagradost.cloudstream3.utils.UIHelper.colorFromAttribute
import com.lagradost.cloudstream3.utils.UIHelper.toPx
import kotlin.math.roundToInt

/** A focusable 16:9 channel-card list shared by touch devices and TV remotes. */
class ZappingChannelAdapter(
    private val onChannelClick: (Int) -> Unit,
) : RecyclerView.Adapter<ZappingChannelAdapter.ChannelViewHolder>() {
    private var channels: List<ZappingChannel> = emptyList()
    private var selectedIndex: Int = RecyclerView.NO_POSITION

    fun submit(channels: List<ZappingChannel>, selectedIndex: Int) {
        this.channels = channels
        this.selectedIndex = selectedIndex.coerceIn(0, (channels.size - 1).coerceAtLeast(0))
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val context = parent.context
        val card = AspectRatioCardView(context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                topMargin = 8.toPx
                bottomMargin = 8.toPx
            }
            radius = 12.toPx.toFloat()
            cardElevation = 2.toPx.toFloat()
            useCompatPadding = true
            isFocusable = true
            isFocusableInTouchMode = true
            isClickable = true
            contentDescription = context.getString(R.string.player_channel_list)
        }
        val content = FrameLayout(context)
        val poster = ImageView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            setBackgroundColor(context.colorFromAttribute(R.attr.boxItemBackground))
        }
        val scrim = View(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                72.toPx,
                Gravity.BOTTOM,
            )
            background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(Color.TRANSPARENT, 0xE6000000.toInt()),
            )
        }
        val title = TextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM,
            ).apply {
                marginStart = 16.toPx
                marginEnd = 16.toPx
                bottomMargin = 12.toPx
            }
            textSize = 16f
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
            setTextColor(context.colorFromAttribute(R.attr.textColor))
        }
        val active = TextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.TOP or Gravity.START,
            ).apply {
                topMargin = 12.toPx
                marginStart = 12.toPx
            }
            text = "●"
            textSize = 13f
            setTextColor(context.colorFromAttribute(R.attr.colorPrimary))
            visibility = View.GONE
        }
        content.addView(poster)
        content.addView(scrim)
        content.addView(title)
        content.addView(active)
        card.addView(content)
        return ChannelViewHolder(card, poster, title, active)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channel = channels[position]
        val isActive = position == selectedIndex
        holder.poster.loadImage(channel.posterUrl)
        holder.title.text = channel.name
        holder.active.isVisible = isActive
        holder.title.setTypeface(null, if (isActive) Typeface.BOLD else Typeface.NORMAL)
        holder.title.setTextColor(
            if (isActive) holder.title.context.colorFromAttribute(R.attr.textColor)
            else holder.title.context.colorFromAttribute(R.attr.grayTextColor)
        )
        holder.card.setCardBackgroundColor(
            if (isActive) holder.card.context.colorFromAttribute(R.attr.primaryBlackBackground)
            else holder.card.context.colorFromAttribute(R.attr.boxItemBackground)
        )
        holder.card.scaleX = if (isActive) 1.03f else 1f
        holder.card.scaleY = if (isActive) 1.03f else 1f
        holder.card.cardElevation = if (isActive) 8.toPx.toFloat() else 2.toPx.toFloat()
        holder.card.foreground = if (holder.card.hasFocus()) {
            ContextCompat.getDrawable(holder.card.context, R.drawable.outline_drawable_less)
        } else null
        holder.card.contentDescription = channel.name
        holder.card.setOnClickListener { onChannelClick(position) }
        holder.card.setOnFocusChangeListener { view, hasFocus ->
            view.foreground = if (hasFocus) {
                ContextCompat.getDrawable(view.context, R.drawable.outline_drawable_less)
            } else null
        }
    }

    override fun getItemCount(): Int = channels.size

    class ChannelViewHolder(
        val card: AspectRatioCardView,
        val poster: ImageView,
        val title: TextView,
        val active: TextView,
    ) : RecyclerView.ViewHolder(card)

    class AspectRatioCardView(context: android.content.Context) : CardView(context) {
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = (width * 9f / 16f).roundToInt()
            super.onMeasure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
            )
        }
    }
}
