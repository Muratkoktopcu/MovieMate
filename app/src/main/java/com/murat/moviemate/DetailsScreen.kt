package com.murat.moviemate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.murat.moviemate.MoviesFragment.RetrofitClient
import com.murat.moviemate.model.MovieData
import com.murat.moviemate.model.MovieResponse
import com.murat.moviemate.service.MovieAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DetailsScreen : Fragment() {
    private lateinit var voteAverage: TextView
    private lateinit var genres: TextView
    private lateinit var overview: TextView
    private lateinit var movieImageView: ImageView
    private lateinit var movieTitle: TextView
    private lateinit var movieYear: TextView
    private lateinit var backButton: Button
    private lateinit var favButton: Button
    private lateinit var watchlistButton: Button
    private lateinit var movieId: String
    private lateinit var auth:FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var isFavorite: Boolean = false
    private var isWatchlist: Boolean = false


    object RetrofitClient {
        private const val BASE_URL = "https://api.themoviedb.org/3/"

        val api: MovieAPI by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MovieAPI::class.java) // service
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_details_screen, container, false)
        movieId = arguments?.getString("id").toString()
        voteAverage = view.findViewById(R.id.voteAverage)
        genres = view.findViewById(R.id.genre)
        overview = view.findViewById(R.id.overview)
        movieImageView = view.findViewById(R.id.poster)
        movieTitle = view.findViewById(R.id.movieTitle)
        movieYear = view.findViewById(R.id.movieYear)
        backButton = view.findViewById(R.id.backButton)
        favButton = view.findViewById(R.id.favButton)
        watchlistButton=view.findViewById(R.id.watchlistButton)

        loadData()
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        favButton.setOnClickListener {
            toggleFavorite()
        }

        checkIfFavorite()

        watchlistButton.setOnClickListener {
            toggleWatchlist()
        }

        checkIfWatchlist()
        return view
    }
    private fun loadData(){
        val call: Call<MovieData> = RetrofitClient.api.getMovieDetails(movieId)
        call.enqueue(object :Callback<MovieData>{
            override fun onResponse(call: Call<MovieData>, response: Response<MovieData>) {
                if(response.isSuccessful){
                    response.body()?.let {
                        Glide.with(requireContext())
                            .load("https://image.tmdb.org/t/p/w500${it.posterPath}")
                            .into(movieImageView)
                        movieTitle.text = it.title
                        movieYear.text = it.releaseDate
                        voteAverage.text = it.voteAverage.toString()
                        overview.text = it.overview
                        val genreText = it.genres.joinToString(", ") { it.name }
                        genres.text = genreText
                    }
                }
            }

            override fun onFailure(call: Call<MovieData>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to load movie details.", Toast.LENGTH_SHORT).show()
            }

        })

    }
    private fun toggleFavorite() {
        val user = auth.currentUser

        if (user != null) {
            val moviesCollection = firestore.collection("Users").document(user.email!!).collection("Favorites")
            val query = moviesCollection
                .whereEqualTo("id", movieId)
                .get()

            query.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    if (result != null && result.isEmpty) {
                        addFavorite()
                    } else {
                        removeFavorite(result)
                    }
                } else {
                    Toast.makeText(requireContext(), "Error checking favorites: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
    private fun addFavorite() {
        val user = auth.currentUser

        if (user != null) {
            val moviesCollection = firestore.collection("Users").document(user.email!!).collection("Favorites")

            val data1 = hashMapOf(
                "id" to movieId,
            )

            moviesCollection.add(data1)
                .addOnSuccessListener {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Data added successfully", Toast.LENGTH_SHORT).show()
                        isFavorite=true
                        updateFavoriteButton()
                    }
                }
                .addOnFailureListener { e ->
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Error adding document: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun removeFavorite(result: QuerySnapshot) {
        for (document in result) {
            document.reference.delete()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT).show()
                    isFavorite = false
                    updateFavoriteButton()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error removing document: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun checkIfFavorite() {
        val user = auth.currentUser

        if (user != null) {
            val query = firestore.collection("Users").document(user.email!!).collection("Favorites")
                .whereEqualTo("id", movieId)
                .get()

            query.addOnSuccessListener { result ->
                isFavorite = !result.isEmpty
                updateFavoriteButton()
            }
        }
    }
    private fun updateFavoriteButton() {
        if (isFavorite) {
            favButton.text = "Remove from Favorites"
        } else {
            favButton.text = "Add to Favorites"
        }
    }
    private fun toggleWatchlist() {
        val user = auth.currentUser

        if (user != null) {
            val moviesCollection = firestore.collection("Users").document(user.email!!).collection("Watchlist")
            val query = moviesCollection
                .whereEqualTo("id", movieId)
                .get()

            query.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    if (result != null && result.isEmpty) {
                        addWatchlist()
                    } else {
                        removeWatchlist(result)
                    }
                } else {
                    Toast.makeText(requireContext(), "Error checking favorites: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
    private fun addWatchlist() {
        val user = auth.currentUser

        if (user != null) {
            val moviesCollection = firestore.collection("Users").document(user.email!!).collection("Watchlist")

            val data1 = hashMapOf(
                "id" to movieId,

                )

            moviesCollection.add(data1)
                .addOnSuccessListener {
                    if (isAdded) {

                        Toast.makeText(requireContext(), "Data added successfully", Toast.LENGTH_SHORT).show()
                        isWatchlist=true
                        updateWatchlistButton()
                    }

                }
                .addOnFailureListener { e ->
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Error adding document: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun removeWatchlist(result: QuerySnapshot) {
        for (document in result) {
            document.reference.delete()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Removed from watchlist", Toast.LENGTH_SHORT).show()
                    isWatchlist = false
                    updateWatchlistButton()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error removing document: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun checkIfWatchlist() {
        val user = auth.currentUser

        if (user != null) {
            val query = firestore.collection("Users").document(user.email!!).collection("Watchlist")
                .whereEqualTo("id", movieId)
                .get()

            query.addOnSuccessListener { result ->
                isWatchlist = !result.isEmpty
                updateWatchlistButton()
            }
        }
    }
    private fun updateWatchlistButton() {
        if (isWatchlist) {
            watchlistButton.text = "Remove from Watchlist"
        } else {
            watchlistButton.text = "Add to Watchlist"
        }
    }
}