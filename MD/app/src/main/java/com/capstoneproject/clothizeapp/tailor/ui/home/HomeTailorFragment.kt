package com.capstoneproject.clothizeapp.tailor.ui.home

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
import com.capstoneproject.clothizeapp.databinding.FragmentHomeTailorBinding
import com.capstoneproject.clothizeapp.tailor.data.adapter.TailorOrderAdapter
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.TailorPrefViewModel
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.TailorPreferences
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.TailorPreferencesFactory
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.TailorSession
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.dataTailorStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class HomeTailorFragment : Fragment() {
    private lateinit var binding: FragmentHomeTailorBinding
    private lateinit var tailorPrefViewModel: TailorPrefViewModel
    private lateinit var homeTailorViewModel: HomeTailorViewModel
    private lateinit var tailorOrderAdapter: TailorOrderAdapter
    private lateinit var tailorSession: TailorSession

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var user: FirebaseUser? = null
    private val listOrder = mutableListOf<Order>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeTailorBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    private fun init() {
        db = Firebase.firestore
        storage = FirebaseStorage.getInstance()
        user = FirebaseAuth.getInstance().currentUser

        val pref = TailorPreferences.getInstance(requireActivity().dataTailorStore)
        tailorPrefViewModel =
            ViewModelProvider(this, TailorPreferencesFactory(pref))[TailorPrefViewModel::class.java]

        homeTailorViewModel = obtainViewModel(requireActivity())

        tailorOrderAdapter = TailorOrderAdapter()



        binding.apply {
            rvOrder.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = tailorOrderAdapter
            }

            tailorPrefViewModel.getSessionUser().observe(requireActivity()) { session ->
                if (session != null) {
                    tailorSession = session
                    tvTitleHome.text = "Welcome, ${session.storeName}"
                    showRecycleList()
                }
            }

            btnFind.setOnClickListener {
                if (edtSearchOrder.text.toString().isNotEmpty()) {
                    val orderAmount = tailorOrderAdapter.searchOrder(edtSearchOrder.text.toString())
                    binding.totalOrders.text = "Total Orders : ${orderAmount}"
                } else {
                    showRecycleList()
                }
            }
        }


    }

    private fun showRecycleList() {
        val query: Query = db.collection("orders")
            .whereEqualTo("tailorId", user!!.uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)


        query.get().addOnSuccessListener { documents ->
            if (documents.size() > 0) {
                if (listOrder.size > 0) {
                    listOrder.clear()
                }
                for (order in documents.documents) {
                    val data = Order(
                        orderId = order.id,
                        tailorName = tailorSession.storeName,
                        tailorPhone = tailorSession.phone,
                        clientName = order.data!!["clientName"].toString(),
                        clientPhone = order.data!!["clientPhone"].toString(),
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

                tailorOrderAdapter.setData(listOrder)
                binding.rvOrder.visibility = View.VISIBLE
                binding.totalOrders.text = "Total Orders : ${listOrder.size}"
            } else {
                binding.rvOrder.visibility = View.GONE
            }
        }.addOnFailureListener {
            Toast.makeText(
                requireActivity(),
                "There is a problem fetching data: ${it.message}",
                Toast.LENGTH_SHORT
            ).show()
        }


//        homeTailorViewModel.getAllOrderByTailorName(tailorSession.storeName, clientName)
//            .observe(requireActivity()) { orders ->
//                if (orders != null) {
//                    binding.totalOrders.text = "Total Orders : ${orders.size}"
//                    tailorOrderAdapter.submitList(orders)
//                }
//            }
    }

    private fun obtainViewModel(activity: Activity): HomeTailorViewModel {
        val factory = HomeTailorViewModelFactory.getInstance(activity.applicationContext)
        return ViewModelProvider(
            this, factory
        )[HomeTailorViewModel::class.java]
    }


    companion object{
        const val TAILOR_NAME = "name"
        const val TAILOR_PHONE = "phone"
    }

}