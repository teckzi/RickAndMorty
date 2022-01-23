package com.teckzi.rickandmorty.presentation.di.character

import com.teckzi.rickandmorty.di.annotation.FragmentScope
import com.teckzi.rickandmorty.presentation.screens.character_screen.CharacterFragment
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [CharacterFragmentModule::class])
interface CharacterFragmentComponent {
    fun inject(characterFragment: CharacterFragment)
}