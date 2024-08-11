package com.murat.moviemate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.murat.moviemate.adapters.RecyclerVerticalAdapter


class WatchlistScreen : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var movieAdapter: RecyclerVerticalAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_watchlist_screen, container, false)

        recyclerView = view.findViewById(R.id.recyclerWatch)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager


        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottom_navigation_watch)

        bottomNavigationView.selectedItemId = R.id.navigation_watch

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    findNavController().navigate(R.id.action_watchlistScreen_to_moviesFragment)
                    true
                }
                R.id.navigation_fav -> {
                    findNavController().navigate(R.id.action_watchlistScreen_to_favoritesScreen)
                    true
                }
                R.id.navigation_profile -> {
                    findNavController().navigate(R.id.action_watchlistScreen_to_profileScreen)
                    true
                }
                R.id.navigation_watch -> true
                else -> false
            }
        }

        fetchData()


        return view
    }

    private fun fetchData() {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        if (user != null) {
            firestore.collection("Users").document(user.email!!).collection("Watchlist")
                .get()
                .addOnSuccessListener { result ->
                    val movieList = mutableListOf<HashMap<String, Any>>()
                    for (document in result) {
                        val movieData = document.data as HashMap<String, Any>
                        movieList.add(movieData)
                    }

                    movieAdapter = RecyclerVerticalAdapter(
                        movieList,
                        object : RecyclerVerticalAdapter.Listener {
                            override fun onItemClick(movie: HashMap<String, Any>) {
                                val movieId = movie["id"] as? String
                                if (movieId != null) {
                                    val bundle = Bundle().apply {
                                        putString("id", movieId)
                                    }
                                    findNavController().navigate(R.id.action_watchlistScreen_to_detailsScreen, bundle)
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Movie ID not found.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    )
                    recyclerView.adapter = movieAdapter
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Could not fetch data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

}