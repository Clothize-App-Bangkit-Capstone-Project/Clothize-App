package com.capstoneproject.clothizeapp.client.ui.client.history

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstoneproject.clothizeapp.client.api.response.Measurement
import com.capstoneproject.clothizeapp.client.data.adapter.HistoryAdapter
import com.capstoneproject.clothizeapp.client.ui.client.measurements.MeasurementViewModel
import com.capstoneproject.clothizeapp.client.ui.client.measurements.MeasurementViewModelFactory
import com.capstoneproject.clothizeapp.databinding.FragmentHistoryBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var viewModel: MeasurementViewModel
    private lateinit var db: FirebaseFirestore
    private var user: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    private fun init() {
        db = Firebase.firestore
        user = FirebaseAuth.getInstance().currentUser
        showRecyclerView()
    }

    private fun showRecyclerView() {
        val query: Query = db.collection("measurements")
            .whereEqualTo("userId", user!!.uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)

        query.get().addOnSuccessListener {documents ->
            if (documents.size() > 0){
                binding.rvHistory.visibility = View.VISIBLE
                binding.titleEmpty.visibility = View.GONE
                val options = FirestoreRecyclerOptions.Builder<Measurement>()
                    .setQuery(query, Measurement::class.java)
                    .setLifecycleOwner(requireActivity())
                    .build()


                val adapter = HistoryAdapter(options)
                binding.rvHistory.layoutManager = LinearLayoutManager(requireActivity())
                binding.rvHistory.adapter = adapter
            }else{
                binding.rvHistory.visibility = View.GONE
                binding.titleEmpty.visibility = View.VISIBLE
            }
        }.addOnFailureListener {
            Toast.makeText(requireActivity(), "There is a problem fetching data: ${it.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun obtainViewModel(activity: Activity): MeasurementViewModel {
        val factory = MeasurementViewModelFactory.getInstance(activity.applicationContext)
        return ViewModelProvider(
            this, factory
        )[MeasurementViewModel::class.java]
    }


}