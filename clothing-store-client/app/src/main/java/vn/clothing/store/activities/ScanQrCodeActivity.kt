package vn.clothing.store.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.util.Util
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

import vn.clothing.store.R
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.activities.order.TrackOrderActivity
import vn.clothing.store.common.AppManager
import vn.clothing.store.common.IntentData
import vn.clothing.store.databinding.ActivityScanQrCodeBinding
import vn.clothing.store.models.ItemQrCodeEmbed
import vn.clothing.store.utils.Utils

class ScanQrCodeActivity : BaseActivity(),ZXingScannerView.ResultHandler {

    private var mScannerView: ZXingScannerView? = null
    private lateinit var binding:ActivityScanQrCodeBinding


    override fun handleResult(result: Result?) {
        mScannerView?.resumeCameraPreview(this)
        if (result == null) {
            return
        }
        mScannerView?.stopCamera()
        if(result.barcodeFormat == BarcodeFormat.EAN_13){
            val intent  = Intent(this,ProductDetailsActivity::class.java)
            intent.putExtra(IntentData.KEY_PRODUCT,result.text)
            startActivity(intent)
        }else if(result.barcodeFormat == BarcodeFormat.QR_CODE){
           try{
               val jsonString = Utils.decodeFromBase64(result.text)
               val model = Gson().fromJson(jsonString, ItemQrCodeEmbed::class.java)
               val intent = Intent(this, TrackOrderActivity::class.java)
               intent.putExtra(IntentData.KEY_ORDER,model.idModel)
               intent.putExtra(IntentData.KEY_USER_ID, model.userId)
               startActivity(intent)
           }catch (e:Exception){
               Log.e("ScanQrCodeActivity","Lỗi: ${e.message}")
           }
        }
        else{
            Toast.makeText(this,"Không phải mã vạch EAN-13",Toast.LENGTH_SHORT).show()
        }


    }

    override fun initView() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.header.tvName.text = "Quét mã"
        mScannerView = ZXingScannerView(this)
        binding.contentFrame.addView(mScannerView)
        mScannerView?.setAutoFocus(true)
    }

    override fun populateData() {
    }

    override fun setListener() {
        binding.header.toolbar.setNavigationOnClickListener { finish() }
    }

    override val layoutView: View
        get() {
            binding = ActivityScanQrCodeBinding.inflate(layoutInflater)
            return binding.root
        }

    override fun onPause() {
        super.onPause()
        mScannerView?.stopCamera()
    }


    override fun onResume() {
        super.onResume()
        requestPermission(arrayListOf(android.Manifest.permission.CAMERA)){
            mScannerView?.startCamera()
            mScannerView?.setResultHandler(this)
            mScannerView?.setAspectTolerance(0.2f)
            mScannerView?.setAutoFocus(true)
            mScannerView?.startCamera()
        }
    }

}