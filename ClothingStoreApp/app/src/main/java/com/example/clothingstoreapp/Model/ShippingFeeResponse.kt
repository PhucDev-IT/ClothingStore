package com.example.clothingstoreapp.Model

import java.io.Serializable

data class ShippingFeeResponse(
    val code: Int,
    val message: String,
    val data: ShippingFeeData
): Serializable

data class ShippingFeeData(
    val total: Int,
    val service_fee: Int,
    val insurance_fee: Int,
    val pick_station_fee: Int,
    val coupon_value: Int,
    val r2s_fee: Int,
    val return_again: Int,
    val document_return: Int,
    val double_check: Int,
    val cod_fee: Int,
    val pick_remote_areas_fee: Int,
    val deliver_remote_areas_fee: Int,
    val cod_failed_fee: Int
):Serializable
