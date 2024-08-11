package com.murat.moviemate.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.murat.moviemate.R
import com.murat.moviemate.model.MovieData
import com.murat.moviemate.model.MovieResult
import com.murat.moviemate.service.MovieAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecyclerVerticalAdapter(
    private val movieList: List<HashMap<String, Any>>,
    private val listener: Listener
) : RecyclerView.Adapter<RecyclerVerticalAdapter.ViewHolder>() {

    interface Listener {
        fun onItemClick(movie: HashMap<String, Any>)
    }
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

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val movieImageView: ImageView = view.findViewById(R.id.posterV)
        private val movieTitle: TextView = view.findViewById(R.id.titleV)
        private val movieYear: TextView = view.findViewById(R.id.dateV)
        private val vote : TextView = view.findViewById(R.id.voteV)

        fun bind(movie: HashMap<String, Any>) {
            val id = movie["id"] as? String

            if (id != null) {
                val call: Call<MovieData> = RetrofitClient.api.getMovieDetails(id)
                call.enqueue(object : Callback<MovieData> {
                    override fun onResponse(call: Call<MovieData>, response: Response<MovieData>) {
                        if(response.isSuccessful){
                            response.body()?.let {
                                Glide.with(itemView.context)
                                    .load("https://image.tmdb.org/t/p/w500${it.posterPath}")
                                    .into(movieImageView)
                                movieTitle.text = it.title
                                movieYear.text = it.releaseDate
                                vote.text=it.voteAverage.toString()
                            }
                        }
                    }

                    override fun onFailure(call: Call<MovieData>, t: Throwable) {
                        Toast.makeText(itemView.context, "Failed to load movie details.", Toast.LENGTH_SHORT).show()
                    }

                })
            }

            itemView.setOnClickListener { listener.onItemClick(movie) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_vertical_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movieList[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int {
        return movieList.size
    }
}