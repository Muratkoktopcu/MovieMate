package com.murat.moviemate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.murat.moviemate.adapters.RecyclerHorizontalAdapter
import com.murat.moviemate.model.MovieResponse
import com.murat.moviemate.model.MovieResult
import com.murat.moviemate.service.MovieAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MoviesFragment : Fragment(),RecyclerHorizontalAdapter.Listener {

    private var upcomingMovies: List<MovieResult>? = null
    private var nowPlayingMovies: List<MovieResult>? = null
    lateinit var recyclerViewNowPlaying : RecyclerView
    lateinit var recyclerViewUpcoming : RecyclerView
    private var recyclerViewAdapter: RecyclerHorizontalAdapter? = null

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_movies, container, false)
        recyclerViewUpcoming = view.findViewById<RecyclerView>(R.id.upcomingrecycler)
        recyclerViewNowPlaying = view.findViewById<RecyclerView>(R.id.nowplayingrecycler)
        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottom_navigation_home)

        bottomNavigationView.selectedItemId = R.id.navigation_home


        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_fav -> {
                    findNavController().navigate(R.id.action_moviesFragment_to_favoritesScreen)
                    true
                }
                R.id.navigation_watch -> {
                    findNavController().navigate(R.id.action_moviesFragment_to_watchlistScreen)
                    true
                }
                R.id.navigation_profile -> {
                    findNavController().navigate(R.id.action_moviesFragment_to_profileScreen)
                    true
                }
                R.id.navigation_home -> true
                else -> false
            }
        }
        val layoutManagerUpcoming = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewUpcoming.layoutManager = layoutManagerUpcoming
        val layoutManagerNowPlaying = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewNowPlaying.layoutManager = layoutManagerNowPlaying

        // Inflate the layout for this fragment
        loadUpcomingData()
        loadNowPlayingData()
        return view
    }

    private fun loadUpcomingData(){
        val call: Call<MovieResponse> = RetrofitClient.api.getUpcomingMovies()
        call.enqueue(object : Callback<MovieResponse>   {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {

                if(response.isSuccessful){
                    response.body()?.let {
                        upcomingMovies=it.results
                        recyclerViewAdapter = RecyclerHorizontalAdapter(upcomingMovies!!,this@MoviesFragment)
                        recyclerViewUpcoming.adapter = recyclerViewAdapter
                        for(x:MovieResult in upcomingMovies!! ){
                            println(x.title)
                        }
                    }
                }

            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
               println("upcomingdata da datalar gelmiyor ")
                t.printStackTrace()
            }

        })

    }

    private fun loadNowPlayingData(){
        val call : Call<MovieResponse> = RetrofitClient.api.getNowPlayingMovies()
        call.enqueue(object :Callback<MovieResponse>{
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if(response.isSuccessful){
                    response.body()?.let {
                        nowPlayingMovies=it.results
                        recyclerViewAdapter = RecyclerHorizontalAdapter(nowPlayingMovies!!,this@MoviesFragment)
                        recyclerViewNowPlaying.adapter = recyclerViewAdapter
                        for(x:MovieResult in nowPlayingMovies!! ){
                            println(x.title)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                println("nowplayingdata da datalar gelmiyor")
                t.printStackTrace()
            }

        })
    }

    override fun onItemClick(movie: MovieResult) {
        val bundle = Bundle().apply {
            putString("id", "${movie.id}")
        }

        findNavController().navigate(R.id.action_moviesFragment_to_detailsScreen, bundle)
    }


}