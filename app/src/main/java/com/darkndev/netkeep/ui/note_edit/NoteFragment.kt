package com.darkndev.netkeep.ui.note_edit

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.darkndev.netkeep.R
import com.darkndev.netkeep.databinding.FragmentNoteEditBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteFragment : Fragment(R.layout.fragment_note_edit), MenuProvider {

    private var _binding: FragmentNoteEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNoteEditBinding.bind(view)

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(
            this@NoteFragment,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

        binding.apply {
            if (viewModel.note != null) saveNote.setImageResource(R.drawable.done)

            titleText.setText(viewModel.title)
            contentText.setText(viewModel.content)

            titleText.doAfterTextChanged {
                viewModel.title = it.toString()
            }
            contentText.doAfterTextChanged {
                viewModel.content = it.toString()
            }

            saveNote.setOnClickListener {
                activity?.hideKeyboard(it)
                viewModel.saveClicked()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.noteEvent.collectLatest {
                when (it) {
                    is NoteViewModel.NoteEvent.Navigate -> {
                        setFragmentResult("SAVED", bundleOf("SAVED" to "Sync Notes?"))
                        findNavController().popBackStack()
                    }

                    is NoteViewModel.NoteEvent.ShowMessage -> {
                        Snackbar.make(view, "Check Fields", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_note, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {

        R.id.action_delete -> {
            viewModel.deleteClicked()
            true
        }

        else -> false
    }
}