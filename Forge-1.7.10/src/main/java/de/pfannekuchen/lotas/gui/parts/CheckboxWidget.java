package de.pfannekuchen.lotas.gui.parts;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.MathHelper;
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
		GL11.glEnable(GL11.GL_DEPTH);
		FontRenderer textRenderer = minecraftClient.fontRendererObj;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0f);
		GL11.glEnable(GL11.GL_BLEND);
//		GL11.gl(770, 771, 1,
//				0);
		GL11.glBlendFunc(770, 771);
		minecraftClient.getTextureManager().bindTexture(TEXTURE);
		drawModalRectWithCustomSizedTexture(this.xPosition, this.yPosition, 0.0F, this.checked ? 20.0F : 0.0F, 20, this.height, 32, 64);
		this.drawString(textRenderer, this.displayString, this.xPosition + 24, this.yPosition + (this.height - 8) / 2,
				14737632 | MathHelper.ceiling_float_int(1.0f * 255.0F) << 24);
	}
	
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		boolean b = super.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
		if (b) {
			onPress();
//			sound(Minecraft.getMinecraft().getSoundHandler());
		}
		return b;
	}

	public void render(int mouseX, int mouseY, float delta) {
		drawButton(Minecraft.getMinecraft(), mouseX, mouseY, delta);
	}
}