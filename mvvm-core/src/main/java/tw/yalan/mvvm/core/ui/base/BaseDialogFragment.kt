package tw.yalan.mvvm.core.ui.base

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

/**
 * Created by Yalan Ding on 2020/3/30.
 */
abstract class BaseDialogFragment() : DialogFragment() {

    interface OnDismissListener {
        fun onDismiss(dialog: DialogInterface)
    }

    abstract val layoutId: Int

    var onDismissListener: OnDismissListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        afterViewCreated(view, savedInstanceState)
    }

    abstract fun afterViewCreated(view: View, savedInstanceState: Bundle?)


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss(dialog)
    }
}