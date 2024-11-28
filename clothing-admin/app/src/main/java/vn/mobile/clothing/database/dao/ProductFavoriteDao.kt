//package vn.mobile.clothing.database.dao
//
//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.Query
//import androidx.room.Transaction
//import androidx.room.Update
//import retrofit2.http.GET
//import vn.clothing.store.models.DeliveryInformation
//import vn.clothing.store.models.ProductFavorite
//import vn.clothing.store.models.User
//
//@Dao
//interface ProductFavoriteDao {
//    @Query("SELECT * FROM product_favorite")
//    fun getAll(): List<ProductFavorite>
//
//    @Insert
//    fun insert(productFavorite: ProductFavorite)
//
//    @Delete
//    fun delete(productFavorite: ProductFavorite)
//
//    @Query("DELETE FROM product_favorite")
//    fun deleteAllUsers()
//
//    @Query("SELECT * FROM product_favorite WHERE id = :id LIMIT 1")
//    fun getProductFavoriteById(id: String): ProductFavorite?
//
//    @Transaction
//    fun upsert(productFavorite: ProductFavorite) {
//        if(getProductFavoriteById(productFavorite.id) == null) {
//            insert(productFavorite)
//        }else{
//            delete(productFavorite)
//        }
//    }
//}