package model

import com.google.firebase.database.Exclude

class User {

    var name: String = ""

    var email: String = ""

    @Exclude @get:Exclude @set:Exclude
    var password: String = ""

    @Exclude @get:Exclude @set:Exclude
    var id: String? = null

    var totalExpenditure: Double = 0.0

    var totalIncoming: Double = 0.0
}