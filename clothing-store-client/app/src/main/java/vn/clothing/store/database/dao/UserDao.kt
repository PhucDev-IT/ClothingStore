package vn.clothing.store.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import vn.clothing.store.database.models.AddressSchema
import vn.clothing.store.models.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Insert
    fun insertAll(vararg user: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)

    @Query("DELETE FROM user")
    fun deleteAllUsers()


    @Transaction
    fun upsertUser(user: User) {
        deleteAllUsers()
        insertAll(user)
    }
}