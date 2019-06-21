package estudando.com.br.studykoin.repository

import com.google.gson.Gson
import estudando.com.br.studykoin.model.City
import estudando.com.br.studykoin.model.State

class RepositoryCity(private val gson: Gson) {

    fun getStates(json: String): List<State> {
        return gson.fromJson(json, Array<State>::class.java).toList()
    }

    fun getCities(json: String): List<City> {
        return gson.fromJson(json, Array<City>::class.java).toList()
    }

}