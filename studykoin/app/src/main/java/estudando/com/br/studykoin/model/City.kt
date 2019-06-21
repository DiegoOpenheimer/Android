package estudando.com.br.studykoin.model

import com.google.gson.annotations.SerializedName

data class City(
    val id: Int,
    val name: String,
    @SerializedName("state_id")
    val stateId: Int
)