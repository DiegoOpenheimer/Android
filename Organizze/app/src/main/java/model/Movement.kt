package model

import com.google.firebase.database.Exclude

class Movement {

    val id: String? = null
    var category: String? = null
    var date: String? = null
    var description: String? = null
    var type: String? = null
    var value: Double? = null

    @Exclude @set:Exclude @get:Exclude
    var key: String? = null
}