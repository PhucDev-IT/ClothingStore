package vn.clothing.store.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import vn.clothing.store.database.models.AddressSchema
import vn.clothing.store.models.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAll(): List<Category>

    @Insert
    fun insertAll(categoryList: List<Category>)

    @Update
    fun update(category: Category)

    @Delete
    fun delete(category: Category)

    @Query("DELETE FROM address")
    fun deleteAllUsers()


    @Transaction
    fun upsertUser(categoryList: List<Category>) {
        deleteAllUsers()
        insertAll(categoryList)
    }
}