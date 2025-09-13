package com.playzelo.jackpotmodule.apiservices;

import com.playzelo.jackpotmodule.model.JackpotGameModels;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface JackpotAPIServices {


    // Jackpot game APIs
    @POST("game/create")
    Call<JackpotGameModels.CreateGameResponse> createGame(@Body JackpotGameModels.CreateGameRequest request);

    @POST("game/spin")
    Call<JackpotGameModels.SpinGameResponse> spinGame(@Body JackpotGameModels.SpinGameRequest request);

    @GET("/game/{gameId}")
    Call<JackpotGameModels.GameStatusResponse> getGame(@Path("gameId") String gameId);
}
