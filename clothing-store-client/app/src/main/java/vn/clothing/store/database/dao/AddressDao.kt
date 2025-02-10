package vn.clothing.store.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import vn.clothing.store.models.DeliveryInformation
import java.util.Date

@Dao
interface AddressDao {

    @Query("SELECT * FROM delivery_information")
    fun getAll(): List<DeliveryInformation>

    @Query("SELECT * FROM delivery_information WHERE id = :id LIMIT 1")
    fun getAddressById(id: Int): DeliveryInformation?

    @Insert
    fun insertAll( addresses: List<DeliveryInformation>)

    @Insert
    suspend fun insert(addresses: DeliveryInformation)

    @Update
    fun update(addresses: DeliveryInformation)

    @Delete
    fun delete(addresses: DeliveryInformation)

    @Query("DELETE FROM delivery_information")
    fun deleteAll()


    @Transaction
    suspend fun upsert(addresses: DeliveryInformation) {
        deleteAll()
        insert(addresses)
    }

    @Transaction
    fun upsertAll(addresses: List<DeliveryInformation>) {
        deleteAll()
        insertAll(addresses)
    }
}