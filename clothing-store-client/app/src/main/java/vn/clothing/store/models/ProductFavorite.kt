package vn.clothing.store.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_favorite")
data class ProductFavorite(@PrimaryKey var id:String, var name:String, var price:Float, var image:String)