package com.utama.aplikasiloginsederhana

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.utama.aplikasiloginsederhana.databinding.FragmentEventBinding

class EventFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EventViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = EventRepository(requireContext())
        val factory = EventViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]

        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())

        viewModel.events.observe(viewLifecycleOwner) { eventList ->
            val adapter = EventAdapter(eventList) { event ->
                showEventDetailDialog(event)
            }
            binding.rvEvents.adapter = adapter
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        binding.fabAddEvent.setOnClickListener {
            showAddEventDialog()
        }
    }

    private fun showEventDetailDialog(event: Event) {
        AlertDialog.Builder(requireContext())
            .setTitle(event.name)
            .setMessage(
                "Tanggal  : ${event.date}\n" +
                "Lokasi   : ${event.location}\n" +
                "Harga    : ${event.getFormattedPrice()}\n" +
                "Deskripsi: ${event.description}"
            )
            .setPositiveButton("Daftar") { _, _ ->
                viewModel.registerEvent(event.id)
                Toast.makeText(requireContext(), "Mencoba mendaftar...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hapus") { _, _ ->
                viewModel.deleteEvent(event.id)
            }
            .setNeutralButton("Tutup", null)
            .show()
    }

    private fun showAddEventDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_event, null)

        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Event Baru")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val name = dialogView.findViewById<EditText>(R.id.etEventName).text.toString().trim()
                val date = dialogView.findViewById<EditText>(R.id.etEventDate).text.toString().trim()
                val location = dialogView.findViewById<EditText>(R.id.etEventLocation).text.toString().trim()
                val price = dialogView.findViewById<EditText>(R.id.etEventPrice).text.toString().toIntOrNull() ?: 0
                // Pastikan deskripsi diambil dari EditText jika ada, jika tidak gunakan default ""
                val description = dialogView.findViewById<EditText>(R.id.etEventDescription)?.text?.toString()?.trim() ?: ""

                if (name.isEmpty() || date.isEmpty() || location.isEmpty()) {
                    Toast.makeText(requireContext(), "Field utama wajib diisi!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val newEvent = Event(
                    id = 0,
                    name = name,
                    date = date,
                    location = location,
                    price = price,
                    description = description
                )
                viewModel.addEvent(newEvent)
                Toast.makeText(requireContext(), "Menyimpan event...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
