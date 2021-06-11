package de.pfannekuchen.lotas.mixin.server;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import de.pfannekuchen.lotas.core.utils.ConfigUtils;
import de.pfannekuchen.lotas.gui.GuiDropChanceManipulation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.world.World;

@Mixin(Block.class)
public class MixinDropBlock {

    @Inject(method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/loot/context/LootContext$Builder;)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
    public void redodrop(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> drops) {
        for (GuiDropChanceManipulation.DropManipulation man : GuiDropChanceManipulation.manipulations) {
            if (!man.enabled.isChecked()) continue;
            List<ItemStack> list = man.redirectDrops(state);
            if (!list.isEmpty()) {
                drops.setReturnValue(list);
                drops.cancel();
            }
        }
    }
    
    @Redirect(method = "dropStack", at = @At(value = "NEW", target = "Lnet/minecraft/entity/ItemEntity;<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;"))
	private static ItemEntity haxInit(World w, double x, double y, double z, ItemStack stack) {
    	ItemEntity it = new ItemEntity(w, x, y, z, stack);
		try {
			if (ConfigUtils.getBoolean("tools", "manipulateVelocityTowards")) {
				double pX = MinecraftClient.getInstance().player.x - x;
				double pZ = MinecraftClient.getInstance().player.z - z;
				it.setVelocity(Math.min(Math.max(Math.round(pX), 1), -1) * 0.03f, it.getVelocity().y, Math.max(Math.round(pZ), 1) * 0.03f);
			} else if (ConfigUtils.getBoolean("tools", "manipulateVelocityAway")) {
				double pX = MinecraftClient.getInstance().player.x - x;
				double pZ = MinecraftClient.getInstance().player.z - z;
				it.setVelocity(Math.min(Math.max(Math.round(pX), 1), -1) * -0.03f, it.getVelocity().y, Math.max(Math.round(pZ), 1) * -0.03f);
			}
		} catch (Exception e) {
			// Ignore this Error
		}
		return it;
	}
	

}
