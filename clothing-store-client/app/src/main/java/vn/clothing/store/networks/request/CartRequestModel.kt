package vn.clothing.store.networks.request

import com.google.gson.annotations.SerializedName

data class CartRequestModel(
    @SerializedName("product_details_id")
    var productDetailsId: Int, var quantity: Int, var color: String, var size: String
)
