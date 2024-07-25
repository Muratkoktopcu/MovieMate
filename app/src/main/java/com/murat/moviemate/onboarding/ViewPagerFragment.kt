package com.murat.moviemate.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.murat.moviemate.R
import com.murat.moviemate.onboarding.screens.FirstScrenn
import com.murat.moviemate.onboarding.screens.SecondScreen
import com.murat.moviemate.onboarding.screens.ThirdScreen


class ViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)
       // val viewPager=view.findViewById<ViewPager>(R.id.viewPager)
        val fragmentList= arrayListOf<Fragment>(
            FirstScrenn(),
            SecondScreen(),
            ThirdScreen()
        )
        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )
        view.findViewById<ViewPager2>(R.id.viewPager).adapter=adapter // bu kısmı sor

        return view
    }

}