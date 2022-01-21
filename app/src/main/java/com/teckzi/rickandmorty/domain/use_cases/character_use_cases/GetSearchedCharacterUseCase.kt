package com.teckzi.rickandmorty.domain.use_cases.character_use_cases

import androidx.paging.PagingData
import com.teckzi.rickandmorty.domain.model.CharacterModel
import com.teckzi.rickandmorty.domain.repository.IRepository
import kotlinx.coroutines.flow.Flow

class GetSearchedCharacterUseCase(
    private val repository: IRepository
) {
    suspend operator fun invoke(
        name: String?,
        status: String?,
        species: String?,
        type: String?,
        gender: String?
    ): Flow<PagingData<CharacterModel>> {
        return repository.searchCharacters(
            name = name,
            status = status,
            species = species,
            type = type,
            gender = gender
        )
    }
}