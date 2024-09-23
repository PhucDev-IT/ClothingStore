package vn.clothing.store.models

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import vn.master.epass.database.core.Converters
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
    @TypeConverters(Converters::class)
    var roles:List<String>?=null

}