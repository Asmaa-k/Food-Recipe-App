package com.example.foodrecipes.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.repository.RecipeRepository;

public class RecipeIngredientViewModel extends ViewModel {
   private RecipeRepository repository;
   private String mRecipeID;
   private Boolean mDidRetriveRecipe;


    RecipeIngredientViewModel()
    {
        mDidRetriveRecipe = false;
        repository = RecipeRepository.getInstance();
    }

    public String getRecipeID() {
        return mRecipeID;
    }

    public LiveData<Recipe> getRecipe() { return repository.getRecipe(); }

    public LiveData<Boolean> isRequestTimeout() {
        return repository.isRequestTimeout();
    }

    public void searchRecipeById(String rId) {
        mRecipeID = rId;
        repository.searchRecipeById(rId); }

    public Boolean didRetrieveRecipe() {
        return mDidRetriveRecipe;
    }

    public void setRetrieveRecipe(Boolean mDidRetriveRecipe) {
        this.mDidRetriveRecipe = mDidRetriveRecipe;
    }
}
