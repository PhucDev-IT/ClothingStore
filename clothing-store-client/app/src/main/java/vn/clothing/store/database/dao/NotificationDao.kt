package vn.clothing.store.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import vn.clothing.store.models.Category
import vn.clothing.store.models.NotificationModel

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification")
    fun getAll(): List<NotificationModel>

    @Insert
    fun insertAll(notifications: List<NotificationModel>)

    @Update
    fun update(notification: NotificationModel)

    @Delete
    fun delete(notification: NotificationModel)

    @Query("DELETE FROM notification")
    fun deleteAllUsers()



}