package com.example.clothingstoreapp.Model

data class ProvincesJson(
    var name: String,
    var code: Int,
    var division_type: String,
    var codename: String,
    var phone_code: Int,
    var districts: List<DistrictsJson>,
)