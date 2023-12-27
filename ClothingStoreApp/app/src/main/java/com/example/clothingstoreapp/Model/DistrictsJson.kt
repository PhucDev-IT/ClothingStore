package com.example.clothingstoreapp.Model

data class DistrictsJson(
    val name: String,
    val code: Int,
    val codename: String,
    val division_type: String,
    val province_code: Int,
    val wards: List<WardJson>,
) {

}