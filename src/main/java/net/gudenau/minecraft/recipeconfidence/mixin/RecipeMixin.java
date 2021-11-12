package net.gudenau.minecraft.recipeconfidence.mixin;

import net.gudenau.minecraft.recipeconfidence.duck.IngredientDuck;
import net.gudenau.minecraft.recipeconfidence.duck.RecipeDuck;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Recipe.class)
public interface RecipeMixin extends RecipeDuck{
    @Shadow DefaultedList<Ingredient> getIngredients();
    
    @Override
    @Unique
    default float gud_recipe_confidence$getConfidence(){
        @SuppressWarnings("unchecked")
        var ingredients = (DefaultedList<IngredientDuck>)(Object)getIngredients();
        int fuzzyCount = 0;
        for(var ingredient : ingredients){
            if(ingredient.gud_recipe_confidence$isFuzzy()){
                fuzzyCount++;
            }
        }
        return fuzzyCount / (float)ingredients.size();
    }
}
