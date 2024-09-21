package vn.clothing.store.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "address")
class AddressSchema(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var provinceId: String,
    var districtId: String,
    var wardId: String,
    var address: String,
    var isDefault: Boolean
)