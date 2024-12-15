package com.example.appmenumakanan.Network

import com.example.appmenumakanan.Model.MenuApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Body
import retrofit2.http.Path

interface ApiService {

    @GET("zHuuM/menuAeyin")
    suspend fun getMenus(): Response<List<MenuApiResponse>>

    @POST("zHuuM/menuAeyin")
    suspend fun addMenu(@Body menu: MenuApiResponse):
            Response<MenuApiResponse>

    @PUT("zHuuM/menuAeyin/{id}")
    suspend fun updateMenu(@Path("id") id: Int, @Body menu:
    MenuApiResponse): Response<MenuApiResponse>

    @DELETE("zHuuM/menuAeyin/{id}")
    suspend fun deleteMenu(@Path("id") id: Int): Response<Unit>
}