package vn.clothing.store.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.clothing.store.R
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.activities.order.PayOrderActivity
import vn.clothing.store.adapter.RvItemCartAdapter
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.common.IntentData
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.customview.CircleAnimationUtil
import vn.clothing.store.databinding.ActivityShoppingCartBinding
import vn.clothing.store.interfaces.ShoppingCartContract
import vn.clothing.store.networks.response.CartResponseModel
import vn.clothing.store.presenter.ShoppingCartPresenter
import vn.clothing.store.utils.FormatCurrency


class ShoppingCartActivity : BaseActivity(),ShoppingCartContract.View {

    private lateinit var binding:ActivityShoppingCartBinding
    private var presenter:ShoppingCartPresenter?=null
    private lateinit var adapter:RvItemCartAdapter
    private var cart:CartResponseModel?=null
    private var selectedCart:ArrayList<CartResponseModel.CartItemResponseModel> = arrayListOf()


    override fun initView() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.header.tvName.text = getString(R.string.title_header_cart)
        presenter = ShoppingCartPresenter(this)
        setUp()
        val dividerItemDecoration = DividerItemDecoration(binding.rvCart.context, DividerItemDecoration.VERTICAL)
        binding.rvCart.addItemDecoration(dividerItemDecoration)
        binding.rvCart.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rvCart.setHasFixedSize(true)
    }

    override fun populateData() {
    }

    override fun setListener() {
        binding.btnBuyProduct.setOnClickListener {
            if(selectedCart.isEmpty()){
                CoreConstant.showToast(this,getString(R.string.please_select_item),CoreConstant.ToastType.WARNING)
                return@setOnClickListener
            }
            val intent =Intent(this,PayOrderActivity::class.java)
            intent.putExtra(IntentData.KEY_LIST_CART_ITEM,selectedCart)
            startActivity(intent)
        }

        binding.header.toolbar.setNavigationOnClickListener {
            finish()
        }



        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                presenter?.removeCart(adapter.getId(viewHolder.adapterPosition))
                adapter.removeItem(viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val p = Paint()
                val itemView = viewHolder.itemView
                val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                val width = height / 3

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if (dX < 0) {
                        // Xử lý khi vuốt trái
                        p.color = Color.RED
                        val background = RectF(
                            itemView.right.toFloat() + dX, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat()
                        )
                        c.drawRect(background, p)

                        val icon = BitmapFactory.decodeResource(this@ShoppingCartActivity.resources, R.drawable.bin)
                        val iconWidth = width
                        val iconMargin = (height - iconWidth) / 2
                        val iconLeft = itemView.right.toFloat() - iconMargin - iconWidth
                        val iconRight = itemView.right.toFloat() - iconMargin
                        val iconTop = itemView.top.toFloat() + iconMargin
                        val iconBottom = itemView.bottom.toFloat() - iconMargin
                        val iconDest = RectF(iconLeft, iconTop, iconRight, iconBottom)
                        c.drawBitmap(icon, null, iconDest, p)
                        icon.recycle() // Giải phóng bitmap
                    }
                } else {
                    c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.rvCart)

    }

    override val layoutView: View
        get() {
            binding = ActivityShoppingCartBinding.inflate(layoutInflater)
            return binding.root
        }

    private fun setUp(){
        adapter = RvItemCartAdapter(arrayListOf(),{ checked->
            if(checked.first){
                selectedCart.add(checked.second)
            }else{
                selectedCart.remove(checked.second)
            }
            displayPrice()
        },{ item ->
            val index = selectedCart.indexOf(item)
            if (index != -1) {
                selectedCart[index].quantity  = item.quantity
            }
            displayPrice()
        })
        binding.rvCart.adapter = adapter
    }

    /**
     * Whenever change item, we will calculate price
     */
    private fun displayPrice(){
        var price:Double = 0.0
        for(item in selectedCart){
            price += item.price!! * item.quantity!!
        }
        binding.tvSumMoney.text = FormatCurrency.numberFormat.format(price)
    }

    //===================================================
    //    region ShoppingCartContract.View
    //==================================================

    override fun onShowLoading() {
        binding.shimmerLayout.startLayoutAnimation()
        binding.shimmerLayout.visibility = View.VISIBLE
    }

    override fun onHiddenLoading() {
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
    }

    override fun showError(message: String?) {
        PopupDialog.showDialog(this,PopupDialog.PopupType.NOTIFICATION,null,message?:getString(R.string.has_error_please_retry)){}
    }

    override fun onResultCarts(cart: CartResponseModel?) {
        if(cart == null || cart.listItem.isNullOrEmpty()){
            binding.lnNotCart.visibility = View.VISIBLE
            binding.rvCart.visibility = View.GONE
        }else{
            binding.lnNotCart.visibility = View.GONE
            binding.rvCart.visibility = View.VISIBLE
            adapter.setData(cart.listItem)
            this.cart = cart
        }
    }

    //================================================
    //  endregion ShoppingCartContract.View
    //===============================================

    override fun onDestroy() {
        presenter?.onDestroy()
        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        setUp()
        presenter?.getAllCarts()
        selectedCart = arrayListOf<CartResponseModel.CartItemResponseModel>()
        binding.tvSumMoney.text = "0đ"
    }

}
