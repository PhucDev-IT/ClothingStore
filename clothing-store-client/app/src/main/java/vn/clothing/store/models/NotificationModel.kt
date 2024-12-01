package vn.clothing.store.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(tableName = "notification")
data class NotificationModel(
    @PrimaryKey
    var id: String,
    var title: String?,
    var content: String?,
    var date: Date?,
    var type: String?,
    @SerializedName("is_action")
    var isAction: Boolean?,
    @SerializedName("is_read")
    var isRead: Boolean?,
    @SerializedName("user_id")
    var userId:String?
)