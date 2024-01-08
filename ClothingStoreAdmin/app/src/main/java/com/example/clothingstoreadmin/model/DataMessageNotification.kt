package com.example.clothingstoreadmin.model

data class DataMessageNotification(
    val to:String,
    val data:Data
)

data class Data(
    val title:String,
    val message:String
)