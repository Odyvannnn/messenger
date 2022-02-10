package com.example.messenger.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class User(val uid: String, val username: String): Parcelable {
    constructor() : this("", "")
}