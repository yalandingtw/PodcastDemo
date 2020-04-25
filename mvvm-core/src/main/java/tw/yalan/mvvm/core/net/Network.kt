package tw.yalan.mvvm.core.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import tw.yalan.mvvm.core.ui.base.CoreScope

@CoreScope
class Network {

    companion object Utils {
        private fun getNetworkInfo(context: Context): NetworkInfo? {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo
        }

        fun isConnected(context: Context): Boolean {
            val info =
                getNetworkInfo(context)
            return info != null && info.isConnected
        }
    }
}

