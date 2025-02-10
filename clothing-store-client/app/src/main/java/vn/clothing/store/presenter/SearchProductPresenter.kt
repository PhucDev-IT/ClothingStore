package vn.clothing.store.presenter

import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.database.AppDatabase.Companion.APPDATABASE
import vn.clothing.store.interfaces.SearchProductContract
import vn.clothing.store.models.Category
import vn.clothing.store.models.Product
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback

class SearchProductPresenter (private var view: SearchProductContract.View?) : SearchProductContract.Presenter  {
    private var handler = Handler(Looper.getMainLooper())
    private var pageSize = 20
    private var currentPage = 0

    /*
   *  get categories from database
   * IF database haven't data -> call api get data
   * Then store data to database
    */
    override fun getCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            val categories = APPDATABASE.categoryDao().getAll()
            if (categories.isNotEmpty()) {
                handler.post{
                    view?.onResultCategories(categories)
                }
            } else {
                APISERVICE.getService().getAllCategories()
                    .enqueue(object : BaseCallback<ResponseModel<ArrayList<Category>>>() {
                        override fun onSuccess(model: ResponseModel<ArrayList<Category>>) {
                            if (model.success && model.data != null) {
                                APPDATABASE.categoryDao().upsertUser(model.data!!)
                                handler.post {

                                    view?.onResultCategories(model.data!!)
                                }
                            }
                        }

                        override fun onError(message: String) {
                            Log.e(TAG, "Error get categories: $message")
                        }
                    })
            }
        }
    }

    override fun getProductByCategory(categoryId: Int) {
        view?.onShowLoading()
        APISERVICE.getService()
            .getProductsByCategory(categoryId, pageSize, (currentPage * pageSize))
            .enqueue(object : BaseCallback<ResponseModel<List<Product>>>(){
                override fun onSuccess(model: ResponseModel<List<Product>>) {
                    view?.onHiddenLoading()
                    if(!model.success || model.data == null){
                        view?.onShowToast(model.error?.message, CoreConstant.ToastType.ERROR)
                    }else{
                        view?.onResultProducts(model.data!!)
                    }
                }

                override fun onError(message: String) {
                    view?.onHiddenLoading()
                }
            })
    }


    companion object{
        private val TAG = SearchProductPresenter::class.java.name
    }
}