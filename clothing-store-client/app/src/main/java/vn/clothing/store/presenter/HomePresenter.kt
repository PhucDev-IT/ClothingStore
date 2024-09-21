package vn.clothing.store.presenter

import vn.clothing.store.interfaces.HomeContract

class HomePresenter : HomeContract.Presenter {
    private var isLoading: Boolean = false
    private var isLastPage = false

    override fun loadData() {

    }

    private fun getFirstData(){

    }
}