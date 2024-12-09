package vn.mobile.clothing.network.response

import com.google.gson.annotations.SerializedName

data class StatisticalStatusOrderResModel (val status: String,
                                           @SerializedName("order_count")
                                           val count: Int)