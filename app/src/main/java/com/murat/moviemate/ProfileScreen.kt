package com.murat.moviemate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView


class ProfileScreen : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_profile_screen, container, false)
        // GoogleSignInClient'i başlatın
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        val signOutButton: Button = view.findViewById(R.id.signOutButton)

        signOutButton.setOnClickListener {
            signOut()
        }
        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottom_navigation_profile)

        bottomNavigationView.selectedItemId = R.id.navigation_profile

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    findNavController().navigate(R.id.action_profileScreen_to_moviesFragment)
                    true
                }
                R.id.navigation_watch -> {
                    findNavController().navigate(R.id.action_profileScreen_to_watchlistScreen)
                    true
                }
                R.id.navigation_fav -> {
                    findNavController().navigate(R.id.action_profileScreen_to_favoritesScreen)
                    true
                }
                R.id.navigation_profile -> true
                else -> false
            }
        }

        fetchUserInfo(view)

        // Inflate the layout for this fragment
        return view
    }
    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener(requireActivity()) { task: Task<Void?> ->
            if (task.isSuccessful) {
                // Oturum kapatıldı, gerekli işlemleri yapabilirsiniz.
                findNavController().navigate(R.id.action_profileScreen_to_homeFragment)
            } else {
                // Bir hata oluştu, kullanıcıya hata mesajı gösterebilirsiniz.
            }
        }
    }
    private fun fetchUserInfo(view: View) {
        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(requireContext())

        if (account != null) {
            // Kullanıcı bilgilerini al
            val userName: String = account.displayName ?: "Unknown"
            val userEmail: String = account.email ?: "Unknown"
            val userPhotoUrl = account.photoUrl

            // TextView'leri güncelle
            val nameTextView: TextView = view.findViewById(R.id.name_surname)
            val emailTextView: TextView = view.findViewById(R.id.emailText)
            val profileImageView: ImageView = view.findViewById(R.id.profileImage)

            nameTextView.text = userName
            emailTextView.text = userEmail

            // Profil fotoğrafını yükle
            userPhotoUrl?.let {
                // Picasso veya Glide gibi bir kütüphane kullanarak resmi yükleyin
                Glide.with(this).load(it).into(profileImageView)
            }
        }
    }

}