package vn.clothing.store.models

import com.google.gson.annotations.SerializedName

class Role {
    var id:Int?=null
    var name:String?=null
    @SerializedName("display_name")
    var displayName:String?=null
}

class Permission{
    var id:Int?=null
    var name:String?=null
    @SerializedName("display_name")
    var displayName:String?=null
}

class RolePermission{
    var role:Role?=null
    var permissions:List<Permission>?=null

}