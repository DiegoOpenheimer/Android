package estudando.com.br.studykoin

import com.google.gson.Gson
import estudando.com.br.studykoin.adapter.ListApapter
import estudando.com.br.studykoin.cities.CityViewModel
import estudando.com.br.studykoin.cities.adapter.CityAdapter
import estudando.com.br.studykoin.repository.RepositoryCity
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ApplicationModule = module {

    single { Gson() }

    single { RepositoryCity(get()) }

    factory { ListApapter() }

    factory { CityAdapter() }

    viewModel { MainViewModel(get()) }

    viewModel { CityViewModel(get()) }

}