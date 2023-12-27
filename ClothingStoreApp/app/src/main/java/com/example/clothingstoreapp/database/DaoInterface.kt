package com.example.clothingstoreapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.clothingstoreapp.Model.Product

@Dao
interface DaoInterface<M> {
    @Insert
    fun insert(m : M)

    @Update
    fun update(m:M)

    @Delete
    fun delete(m : M)

}