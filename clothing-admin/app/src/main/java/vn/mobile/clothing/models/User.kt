package vn.mobile.clothing.models

import androidx.annotation.NonNull
import androidx.databinding.adapters.Converters
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity("user")
class User {
    @PrimaryKey
    var id: String = ""
    @SerializedName("last_name")
    var lastName:String?=null
    @SerializedName("first_name")
    var firstName:String?=null
    @SerializedName("full_name")
    var fullName:String?=null
    @SerializedName("date_of_birth")
    var dateOfBirth:Date?=null
    @SerializedName("number_phone")
    var numberPhone:String?=null
    var email:String?=null
    var gender:Boolean?=null
    var password:String?=null
    @SerializedName("is_active")
    var isActive:Boolean = false
    @TypeConverters(vn.mobile.clothing.database.core.Converters::class)
    var roles:List<String>?=null

}