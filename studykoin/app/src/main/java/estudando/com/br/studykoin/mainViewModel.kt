package estudando.com.br.studykoin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import estudando.com.br.studykoin.model.State
import estudando.com.br.studykoin.repository.RepositoryCity

class MainViewModel(private val repositoryCity: RepositoryCity) : ViewModel() {

    private val liveStates: MutableLiveData<List<State>> = MutableLiveData()


    fun getObservable(): LiveData<List<State>> = liveStates

    fun getCities(json: String) {
        val states: List<State> = repositoryCity.getStates(json)
        liveStates.postValue(states)
    }

}