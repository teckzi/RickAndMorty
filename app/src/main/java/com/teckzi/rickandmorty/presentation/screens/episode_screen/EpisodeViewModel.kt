package com.teckzi.rickandmorty.presentation.screens.episode_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.teckzi.rickandmorty.domain.model.EpisodeModel
import com.teckzi.rickandmorty.domain.use_cases.UseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor(
    private val useCases: UseCases
) : ViewModel() {

    private val _getAllEpisodes = useCases.getAllEpisodeUseCase()
    val getAllEpisodes = _getAllEpisodes

    private val _searchEpisode = MutableStateFlow<PagingData<EpisodeModel>>(PagingData.empty())
    val searchEpisode = _searchEpisode

    fun searchEpisode(
        name: String?,
        episode: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            useCases.getSearchedEpisodeUseCase(
                name = name,
                episode = episode
            ).cachedIn(viewModelScope).collect {
                _searchEpisode.value = it
            }
        }
    }
}