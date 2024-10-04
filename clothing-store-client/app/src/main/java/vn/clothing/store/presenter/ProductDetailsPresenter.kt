package vn.clothing.store.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.clothing.store.R
import vn.clothing.store.common.AppManager
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.database.AppDatabase.Companion.APPDATABASE
import vn.clothing.store.interfaces.ProductDetailsContract
import vn.clothing.store.models.CartModel
import vn.clothing.store.models.Image
import vn.clothing.store.models.ProductDetails
import vn.clothing.store.models.ProductFavorite
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.clothing.store.networks.request.CartRequestModel
import vn.clothing.store.networks.response.CartResponseModel
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback

class ProductDetailsPresenter(private var view: ProductDetailsContract.View?):ProductDetailsContract.Presenter {
    override fun loadInformationProduct(productId:String) {
        view?.onShowLoading()
        APISERVICE.getService().getProductDetails(productId).enqueue(object : BaseCallback<ResponseModel<List<ProductDetails>>>(){
            override fun onSuccess(model: ResponseModel<List<ProductDetails>>) {
                if(!model.success || model.data == null){
                    view?.onShowError(model.error?.message)
                }else{
                    view?.onResultProductDetails(model.data!!)
                }
                view?.onHiddenLoading()
            }

            override fun onError(message: String) {
                view?.onHiddenLoading()
                view?.onShowError(message)
            }
        })
    }

    override fun loadImages(id: String) {
        APISERVICE.getService().getImagesByModel("PRODUCT",id).enqueue(object : BaseCallback<ResponseModel<List<Image>>>(){
            override fun onSuccess(model: ResponseModel<List<Image>>) {
                if(!model.success || model.data == null){
                    view?.onShowError(model.error?.message)
                }else{
                    view?.onResultImages(model.data!!)
                }
            }

            override fun onError(message: String) {
                view?.onShowError(message)
            }
        })
    }

    override fun addToCart(model:CartRequestModel){
        view?.onShowLoading()
       APISERVICE.getService(AppManager.token).addToCart(model).enqueue(object : BaseCallback<ResponseModel<CartModel.CartItem>>(){
           override fun onSuccess(model: ResponseModel<CartModel.CartItem>) {
               view?.onHiddenLoading()
               if(!model.success || model.data == null){
                   view?.onShowError(model.error?.message)
               }else{
                   view?.onShowToast(view?.getContext()?.getString(R.string.add_to_card_success),CoreConstant.ToastType.SUCCESS)
               }
           }

           override fun onError(message: String) {
               view?.onHiddenLoading()
               view?.onShowError(message)
           }
       })
    }


   override fun upsertProductFavorite(productFavorite: ProductFavorite){
        CoroutineScope(Dispatchers.IO).launch {
            APPDATABASE.productFavoriteDao().upsert(productFavorite)
        }
    }

    override fun checkFavorite(productId: String) {
        APPDATABASE.productFavoriteDao().getProductFavoriteById(productId).let {
            view?.isProductIsFavorite(it != null)
        }
    }
}