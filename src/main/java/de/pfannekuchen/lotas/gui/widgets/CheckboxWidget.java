package de.pfannekuchen.lotas.gui.widgets;

import de.pfannekuchen.lotas.core.MCVer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
//#if MC>=10900
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
//#endif
import net.minecraft.util.ResourceLocation;


public class CheckboxWidget extends GuiButton {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
	boolean checked;

	public CheckboxWidget(int x, int y, int width, int height, String message, boolean checked) {
		super(999, x, y, width, height, message);
		this.checked = checked;
	}

	public void onPress() {
		this.checked = !this.checked;
	}

	public boolean isChecked() {
		return this.checked;
	}

	public void drawButton(Minecraft minecraftClient, int mouseX, int mouseY, float delta) {
		GlStateManager.enableDepth();
		FontRenderer textRenderer = MCVer.getFontRenderer(minecraftClient);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0f);
		GlStateManager.enableBlend();
		//#if MC>=10900
		GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		//#else
//$$ 		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
//$$ 		GlStateManager.blendFunc(770, 771);
		//#endif
		minecraftClient.getTextureManager().bindTexture(TEXTURE);
		drawModalRectWithCustomSizedTexture(MCVer.x(this), MCVer.y(this), 0.0F, this.checked ? 20.0F : 0.0F, 20, this.height, 32, 64);
		this.drawString(textRenderer, this.displayString, MCVer.x(this) + 24, MCVer.y(this) + (this.height - 8) / 2, 14737632 | MCVer.ceil(1.0f * 255.0F) << 24);
	}
	
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		boolean b = super.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
		if (b) {
			onPress();
			playPressSound(Minecraft.getMinecraft().getSoundHandler());
		}
		return b;
	}

	public void render(int mouseX, int mouseY, float delta) {
		drawButton(Minecraft.getMinecraft(), mouseX, mouseY, delta);
	}
}