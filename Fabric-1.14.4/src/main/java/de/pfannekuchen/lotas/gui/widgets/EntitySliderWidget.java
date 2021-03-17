package de.pfannekuchen.lotas.gui.widgets;

import java.util.HashMap;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;

public class EntitySliderWidget extends AbstractButtonWidget {

	public HashMap<Integer, String> entities;

	private float sliderPosition = 1.0F;
	public boolean isMouseDown;
	private final String name;
	private final float min;
	private final float max;

	public EntitySliderWidget(int xPos, int yPos, HashMap<Integer, String> ent, int width, int height, PressAction c) {
		super(xPos, yPos, width, height, "");
		this.name = "Entity";
		this.entities = ent;
		this.min = 0;
		this.max = ent.size() - 1;
		this.sliderPosition = -min / (max - min);
		this.setMessage("Entity: " + ent.get(0));
	}

	/**
	 * Gets the value of the slider.
	 * 
	 * @return A value that will under normal circumstances be between the slider's
	 *         {@link #min} and {@link #max} values, unless it was manually set out
	 *         of that range.
	 */
	public float getSliderValue() {
		return this.min + (this.max - this.min) * this.sliderPosition;
	}

	/**
	 * Sets the slider's value, optionally notifying the associated
	 * {@linkplain GuiPageButtonList.GuiResponder responder} of the change.
	 */
	public void setSliderValue(float value) {
		this.sliderPosition = (value - this.min) / (this.max - this.min);
		this.setMessage(this.getDisplayString());
	}

	/**
	 * Gets the slider's position.
	 * 
	 * @return The position of the slider, which will under normal circumstances be
	 *         between 0 and 1, unless it was manually set out of that range.
	 */
	public float getSliderPosition() {
		return this.sliderPosition;
	}

	private String getDisplayString() {
		return this.name + ": " + this.getSliderValue();
	}

	@Override
	protected void renderBg(MinecraftClient client, int mouseX, int mouseY) {
		client.getTextureManager().bindTexture(WIDGETS_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int i = (this.isHovered() ? 2 : 1) * 20;
		this.blit(this.x + (int) (this.sliderPosition * (double) (this.width - 8)), this.y, 0, 46 + i, 4, 20);
		this.blit(this.x + (int) (this.sliderPosition * (double) (this.width - 8)) + 4, this.y, 196, 46 + i, 4, 20);
	}

	/**
	 * Fired when the mouse button is dragged. Equivalent of
	 * MouseListener.mouseDragged(MouseEvent e).
	 */
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.visible) {
			if (this.isMouseDown) {
				this.sliderPosition = (float) (mouseX - (this.x + 4)) / (float) (this.width - 8);

				if (this.sliderPosition < 0.0F) {
					this.sliderPosition = 0.0F;
				}

				if (this.sliderPosition > 1.0F) {
					this.sliderPosition = 1.0F;
				}

				setMessage("Entity: " + entities.get((int) Math.round(sliderPosition * (max - min) + min)));
			}

			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			blit(this.x + (int) (this.sliderPosition * (float) (this.width - 8)), this.y, 0, 66, 4, 20);
			blit(this.x + (int) (this.sliderPosition * (float) (this.width - 8)) + 4, this.y, 196, 66, 4, 20);
		}
		return true;
	}

	@Override
	protected int getYImage(boolean isHovered) {
		return 0;
	}

	/**
	 * Sets the position of the slider and notifies the associated
	 * {@linkplain GuiPageButtonList.GuiResponder responder} of the change
	 */
	public void setSliderPosition(float position) {
		this.sliderPosition = position;
		this.setMessage(this.getDisplayString());
	}

	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of
	 * MouseListener.mousePressed(MouseEvent e).
	 */
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int delta) {
		if (super.mouseClicked(mouseX, mouseY, delta)) {
			this.sliderPosition = (float) (mouseX - (this.x + 4)) / (float) (this.width - 8);

			if (this.sliderPosition < 0.0F) {
				this.sliderPosition = 0.0F;
			}

			if (this.sliderPosition > 1.0F) {
				this.sliderPosition = 1.0F;
			}

			this.setMessage("Entity: " + entities.get((int) Math.round(sliderPosition * (max - min) + min)));
			this.isMouseDown = true;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Fired when the mouse button is released. Equivalent of
	 * MouseListener.mouseReleased(MouseEvent e).
	 */
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		this.isMouseDown = false;
		return super.mouseReleased(mouseX, mouseY, button);
	}

}