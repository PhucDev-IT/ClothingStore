package vn.clothing.store.activities.common

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import vn.clothing.store.R

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(layoutView)
        initView()
        setListener()
        populateData()

    }


    abstract fun initView()
    abstract fun populateData()
    abstract fun setListener()

    abstract val layoutView: View


    protected fun requestPermission(permission:ArrayList<String>,runnable: Runnable){
        Dexter.withContext(this).withPermissions(permission)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport) {
                    if (p0.areAllPermissionsGranted()){
                        runnable.run()
                    }else{
                        Toast.makeText(this@BaseActivity,"Vui lòng cấp quyền", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest>,
                    permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                }
            }).check()
    }

}