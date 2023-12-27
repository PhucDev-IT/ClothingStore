package com.example.clothingstoreapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.clothingstoreapp.Model.Product
import com.example.clothingstoreapp.Model.ProductIsLiked

@Database(entities = [ProductIsLiked::class], version = 1)
abstract class ProductDatabase : RoomDatabase() {

    companion object {
        private val DATABASE_NAME: String = "ClothingStore.db"
        private var INSTANCE: ProductDatabase? = null

        fun getInstance(context: Context): ProductDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    ProductDatabase::class.java,
                    DATABASE_NAME
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE!!
        }
    }

    abstract fun productDao(): ProductDAO
}