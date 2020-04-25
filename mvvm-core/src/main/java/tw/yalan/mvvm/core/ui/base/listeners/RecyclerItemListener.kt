package tw.yalan.mvvm.core.ui.base.listeners


/**
 * Created by Yalan Ding on 02/01/2020
 */

interface RecyclerItemListener<T> {
    fun onItemSelected(position: Int, item: T)
}
