package com.example.androidtrivial

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OptionsPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> SonidoFragment()
        1 -> EfectosFragment()
        else -> throw IndexOutOfBoundsException("Solo 2 pestañas disponibles")
    }
}
