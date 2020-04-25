package tw.yalan.mvvm.core.data.local

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import tw.yalan.mvvm.core.ui.base.CoreScope

/**
 * Created by Yalan Ding on 02/01/2020
 *
 */

@CoreScope
class LocalRepository
constructor(
    val sharedPreferences: SharedPreferences
) {


}
