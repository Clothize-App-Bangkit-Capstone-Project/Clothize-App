package com.capstoneproject.clothizeapp.client.ui.client.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.capstoneproject.clothizeapp.client.api.response.Tailor
import com.capstoneproject.clothizeapp.client.data.adapter.TailorAdapter
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.ClientPrefViewModel
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.ClientPreferences
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.ClientPreferencesFactory
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.dataStore
import com.capstoneproject.clothizeapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var clientPrefViewModel: ClientPrefViewModel
    private lateinit var tailorAdapter: TailorAdapter

    private lateinit var db: FirebaseFirestore
    private var user: FirebaseUser? = null
    private lateinit var storage: FirebaseStorage

    private val listTailor = mutableListOf<Tailor>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        init()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showRecycleList()
    }

    private fun init() {
        db = Firebase.firestore
        storage = FirebaseStorage.getInstance()
        user = FirebaseAuth.getInstance().currentUser

        val pref = ClientPreferences.getInstance(requireActivity().dataStore)
        clientPrefViewModel =
            ViewModelProvider(this, ClientPreferencesFactory(pref))[ClientPrefViewModel::class.java]


        // Initialize ViewModel
        viewModel = obtainViewModel(requireActivity())


        binding.apply {
            clientPrefViewModel.getSessionUser().observe(requireActivity()) { session ->
                if (session != null) {
                    binding.tvTitleHome.text = "Welcome, ${session.fullName}"
                }

            }
            btnFind.setOnClickListener {
                if (edtSearchTailor.text.toString().isNotEmpty()) {
                    // search tailor
                    tailorAdapter.searchTailor(edtSearchTailor.text.toString())
                } else {
                    showRecycleList()
                }
            }
        }

    }


    private fun showRecycleList(tailorName: String = "") {

        val query: Query = if (tailorName == "") {
            db.collection("tailors")
        } else {
            db.collection("tailors").whereEqualTo("storeName", tailorName)
        }

        tailorAdapter = TailorAdapter()
        binding.rvTailor.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.rvTailor.adapter = tailorAdapter

        query.get().addOnSuccessListener { documents ->
            if (documents.size() > 0) {
                if (listTailor.size > 0){
                    listTailor.clear()
                }
                for (tailor in documents.documents) {
                   val data = Tailor(
                       storeName = tailor.data!!["storeName"].toString(),
                       storeImg = tailor.data!!["storeImg"].toString(),
                       description = tailor.data!!["description"].toString(),
                       phone = tailor.data!!["phone"].toString(),
                       location = tailor.getGeoPoint("location"),

                   )
                    listTailor.add(data)
                }

                tailorAdapter.setData(listTailor)
                binding.rvTailor.visibility = View.VISIBLE
                binding.titleEmpty.visibility = View.GONE

            } else {
                binding.rvTailor.visibility = View.GONE
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


    private fun obtainViewModel(activity: Activity): HomeViewModel {
        val factory = HomeViewModelFactory.getInstance(activity.applicationContext)
        return ViewModelProvider(
            this, factory
        )[HomeViewModel::class.java]
    }


    //    private fun initializeData(){
//        val inputStream = resources.openRawResource(R.raw.tailor)
//        val jsonText = inputStream.bufferedReader().use { it.readText() }
//        val listType = object : TypeToken<List<Tailor>>() {}.type
//        val raw: List<Tailor> = Gson().fromJson(jsonText, listType)
//
//        for (tailor in raw){
//            val tailorRaw = hashMapOf(
//                "storeName" to tailor.nameTailor,
//                "phone" to tailor.phone,
//                "location" to GeoPoint(tailor.latitude, tailor.longitude),
//                "storeImg" to tailor.photoTailor,
//                "description" to tailor.descriptionTailor
//            )
//            db.collection("tailors").add(tailorRaw)
//        }
//
//
//    }
}