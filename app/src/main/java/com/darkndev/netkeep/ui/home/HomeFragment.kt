package com.darkndev.netkeep.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.darkndev.netkeep.R
import com.darkndev.netkeep.databinding.FragmentHomeBinding
import com.darkndev.netkeep.recyclerview.ItemOffsetDecoration
import com.darkndev.netkeep.recyclerview.NoteAdapter
import com.darkndev.netkeep.ui.home.HomeViewModel.State.LOADING
import com.darkndev.netkeep.ui.home.HomeViewModel.State.NOT_LOADING
import com.darkndev.netkeep.utils.onQueryTextChange
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), MenuProvider {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var noteAdapter: NoteAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(
            this@HomeFragment,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

        noteAdapter = NoteAdapter { note ->
            val action = HomeFragmentDirections.actionHomeFragmentToNoteFragment("Edit Note", note)
            findNavController().navigate(action)
        }

        binding.apply {
            recyclerView.apply {
                adapter = noteAdapter
                addItemDecoration(ItemOffsetDecoration(4))
            }

            addNote.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToNoteFragment("Add Note")
                findNavController().navigate(action)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.state.collectLatest {
                    when (it) {
                        LOADING -> {
                            progressBar.show()
                        }

                        NOT_LOADING -> {
                            progressBar.hide()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.homeEvent.collectLatest {
                when (it) {
                    is HomeViewModel.HomeEvent.ShowMessage -> {
                        Snackbar.make(view, it.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.allNotes.observe(viewLifecycleOwner) {
            noteAdapter.submitFilterableList(it) {
                Runnable {
                    binding.recyclerView.scrollToPosition(0)
                }
            }
        }

        setFragmentResultListener("SAVED") { _, bundle ->
            val message = bundle.getString("SAVED")
            message?.let {
                Snackbar.make(view, it, Snackbar.LENGTH_SHORT).setAction("YES") {
                    viewModel.refresh()
                }.show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_home, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        searchView.onQueryTextChange {
            noteAdapter.filter.filter(it)
            binding.recyclerView.scrollToPosition(0)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.action_refresh -> {
            viewModel.refresh()
            true
        }

        else -> false
    }
}