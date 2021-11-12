package net.gudenau.minecraft.recipeconfidence.mixin;

import java.util.Optional;
import java.util.stream.Stream;
import net.gudenau.minecraft.recipeconfidence.duck.RecipeDuck;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin{
    @Redirect(
        method = "getFirstMatch",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/stream/Stream;findFirst()Ljava/util/Optional;"
        )
    )
    private <C extends Inventory, T extends Recipe<C>> Optional<T> getFirstMatch(Stream<T> stream){
        return stream.min((recipeA, recipeB)->{
            var confidenceA = ((RecipeDuck)recipeA).gud_recipe_confidence$getConfidence();
            var confidenceB = ((RecipeDuck)recipeB).gud_recipe_confidence$getConfidence();
            int result = Float.compare(confidenceA, confidenceB);
            if(result != 0){
                return result;
            }
            return recipeA.getOutput().getTranslationKey().compareTo(recipeB.getOutput().getTranslationKey());
        });
    }
}
