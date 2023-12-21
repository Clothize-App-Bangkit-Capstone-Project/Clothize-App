package com.capstoneproject.clothizeapp.client.ui.client.order

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstoneproject.clothizeapp.client.api.response.Order
import com.capstoneproject.clothizeapp.client.data.adapter.OrderAdapter
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.ClientPrefViewModel
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.ClientPreferences
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.ClientPreferencesFactory
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.ClientSession
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.dataStore
import com.capstoneproject.clothizeapp.databinding.FragmentOrderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class OrderFragment : Fragment() {
    private lateinit var binding: FragmentOrderBinding
    private lateinit var viewModel: OrderViewModel
    private lateinit var clientPrefViewModel: ClientPrefViewModel
    private lateinit var clientSession: ClientSession
    private lateinit var orderAdapter: OrderAdapter

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var user: FirebaseUser? = null
    private val listOrder = mutableListOf<Order>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOrderBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    private fun init() {
        db = Firebase.firestore
        storage = FirebaseStorage.getInstance()
        user = FirebaseAuth.getInstance().currentUser

        viewModel = obtainViewModel(requireActivity())
        val pref = ClientPreferences.getInstance(requireActivity().dataStore)
        clientPrefViewModel =
            ViewModelProvider(this, ClientPreferencesFactory(pref))[ClientPrefViewModel::class.java]

        orderAdapter = OrderAdapter()
        binding.rvOrder.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvOrder.adapter = orderAdapter

        clientPrefViewModel.getSessionUser().observe(requireActivity()) { session ->
            if (session != null) {
                clientSession = session
                showRecyclerView()
            }
        }
    }

    private fun showRecyclerView() {

        val query: Query = db.collection("orders")
            .whereEqualTo("clientId", user!!.uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)


        query.get().addOnSuccessListener { documents ->
            if (documents.size() > 0) {
                if (listOrder.size > 0) {
                    listOrder.clear()
                }
                for (order in documents.documents) {
                    val data = Order(
                        orderId = order.id,
                        clientName = clientSession.fullName,
                        clientPhone = clientSession.phone,
                        tailorName = order.data!!["tailorName"].toString(),
                        tailorPhone = order.data!!["tailorPhone"].toString(),
                        gender = order.data!!["gender"].toString(),
                        service = order.data!!["service"].toString(),
                        size = order.data!!["size"].toString(),
                        color = order.data!!["color"].toString(),
                        qty = order.data!!["qty"].toString().toInt(),
                        estimation = order.data!!["estimation"].toString().toInt(),
                        status = order.data!!["status"].toString(),
                        price = order.data!!["price"].toString().toInt(),
                        createdAt = order.getTimestamp("createdAt")!!.toDate(),
                        comment = order.data!!["comment"].toString(),
                        urlImg = order.data!!["urlImg"].toString(),
                        isTailorRejected = order.getBoolean("isTailorRejected"),
                        clientId = order.data!!["userId"].toString(),
                        tailorId = order.data!!["tailorId"].toString(),
                    )
                    listOrder.add(data)
                }

                orderAdapter.submitList(listOrder)
                binding.rvOrder.visibility = View.VISIBLE
                binding.titleEmpty.visibility = View.GONE

            } else {
                binding.rvOrder.visibility = View.GONE
                binding.titleEmpty.visibility = View.VISIBLE
            }
        }.addOnFailureListener {
            Toast.makeText(
                requireActivity(),
                "There is a problem fetching data: ${it.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun obtainViewModel(activity: Activity): OrderViewModel {
        val factory = OrderViewModelFactory.getInstance(activity.applicationContext)
        return ViewModelProvider(
            this, factory
        )[OrderViewModel::class.java]
    }

}