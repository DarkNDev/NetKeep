package com.darkndev.netkeep.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.darkndev.netkeep.databinding.FragmentSignInBinding
import com.darkndev.netkeep.utils.user.Event
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    //comes from navigation dependency
    private val viewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        binding.apply {

            signInUsername.setText(viewModel.signInUsername)
            signInPassword.setText(viewModel.signInPassword)

            signInUsername.doAfterTextChanged {
                viewModel.signInUsername = it.toString()
            }

            signInPassword.doAfterTextChanged {
                viewModel.signInPassword = it.toString()
            }

            signIn.setOnClickListener {
                viewModel.signInClicked()
            }

            createAccount.setOnClickListener {
                val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
                findNavController().navigate(action)
            }

            progress.hide()

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.status.collectLatest {
                    when (it) {
                        true -> progress.show()
                        false -> progress.hide()
                    }
                    createAccount.isEnabled = !it
                    signIn.isEnabled = !it
                    signInUsernameLayout.isEnabled = !it
                    signInPasswordLayout.isEnabled = !it
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.signingEvent.collectLatest {
                when (it) {
                    is Event.Navigate -> {
                        val action =
                            SignInFragmentDirections.actionSignInFragmentToHomeFragment()
                        findNavController().navigate(action)
                    }

                    is Event.ShowMessage -> {
                        Snackbar.make(
                            binding.root,
                            it.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.authenticate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}