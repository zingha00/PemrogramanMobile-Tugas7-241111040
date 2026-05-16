package com.utama.aplikasiloginsederhana

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.utama.aplikasiloginsederhana.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil username dari intent HomeActivity
        val username = activity?.intent?.getStringExtra("username") ?: "User"

        // Set data profil
        binding.tvProfileName.text = username
        binding.tvInfoName.text = username
        binding.tvProfileEmail.text = "user@example.com"
        binding.tvInfoEmail.text = "user@example.com"
        binding.tvInfoPhone.text = "+62 812-3456-7890"

        // Tombol Logout
        binding.btnLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Berhasil keluar", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}