package com.example.clothingstoreapp.Model

import java.util.Date

class Notification {
    private var _id: String? = null
    var id: String?
        get() = _id
        set(value) {
            _id = value
        }

    private var _img: String? = null
    var img: String?
        get() = _img
        set(value) {
            _img = value
        }

    private var _title: String? = null
    var title: String?
        get() = _title
        set(value) {
            _title = value
        }


    private var _content: String? = null
    var content: String?
        get() = _content
        set(value) {
            _content = value
        }


    private var _time: Date? = null
    var time: Date?
        get() = _time
        set(value) {
            _time = value
        }

}