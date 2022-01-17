package com.teckzi.rickandmorty.presentation.screens.episode_screen

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import com.teckzi.rickandmorty.R
import com.teckzi.rickandmorty.databinding.FragmentEpisodeBinding
import com.teckzi.rickandmorty.presentation.adapters.EpisodeAdapter
import com.teckzi.rickandmorty.presentation.adapters.LoaderStateAdapter
import com.teckzi.rickandmorty.util.Constants.EPISODE_TYPE
import com.teckzi.rickandmorty.util.Constants.FILTER_RETURN_BACK_TO_EPISODE
import com.teckzi.rickandmorty.util.Constants.FILTER_TYPE_ARGUMENT_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EpisodeFragment : Fragment(R.layout.fragment_episode), SearchView.OnQueryTextListener,
    SwipyRefreshLayout.OnRefreshListener {
    private val viewModel by viewModels<EpisodeViewModel>()
    private val binding by viewBinding(FragmentEpisodeBinding::bind)
    private lateinit var episodeAdapter: EpisodeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initRecyclerView()
        getEpisodes()
        filterButton()
        getFilterResult()
        binding.swipeRefreshLayout.setOnRefreshListener(this)
    }

    private fun initRecyclerView() {
        episodeAdapter = EpisodeAdapter(requireContext())
        binding.episodeMainRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.episodeMainRecyclerView.adapter = episodeAdapter.withLoadStateHeaderAndFooter(
            header = LoaderStateAdapter(),
            footer = LoaderStateAdapter()
        )
        episodeAdapter.addLoadStateListener { state: CombinedLoadStates ->
            binding.episodeMainRecyclerView.isVisible = state.refresh != LoadState.Loading
            binding.episodeProgressBar.isVisible = state.refresh == LoadState.Loading
        }
    }

    private fun getEpisodes() {
        lifecycleScope.launch {
            viewModel.getAllEpisodes.collectLatest {
                episodeAdapter.submitData(it)
            }
        }
    }

    private fun filterButton() {
        binding.filterFloatingButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_episodeFragment_to_bottomSheet,
                bundleOf(FILTER_TYPE_ARGUMENT_KEY to EPISODE_TYPE)
            )
        }
    }

    private fun getFilterResult() {
        arguments?.getString(FILTER_RETURN_BACK_TO_EPISODE)?.let {
            lifecycleScope.launch(context = Dispatchers.Main) {
                searchEpisode(episode = it)
            }
        }
    }

    private fun searchEpisode(
        name: String? = null,
        episode: String? = null,
    ) {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.searchEpisode(
                name = name,
                episode = episode
            )
            viewModel.searchEpisode.collectLatest { pagingData ->
                episodeAdapter.submitData(pagingData)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val search = menu.findItem(R.id.search)
        val searchView = search.actionView as? SearchView
        searchView?.queryHint = "Search episode..."
        searchView?.setOnQueryTextListener(this)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val search = menu.findItem(R.id.search)
        val searchView = search.actionView as? SearchView
        searchView?.isIconified = true
        searchView?.isIconified = true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) searchEpisode(name = query)
        return true
    }

    override fun onRefresh(direction: SwipyRefreshLayoutDirection?) {
        getEpisodes()
        binding.swipeRefreshLayout.isRefreshing = false
    }
    companion object {
        private const val TAG = "TAG EpisodeFragment"
    }
}
