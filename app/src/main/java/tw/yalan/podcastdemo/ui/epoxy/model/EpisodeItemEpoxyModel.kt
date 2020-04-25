package tw.yalan.podcastdemo.ui.epoxy.model

import androidx.appcompat.widget.AppCompatTextView
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageButton
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import tw.yalan.podcastdemo.R

/**
 * Created by Alan Ding on 2018/3/4.
 */
@EpoxyModelClass(layout = R.layout.row_episode_item)
abstract class EpisodeItemEpoxyModel : EpoxyModelWithHolder<EpisodeItemEpoxyModel.ViewHolder>() {
    @EpoxyAttribute
    var title: String? = null

    @EpoxyAttribute
    var playing: Boolean = false

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickButton: View.OnClickListener? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickItem: View.OnClickListener? = null


    override fun shouldSaveViewState(): Boolean {
        return true
    }

    override fun bind(holder: ViewHolder) {
        holder.tvName.text = title ?: ""
        holder.btnPlay.setImageResource(if (playing) R.drawable.ic_pause else R.drawable.ic_play)
        holder.btnPlay.visibility = View.VISIBLE
        holder.progress.visibility = View.GONE
        holder.btnPlay.setOnClickListener {
            if (!playing) {
                holder.progress.visibility = View.VISIBLE
                holder.btnPlay.visibility = View.INVISIBLE
            }
            onClickButton?.onClick(it)
        }
        holder.itemView.setOnClickListener(onClickItem)

    }

    override fun unbind(holder: ViewHolder) {
        holder.itemView.setOnClickListener(null)
    }

    class ViewHolder : KotlinEpoxyHolder() {
        lateinit var itemView: View
        val tvName by bind<AppCompatTextView>(R.id.tvName)
        val btnPlay by bind<AppCompatImageButton>(R.id.btnPlay)
        val progress by bind<ProgressBar>(R.id.progress)

        override fun bindView(itemView: View) {
            super.bindView(itemView)
            this.itemView = itemView
        }
    }

}