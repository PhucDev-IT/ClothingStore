package vn.clothing.store.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "category")
class Category {
    @PrimaryKey
    var id:Int=1
    var name:String?=null
    @SerializedName("is_public")
    var isPublic:Boolean=false

}