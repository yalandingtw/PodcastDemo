package tw.yalan.mvvm.core.ui.base

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import tw.yalan.mvvm.core.ext.compositeDisposableFactory
import tw.yalan.mvvm.core.ui.base.listeners.ActionBarView
import tw.yalan.mvvm.core.ui.base.listeners.BaseView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import tw.yalan.mvvm.core.ext.getLastFragment

/**
 * Created by Yalan Ding on 02/01/2020
 */

@CoreScope
abstract class BaseActivity : AppCompatActivity(), BaseView, ActionBarView {

    fun Disposable.addDisposeQueue() = compositeDisposable.add(this)

    private val compositeDisposable: CompositeDisposable by compositeDisposableFactory()

    protected lateinit var baseViewModel: BaseViewModel

    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        observeViewModel()
    }

    override fun setUpIconVisibility(visible: Boolean) {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(visible)
    }

    override fun setTitle(titleKey: String) {
        val actionBar = supportActionBar
        actionBar?.title = titleKey
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)
        toolbar?.setNavigationOnClickListener {
            if (supportFragmentManager.getLastFragment() != null) {
                supportFragmentManager.popBackStack()
            } else {
                finish()
            }
        }
    }

    abstract fun observeViewModel()

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }
}
