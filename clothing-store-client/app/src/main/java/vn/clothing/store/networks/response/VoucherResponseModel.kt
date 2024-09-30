package vn.clothing.store.networks.response

import vn.clothing.store.models.VoucherModel
import vn.mobile.banking.network.response.Pagination

class VoucherResponseModel {
    var vouchers:List<VoucherModel>?=null
    var pagination: Pagination?=null
}