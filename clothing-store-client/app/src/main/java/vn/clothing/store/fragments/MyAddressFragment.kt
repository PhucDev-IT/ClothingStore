package vn.clothing.store.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import vn.clothing.store.R
import vn.clothing.store.adapter.RvMyAddressAdapter
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


        initUi()
        return binding.root
    }

    private fun initUi() {
        presenter?.getAllAddress()
        binding.header.tvName.text = getString(R.string.header_address_delivery)
        binding.lnAddNewAddress.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, AddNewDeliveryInfoFragment()).addToBackStack(null).commit()
        }

        binding.header.toolbar.setNavigationOnClickListener {
           parentFragmentManager.popBackStack()
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


    //======================================
    // endregion
    //======================================


}