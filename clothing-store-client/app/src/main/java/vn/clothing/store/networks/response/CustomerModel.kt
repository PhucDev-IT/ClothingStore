package vn.clothing.store.networks.response

import com.google.gson.annotations.SerializedName

data class CustomerModel(
    @SerializedName("id") val id: String,
    @SerializedName("object") val objectType: String,
    @SerializedName("address") val address: String? = null,
    @SerializedName("balance") val balance: Int,
    @SerializedName("created") val created: Long,
    @SerializedName("currency") val currency: String? = null,
    @SerializedName("default_source") val defaultSource: String? = null,
    @SerializedName("delinquent") val delinquent: Boolean,
    @SerializedName("description") val description: String? = null,
    @SerializedName("discount") val discount: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("invoice_prefix") val invoicePrefix: String,
    @SerializedName("invoice_settings") val invoiceSettings: InvoiceSettings,
    @SerializedName("livemode") val livemode: Boolean,
    @SerializedName("metadata") val metadata: Map<String, String>,
    @SerializedName("name") val name: String? = null,
    @SerializedName("next_invoice_sequence") val nextInvoiceSequence: Int,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("preferred_locales") val preferredLocales: List<String>,
    @SerializedName("shipping") val shipping: String? = null,
    @SerializedName("tax_exempt") val taxExempt: String,
    @SerializedName("test_clock") val testClock: String? = null
)

data class InvoiceSettings(
    @SerializedName("custom_fields") val customFields: String? = null,
    @SerializedName("default_payment_method") val defaultPaymentMethod: String? = null,
    @SerializedName("footer") val footer: String? = null,
    @SerializedName("rendering_options") val renderingOptions: String? = null
)