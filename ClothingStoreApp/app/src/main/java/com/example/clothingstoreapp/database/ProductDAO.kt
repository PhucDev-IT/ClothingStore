package com.example.clothingstoreapp.database

import androidx.room.Dao
import androidx.room.Query
import com.example.clothingstoreapp.Model.ProductIsLiked
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDAO : DaoInterface<ProductIsLiked> {

    @Query("SELECT * FROM ProductIsLiked")
    fun selectAllProducts():List<ProductIsLiked>?

    @Query("SELECT COUNT(*) FROM ProductIsLiked WHERE idProduct = :id")
    fun findByID(id:String): Int
}