package de.pfannekuchen.lotas.mixin.render.binds;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import de.pfannekuchen.lotas.mods.TickrateChangerMod;
import net.minecraft.client.gui.components.SubtitleOverlay;

/**
 * Slows down the Subtitles
 * @author ScribbleLP
 */
@Mixin(SubtitleOverlay.class)
public abstract class MixinTickrateChangerSubtitleOverlay {

	//#if MC>=1.19.4
	@ModifyConstant(method = "render", constant = @Constant(doubleValue = 3000D))
	public double applyTickrate2(double threethousand) {
		float multiplier = TickrateChangerMod.tickrate == 0 ? 20F / TickrateChangerMod.tickrateSaved : 20F / TickrateChangerMod.tickrate;
		return threethousand * multiplier;
	}
	//#else
//$$ 	@ModifyConstant(method = "render", constant = @Constant(longValue = 3000L))
//$$ 	public long applyTickrate(long threethousand) {
//$$ 		float multiplier = TickrateChangerMod.tickrate == 0 ? 20F / TickrateChangerMod.tickrateSaved : 20F / TickrateChangerMod.tickrate;
//$$ 		return (long) (threethousand * multiplier);
//$$ 	}
//$$
//$$
//$$ 	@ModifyConstant(method = "render", constant = @Constant(floatValue = 3000F))
//$$ 	public float applyTickrate2(float threethousand) {
//$$ 		float multiplier = TickrateChangerMod.tickrate == 0 ? 20F / TickrateChangerMod.tickrateSaved : 20F / TickrateChangerMod.tickrate;
//$$ 		return threethousand * multiplier;
//$$ 	}
	//#endif
}
