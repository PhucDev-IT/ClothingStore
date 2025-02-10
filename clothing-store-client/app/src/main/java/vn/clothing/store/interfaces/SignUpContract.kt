package vn.clothing.store.interfaces

import android.content.Context
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.models.User
import vn.clothing.store.networks.request.RegisterRequestModel

interface SignUpContract {
    interface View{
        fun onShowLoading()
        fun onHideLoading()
        fun getContext(): Context
        fun onShowError(message:String?)
        fun onShowToast(type: CoreConstant.ToastType, message:String)
        fun signUpSuccess(user: User)
    }

    interface Presenter{
        fun loginWithGoogle()
        fun requestSignUp(requeset:RegisterRequestModel)

    }
}