package vn.clothing.store.fragments

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import vn.clothing.store.R
import vn.clothing.store.databinding.FragmentHomeBinding
import vn.clothing.store.interfaces.HomeContract

class HomeFragment : Fragment(), HomeContract.View {
    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        initSlider()
        flashSale()
        return binding.root
    }

    private fun initSlider() {
        val imageList = ArrayList<SlideModel>() // Create image list

        imageList.add(SlideModel(R.drawable.slider1, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.slider2, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.slider3, ScaleTypes.CENTER_CROP))
        binding.imageSlider.setImageList(imageList)
    }

    private fun flashSale() {
        object : CountDownTimer(3 * 60 * 60 * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (60 * 60 * 1000) // tính giờ
                val minutes = (millisUntilFinished % (60 * 60 * 1000)) / (60 * 1000) // tính phút
                val seconds = (millisUntilFinished % (60 * 1000)) / 1000 // tính giây

                binding.tvHours.text = hours.toString()
                binding.tvMinute.text = minutes.toString()
                binding.tvSecond.text = seconds.toString()
            }

            override fun onFinish() {
                binding.lnTimeSale.visibility = View.GONE
            }
        }.start()

    }


    //===================================================
    //    region HomeContract.View
    //==================================================

    override fun onShowLoading() {

    }

    override fun onHiddenLoading() {

    }



    //================================================
    //  endregion HomeContract.View
    //===============================================
}