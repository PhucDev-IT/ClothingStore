package vn.clothing.store.presenter

import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.clothing.store.common.AppManager
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.database.AppDatabase.Companion.APPDATABASE
import vn.clothing.store.interfaces.HomeContract
import vn.clothing.store.models.Category
import vn.clothing.store.models.Product
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback

class HomePresenter(private var view: HomeContract.View?) : HomeContract.Presenter {

    private var handler = Handler(Looper.getMainLooper())
    private var pageSize = 20
    private var currentPage = 0


    companion object {
        private val TAG = HomePresenter::class.java.name
    }

    override fun loadData() {

    }

    private fun getFirstData() {

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
                   view?.onShowCategories(categories)
               }
            } else {
                APISERVICE.getService().getAllCategories()
                    .enqueue(object : BaseCallback<ResponseModel<ArrayList<Category>>>() {
                        override fun onSuccess(model: ResponseModel<ArrayList<Category>>) {
                            if (model.success && model.data != null) {
                                APPDATABASE.categoryDao().upsertUser(model.data!!)
                                handler.post {

                                    view?.onShowCategories(model.data!!)
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


    override fun countNotification() {
        APISERVICE.getService(AppManager.token).countNotifications(AppManager.user?.id?:"").enqueue(object : BaseCallback<ResponseModel<Int>>(){
            override fun onSuccess(model: ResponseModel<Int>) {
                if(model.success && model.data!=null){
                    view?.onCountNotification(model.data!!)
                }else{
                    view?.onCountNotification(0)
                }
            }

            override fun onError(message: String) {
                view?.onCountNotification(0)
            }
        })
    }
}