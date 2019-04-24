package com.example.foodrecipes.requests;

import com.example.foodrecipes.utill.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    private static Retrofit.Builder retrofit_builder = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofit_builder.build();

   private static RecipeApi recipeApi = retrofit.create(RecipeApi.class);

   public static RecipeApi getRecipeApi()//to access in the another activity
   {
       return recipeApi;
   }
}
