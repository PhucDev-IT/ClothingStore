package vn.clothing.store.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import vn.clothing.store.MyApplication
import vn.clothing.store.database.dao.AddressDao
import vn.clothing.store.database.dao.CategoryDao
import vn.clothing.store.database.dao.UserDao
import vn.clothing.store.models.Category
import vn.clothing.store.models.DeliveryInformation
import vn.clothing.store.models.User
import vn.master.epass.database.core.Converters


@Database(entities = [DeliveryInformation::class, User::class, Category::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun addressDao(): AddressDao
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao

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
                ).fallbackToDestructiveMigration()
                    .allowMainThreadQueries().build().also { instance = it }
            }
        }
    }
}
