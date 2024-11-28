package vn.mobile.clothing.network.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResponseModel<T> {
    @Expose
    var success = false

    @SerializedName("error")
    @Expose
    var error: ErrorResponseModel? = null

    @SerializedName("data")
    @Expose
    var data: T? = null

    constructor()

    constructor(success: Boolean, error: ErrorResponseModel?, data: T) {
        this.success = success
        this.error = error
        this.data = data
    }
}

class Pagination(var totalItems:Int?,var totalPages:Int?,var currentPage:Int?,var itemsPerPage:Int?)