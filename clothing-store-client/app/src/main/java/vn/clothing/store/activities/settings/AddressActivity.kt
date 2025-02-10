package vn.clothing.store.activities.settings

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import vn.clothing.store.R
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.databinding.ActivityAddressBinding
import vn.clothing.store.fragments.MyAddressFragment

class AddressActivity : BaseActivity() {
    private lateinit var binding:ActivityAddressBinding
    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MyAddressFragment())
                .commitNow()
        }
    }

    override fun populateData() {

    }

    override fun setListener() {

    }

    override val layoutView: View
        get() {
            binding = ActivityAddressBinding.inflate(layoutInflater)
            return binding.root
        }
}