package vn.clothing.store.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.clothing.store.R
import vn.clothing.store.activities.settings.UpdateAddressActivity
import vn.clothing.store.adapter.RvMyAddressAdapter
import vn.clothing.store.common.IntentData
import vn.clothing.store.databinding.FragmentMyAddressBinding
import vn.clothing.store.interfaces.MyAddressContract
import vn.clothing.store.models.DeliveryInformation
import vn.clothing.store.presenter.MyAddressPresenter

class MyAddressFragment : Fragment(), MyAddressContract.View {
    private lateinit var _binding: FragmentMyAddressBinding
    private val binding get() = _binding
    private var presenter: MyAddressPresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyAddressBinding.inflate(inflater, container, false)
        presenter = MyAddressPresenter(this)
        binding.rvAddress.adapter = presenter?.adapter
        val dividerItemDecoration = DividerItemDecoration(binding.rvAddress.context, DividerItemDecoration.VERTICAL)
        binding.rvAddress.addItemDecoration(dividerItemDecoration)
        binding.rvAddress.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                presenter?.removeAddressToDatabase(viewHolder.adapterPosition)
                presenter?.removeItem(viewHolder.adapterPosition)
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

                        val icon = BitmapFactory.decodeResource(context?.resources, R.drawable.bin)
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
        itemTouchHelper.attachToRecyclerView(binding.rvAddress)

        initUi()
        return binding.root
    }

    private fun initUi() {
        binding.header.tvName.text = getString(R.string.header_address_delivery)
        binding.lnAddNewAddress.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, AddNewDeliveryInfoFragment()).addToBackStack(null).commit()
        }

        binding.header.toolbar.setNavigationOnClickListener {
           requireActivity().finish()
        }
    }

    //========================================
    // region MyAddressContract.View
    //========================================
    override fun onShowLoading() {
        binding.shimmerLayout.startLayoutAnimation()
        binding.shimmerLayout.visibility = View.VISIBLE
    }

    override fun onHiddenLoading() {
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
    }

    override fun onSelectedAddress(delivery: DeliveryInformation) {
        val intent = Intent(requireContext(), UpdateAddressActivity::class.java)
        intent.putExtra(IntentData.KEY_ADDRESS, delivery)
        startActivity(intent)
    }

    //======================================
    // endregion
    //======================================


    override fun onResume() {
        super.onResume()
        presenter?.getAllAddress()
    }

}