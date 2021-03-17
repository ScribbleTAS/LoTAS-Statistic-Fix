package de.pfannekuchen.lotas.mixin.tickratechanger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import de.pfannekuchen.lotas.tickratechanger.TickrateChanger;
import net.minecraft.client.renderer.RenderItem;

@Mixin(RenderItem.class)
public abstract class BindRenderItem {
	
	@ModifyVariable(method = "renderEffect", at = @At("STORE"), index = 2, ordinal = 0)
	public float modifyrenderEffect1(float f) {
		return (getTime() % 3000L) / 3000.0F / 8F;
	}
	
	@ModifyVariable(method = "renderEffect", at = @At("STORE"), index = 3, ordinal = 1)
	public float modifyrenderEffect2(float f) {
		return (getTime() % 4873L) / 4873.0F / 8F;
	}
	
	public long getTime() {
		return TickrateChanger.tickrate == 0 || TickrateChanger.advanceClient ?
				(TickrateChanger.ticksPassed * 50L) :
				(TickrateChanger.ticksPassed * 50L) + ((System.currentTimeMillis() - TickrateChanger.timeOffset) * ((int)TickrateChanger.tickrate / 20L));
	}
	
}