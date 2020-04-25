package tw.yalan.mvvm.core.ui.base


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import tw.yalan.mvvm.core.ext.compositeDisposableFactory
import tw.yalan.mvvm.core.ui.base.listeners.BaseView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Yalan Ding on 02/01/2020
 */

abstract class BaseFragment : Fragment(), BaseView {

    fun Disposable.addDisposeQueue() = compositeDisposable.add(this)

    protected var baseViewModel: BaseViewModel? = null

    abstract val layoutId: Int

    //    @Inject
    private val compositeDisposable: CompositeDisposable by compositeDisposableFactory()


    protected abstract fun afterViewCreated(view: View, savedInstanceState: Bundle?)
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        afterViewCreated(view, savedInstanceState)
    }

    abstract fun observeViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
    }

    fun setupToolbar(toolbar: Toolbar?, title: String? = null, showArrow: Boolean = false) {
        (activity as BaseActivity?)?.setSupportActionBar(toolbar)
        (activity as BaseActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(showArrow)
        (activity as BaseActivity?)?.supportActionBar?.title = title ?: ""
    }
}
