package vn.clothing.store.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import vn.clothing.store.database.models.AddressSchema
import java.util.Date

@Dao
interface AddressDao {

    @Query("SELECT * FROM address")
    fun getAll(): List<AddressSchema>

    @Query("SELECT * FROM address WHERE id = :id LIMIT 1")
    fun getAddressById(id: Int): AddressSchema?

    @Insert
    fun insertAll(vararg addresses: AddressSchema)

    @Update
    fun update(addresses: AddressSchema)

    @Delete
    fun delete(addresses: AddressSchema)

    @Query("DELETE FROM address")
    fun deleteAllUsers()


    @Transaction
    fun upsertUser(addresses: AddressSchema) {
        deleteAllUsers()
        insertAll(addresses)
    }
}