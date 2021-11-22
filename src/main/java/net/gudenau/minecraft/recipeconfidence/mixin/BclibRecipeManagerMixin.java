package net.gudenau.minecraft.recipeconfidence.mixin;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.gudenau.minecraft.recipeconfidence.duck.RecipeDuck;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RecipeManager.class, priority = 999)
public abstract class BclibRecipeManagerMixin{
    @Shadow protected abstract <C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> getAllOfType(RecipeType<T> type);
    
    @Inject(
        method = "getFirstMatch",
        at = @At("HEAD"),
        cancellable = true
    )
    private <C extends Inventory, T extends Recipe<C>> void getFirstMatch(RecipeType<T> type, C inventory, World world, CallbackInfoReturnable<Optional<T>> cir){
        cir.setReturnValue(getAllOfType(type).values().stream()
            .flatMap((recipe)->Util.stream(type.match(recipe, world, inventory)))
            .min((recipeA, recipeB)->{
                var confidenceA = ((RecipeDuck)recipeA).gud_recipe_confidence$getConfidence();
                var confidenceB = ((RecipeDuck)recipeB).gud_recipe_confidence$getConfidence();
                int result = Float.compare(confidenceA, confidenceB);
                if(result != 0){
                    return result;
                }
    
                // bclib compat code start
                boolean iaAVanilla = recipeA.getId().getNamespace().equals(Identifier.DEFAULT_NAMESPACE);
                boolean iaBVanilla = recipeB.getId().getNamespace().equals(Identifier.DEFAULT_NAMESPACE);
                if(iaAVanilla != iaBVanilla){
                    return iaAVanilla ? 1 : -1;
                }
                // bclib compat code end
                
                return recipeA.getOutput().getTranslationKey().compareTo(recipeB.getOutput().getTranslationKey());
            })
        );
    }
}
