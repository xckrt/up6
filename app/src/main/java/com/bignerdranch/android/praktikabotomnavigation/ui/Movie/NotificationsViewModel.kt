package com.bignerdranch.android.praktikabotomnavigation.ui.Movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import Models.Movie
import retrofit2.Call
import retrofit2.http.GET

class NotificationsViewModel : ViewModel() {

    var selectedMovie: Movie  = Movie(0,"",0.0, "")
    var lastSearchQuery: String? = null
}