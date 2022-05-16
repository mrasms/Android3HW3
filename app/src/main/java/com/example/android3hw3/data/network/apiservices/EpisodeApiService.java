package com.example.android3hw3.data.network.apiservices;

import com.example.android3hw3.models.EpisodeModel;
import com.example.android3hw3.models.RickAndMortyResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EpisodeApiService {

    @GET("api/episode")
    Call<RickAndMortyResponse<EpisodeModel>> fetchEpisodes();

}