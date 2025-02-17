package vn.clothing.store.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import vn.clothing.store.R
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.adapter.RvNotificationAdapter
import vn.clothing.store.common.AppManager
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.database.AppDatabase.Companion.APPDATABASE
import vn.clothing.store.databinding.ActivityNotificationBinding
import vn.clothing.store.models.NotificationModel
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback

class NotificationActivity : BaseActivity() {
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: RvNotificationAdapter
    private val GSON: Gson =
        GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").setLenient().create()

    override fun initView() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        adapter = RvNotificationAdapter {
            markAsRead(it)
        }
        binding.rvNotification.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvNotification.layoutManager = linearLayoutManager
        val dividerItemDecoration =
            DividerItemDecoration(binding.rvNotification.context, DividerItemDecoration.VERTICAL)
        binding.rvNotification.addItemDecoration(dividerItemDecoration)

        binding.header.tvName.text = getString(R.string.title_header_notification)

    }

    override fun populateData() {
        getAllNotification()
    }

    override fun setListener() {
        binding.header.toolbar.setNavigationOnClickListener { finish() }

        binding.tvReadAll.setOnClickListener{
            val list = adapter.getUnReads()
            if(list.isNotEmpty()){
                adapter.markAll()
                updateNotification(ArrayList(list))
            }
        }
    }

    override val layoutView: View
        get() {
            binding = ActivityNotificationBinding.inflate(layoutInflater)
            return binding.root
        }

    private fun showLoading() {
        binding.llContainerNotificaiton.visibility = View.GONE
        binding.shimmerLayout.startLayoutAnimation()
        binding.shimmerLayout.visibility = View.VISIBLE
    }

    private fun onHideLoading() {
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
        binding.llContainerNotificaiton.visibility = View.VISIBLE
    }

    private fun getAllNotification() {
        showLoading()
        APISERVICE.getService(AppManager.token).getNotification()
            .enqueue(object : BaseCallback<ResponseModel<List<NotificationModel>>>() {
                override fun onSuccess(model: ResponseModel<List<NotificationModel>>) {
                    if (model.success && model.data != null) {
                        adapter.setData(model.data!!)
                    } else {
                        CoreConstant.showToast(
                            this@NotificationActivity,
                            "Không có thông báo",
                            CoreConstant.ToastType.INFO
                        )
                    }
                    onHideLoading()
                }

                override fun onError(message: String) {
                    PopupDialog.showDialog(this@NotificationActivity,PopupDialog.PopupType.NOTIFICATION,getString(R.string.can_not_success),message){}
                }
            })
    }



    private fun markAsRead(notificationModel: NotificationModel) {
        CoroutineScope(Dispatchers.IO).launch {
            handler.post {
                updateNotification(arrayListOf(notificationModel.id))
            }
        }
    }

    private fun updateNotification(ids:ArrayList<String>) {
        val jsonObject = JsonObject().apply {
            add("ids", GSON.toJsonTree(ids))
        }
        APISERVICE.getService(AppManager.token).markReadNotification(jsonObject)
            .enqueue(object : BaseCallback<ResponseModel<Boolean>>() {
                override fun onSuccess(model: ResponseModel<Boolean>) {

                }

                override fun onError(message: String) {

                }
            })
    }
}