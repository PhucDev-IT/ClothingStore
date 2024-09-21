package vn.clothing.store.interfaces

interface HomeContract {
    interface View {
        fun onShowLoading()
        fun onHiddenLoading()
    }

    interface Presenter{
        fun loadData()
    }
}