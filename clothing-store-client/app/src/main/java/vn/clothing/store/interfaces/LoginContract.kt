package vn.clothing.store.interfaces

import android.content.Context
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.models.User

interface LoginContract {
    interface View{
        fun onShowLoading()
        fun onHideLoading()
        fun getContext():Context
        fun onShowError(message:String?)
        fun onShowToast(type: CoreConstant.ToastType, message:String?)
        fun onLoginSuccess(user:User)
    }

    interface Presenter{
        fun loginWithGoogle()
        fun loginSystem(email:String, password:String)
    }
}