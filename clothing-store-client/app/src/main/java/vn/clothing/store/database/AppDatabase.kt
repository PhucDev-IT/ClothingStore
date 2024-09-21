package vn.clothing.store.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import vn.clothing.store.MyApplication
import vn.clothing.store.database.dao.AddressDao
import vn.clothing.store.database.models.AddressSchema
import vn.master.epass.database.core.Converters


@Database(entities = [AddressSchema::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun addressDao(): AddressDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val DATABASE_NAME: String = "clothing.db"
        val APPDATABASE = getInstance()
        private fun getInstance(): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    MyApplication.instance.context,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).allowMainThreadQueries().build().also { instance = it }
            }
        }
    }
}
