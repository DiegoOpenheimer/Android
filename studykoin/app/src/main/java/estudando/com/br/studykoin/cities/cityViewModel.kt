package estudando.com.br.studykoin.cities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import estudando.com.br.studykoin.model.City
import estudando.com.br.studykoin.repository.RepositoryCity

class CityViewModel(private val repositoryCity: RepositoryCity) : ViewModel() {

    private val cityData = MutableLiveData<List<City>>()

    fun getObservable(): LiveData<List<City>> = cityData

    fun getCity(id: Int, json: String) {
        val list: List<City> = repositoryCity.getCities(json)
        cityData.postValue(
            list?.filter { it.stateId == id }
        )
    }

}