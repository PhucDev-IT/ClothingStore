package vn.clothing.store.interfaces

import android.content.Context
import vn.clothing.store.common.CoreConstant

interface SignUpContract {
    interface View{
        fun onShowLoading()
        fun onHideLoading()
        fun getContext(): Context
        fun onShowError(message:String?)
        fun onShowToast(type: CoreConstant.ToastType, message:String)
    }

    interface Presenter{
        fun loginWithGoogle()

    }
}