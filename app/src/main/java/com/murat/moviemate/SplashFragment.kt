package com.murat.moviemate

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth


class SplashFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Handler().postDelayed({
            if(onBoardingFinished()){
                val user = FirebaseAuth.getInstance().currentUser
                if(user == null)
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                else
                    findNavController().navigate(R.id.action_splashFragment_to_moviesFragment)
            }else{
                findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
            }
        }, 3000)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
    private fun onBoardingFinished():Boolean{
        val sharedPref = requireActivity().getSharedPreferences("onBoarding",Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished",false)
    }
}