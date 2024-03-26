package com.example.clothingstoreapp.Model

import java.io.Serializable
import java.util.Date

class TrackingOrder:Serializable {

    private var _id: String? = null
    var id: String?
        get() = _id
        set(value) {
            _id = value
        }

    private var _orderID: String? = null
    var orderID: String?
        get() = _orderID
        set(value) {
            _orderID = value
        }

    private var _name: String? = null
    var name: String?
        get() = _name
        set(value) {
            _name = value
        }


    private var _time: Date = Date()
    var time: Date
        get() = _time
        set(value) {
            _time = value
        }

    private var _describe: String? = null
    var describe: String?
        get() = _describe
        set(value) {
            _describe = value
        }

    constructor()

    constructor(orderID: String?, name: String?, time: Date, describe: String?) {
        this.orderID = orderID
        this.name = name
        this.time = time
        this.describe = describe
    }
}