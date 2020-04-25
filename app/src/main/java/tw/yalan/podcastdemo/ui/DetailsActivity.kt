package tw.yalan.podcastdemo.ui

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import tw.yalan.mvvm.core.data.dto.Podcast
import tw.yalan.mvvm.core.ext.getLastFragment
import tw.yalan.mvvm.core.ui.base.BaseActivity
import tw.yalan.podcastdemo.Keys
import tw.yalan.podcastdemo.R
import tw.yalan.podcastdemo.ui.fragments.details.DetailsFragment

class DetailsActivity : BaseActivity() {
    override val layoutId: Int = R.layout.activity_fragment_container

    var fragmentDetails: DetailsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentDetails = fragmentDetails
            ?: supportFragmentManager.findFragmentByTag("DetailsFragment") as DetailsFragment?
                    ?: DetailsFragment()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
            fragmentDetails?.let {
                if (!it.isAdded) {
                    supportFragmentManager.beginTransaction()
                        .add(R.id.container, it, "DetailsFragment")
                        .commit()
                } else {
                    supportFragmentManager.beginTransaction().show(it).commit()
                }
            }
        }

    }

    override fun observeViewModel() {
        //Do nothing
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentDetails = null
    }

    companion object {
        fun newBundle(podcast: Podcast): Bundle {
            return Bundle().apply {
                putParcelable(Keys.EXTRA_PODCAST, podcast)
            }
        }
    }
}
