package tw.yalan.podcastdemo.ui.epoxy.model

import androidx.appcompat.widget.AppCompatTextView
import android.view.View
import android.widget.ImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import tw.yalan.podcastdemo.App
import tw.yalan.podcastdemo.R
import tw.yalan.podcastdemo.ext.corner

/**
 * Created by Yalan Ding on 2020/4/23.
 */
@EpoxyModelClass(layout = R.layout.row_podcast_item)
abstract class PodcastItemEpoxyModel : EpoxyModelWithHolder<PodcastItemEpoxyModel.ViewHolder>() {
    @EpoxyAttribute
    var imageUrl: String? = null

    @EpoxyAttribute
    var title: String? = null

    @EpoxyAttribute
    var artistName: String? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickItem: View.OnClickListener? = null

    override fun shouldSaveViewState(): Boolean {
        return true
    }

    override fun bind(holder: ViewHolder) {
        holder.ivBg.let {
            Glide.with(App.get()).load(imageUrl).override(200, 200).corner(2).into(it)
        }
        holder.tvName.text = title ?: ""
        holder.tvArtist.text = artistName ?: ""
        holder.itemView.setOnClickListener(onClickItem)
    }

    override fun unbind(holder: ViewHolder) {
        holder.itemView.setOnClickListener(null)
    }

    class ViewHolder : KotlinEpoxyHolder() {
        lateinit var itemView: View
        val ivBg by bind<ImageView>(R.id.ivBg)
        val tvName by bind<AppCompatTextView>(R.id.tvName)
        val tvArtist by bind<AppCompatTextView>(R.id.tvArtist)

        override fun bindView(itemView: View) {
            super.bindView(itemView)
            this.itemView = itemView
        }
    }

}