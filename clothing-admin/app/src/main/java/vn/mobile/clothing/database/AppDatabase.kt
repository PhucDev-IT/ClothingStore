package vn.mobile.clothing.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import vn.mobile.clothing.database.dao.UserDao
import vn.mobile.clothing.database.core.Converters
import vn.mobile.clothing.MyApplication
import vn.mobile.clothing.models.User


@Database(entities = [ User::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
  //  abstract fun productFavoriteDao(): ProductFavoriteDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val DATABASE_NAME: String = "clothing_admin.db"
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
