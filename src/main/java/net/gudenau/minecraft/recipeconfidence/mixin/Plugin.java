package net.gudenau.minecraft.recipeconfidence.mixin;

import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import net.gudenau.minecraft.recipeconfidence.BclibFlag;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

// bclib breaks this mod and they don't want to fix the compatibility issues, so we have to do it ourselves.
public final class Plugin implements IMixinConfigPlugin{
    private static final boolean bclibPresent;
    
    static{
        bclibPresent = FabricLoader.getInstance().isModLoaded("bclib");
        if(bclibPresent && !BclibFlag.bclibCompatValidated()){
            var logger = LogManager.getLogger("bclib");
            ("\n".repeat(4) + """
            bclib is incompatible with RecipeConfidence.
            
            There is a chance that the compatibility layer is not correct, issues may arise
            when using recipes (crafting, smelting, etc.) because of the conflict.
            
            If you are the authors of bclib and have validated the compatibility layer, feel
            free to make a mixin that changes BclibFlag.bclibCompatValidated to return true.
            """ + "\n".repeat(4)).lines().forEach(logger::error);
        }
    }
    
    @Override
    public void onLoad(String mixinPackage){}
    
    @Override
    public String getRefMapperConfig(){
        return null;
    }
    
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName){
        if(bclibPresent && mixinClassName.endsWith("RecipeManagerMixin")){
            return false;
        }else{
            return !mixinClassName.endsWith("BclibRecipeManagerMixin");
        }
    }
    
    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets){}
    
    @Override
    public List<String> getMixins(){
        return List.of();
    }
    
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo){}
    
    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo){}
}
