package com.example.padellex.api

import com.example.padellex.api.model.RatingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PlayerRatingService {

    @GET("/tennis_analysis")
   suspend fun getPlayerRating(@Query("public_id")  videoPublicId : String) : RatingResponse
}