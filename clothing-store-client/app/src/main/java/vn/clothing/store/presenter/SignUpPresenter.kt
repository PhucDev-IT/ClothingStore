package vn.clothing.store.presenter

import vn.clothing.store.R
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.interfaces.SignUpContract
import vn.clothing.store.models.User
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.clothing.store.networks.request.RegisterRequestModel
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback

class SignUpPresenter(private var view:SignUpContract.View?):SignUpContract.Presenter {
    override fun loginWithGoogle() {

    }

    override fun requestSignUp(requeset: RegisterRequestModel) {
        view?.onShowLoading()
        APISERVICE.getService().signUp(requeset).enqueue(object :BaseCallback<ResponseModel<User>>(){
            override fun onSuccess(model: ResponseModel<User>) {
                view?.onHideLoading()
                if(model.success && model.data!=null){
                    view?.signUpSuccess(model.data!!)
                }else{
                    view?.onShowToast(CoreConstant.ToastType.ERROR,view?.getContext()?.getString(R.string.has_error_please_retry)?:"")
                }
            }

            override fun onError(message: String) {
                view?.onHideLoading()
                view?.onShowError(message)
            }
        })
    }
}