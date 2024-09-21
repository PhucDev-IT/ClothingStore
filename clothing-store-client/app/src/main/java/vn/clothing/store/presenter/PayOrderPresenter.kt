package vn.clothing.store.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.clothing.store.database.AppDatabase
import vn.clothing.store.interfaces.PayOrderContract

class PayOrderPresenter(private var view: PayOrderContract.View?): PayOrderContract.Presenter {

    override fun getDefaultAddress() {
        CoroutineScope(Dispatchers.IO).launch {
            val list =  AppDatabase.APPDATABASE.addressDao().getAll()
            withContext(Dispatchers.Main) {
              view?.onResultAddress(list)
            }
        }
    }

    override fun onDestroy() {
        view = null
    }
}