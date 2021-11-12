package net.gudenau.minecraft.recipeconfidence.mixin;

import net.gudenau.minecraft.recipeconfidence.duck.IngredientDuck;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Ingredient.class)
public abstract class IngredientMixin implements IngredientDuck{
	
	@Shadow private ItemStack[] matchingStacks;
	@Unique @Mutable private boolean gud_recipe_confidence$fuzzy;
	
	@Inject(
		method = "cacheMatchingStacks",
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "Ljava/util/stream/Stream;toArray(Ljava/util/function/IntFunction;)[Ljava/lang/Object;",
			shift = At.Shift.BY,
			by = 2
		)
	)
	private void init(CallbackInfo ci){
		gud_recipe_confidence$fuzzy = matchingStacks.length > 1;
	}
	
	@Unique
	@Override
	public boolean gud_recipe_confidence$isFuzzy(){
		return gud_recipe_confidence$fuzzy;
	}
}
