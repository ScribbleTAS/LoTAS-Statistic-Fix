package de.pfannekuchen.lotas.gui;

import java.util.List;

import com.google.common.base.Predicates;

import de.pfannekuchen.lotas.gui.widgets.NewButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.LiteralText;

public class AIManipulationScreen extends Screen {

	public AIManipulationScreen() {
		super(new LiteralText(""));
	}

	public static int selectedIndex = 0;
	public static List<MobEntity> entities;

	public TextFieldWidget xText;
	public TextFieldWidget yText;
	public TextFieldWidget zText;

	public static int spawnX = (int) MinecraftClient.getInstance().player.x;
	public static int spawnY = (int) MinecraftClient.getInstance().player.y;
	public static int spawnZ = (int) MinecraftClient.getInstance().player.z;

	@Override
	public void init() {
		addButton(new NewButtonWidget(5, 5, 98, 20, "<", btn -> {
			selectedIndex--;
			btn.active = selectedIndex != 0;
			buttons.get(1).active = selectedIndex != entities.size() - 1;
		}));
		addButton(new NewButtonWidget(width - 5 - 98, 5, 98, 20, ">", button -> {
			selectedIndex++;
			button.active = selectedIndex != entities.size() - 1;
			buttons.get(0).active = selectedIndex != 0;
		}));

		xText = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, width / 2 - 100, height - 50, 60, 20, spawnX + "");
		yText = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, width / 2 - 30, height - 50, 60, 20, spawnY + "");
		zText = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, width / 2 + 40, height - 50, 60, 20, spawnZ + "");

		addButton(new NewButtonWidget(width / 2 - 100, height - 25, 200, 20, "Change Target", button -> {
			button.active = !entities.get(selectedIndex).getNavigation().startMovingTo(spawnX, spawnY, spawnZ, 1.0f);
		}));

		addButton(new NewButtonWidget(width / 2 - 100, height - 72, 60, 20, "X++", btn -> spawnX++));
		addButton(new NewButtonWidget(width / 2 - 100, height - 94, 60, 20, "X--", btn -> spawnX--));
		addButton(new NewButtonWidget(width / 2 - 30, height - 72, 60, 20, "Y++", btn -> spawnY++));
		addButton(new NewButtonWidget(width / 2 - 30, height - 94, 60, 20, "Y--", btn -> spawnY--));
		addButton(new NewButtonWidget(width / 2 + 40, height - 72, 60, 20, "Z++", btn -> spawnZ++));
		addButton(new NewButtonWidget(width / 2 + 40, height - 94, 60, 20, "Z--", btn -> spawnZ--));
		addButton(new NewButtonWidget(width / 2 - 100, height - 116, 200, 20, "Move to me", btn -> {
			spawnX = (int) minecraft.player.x;
			spawnY = (int) minecraft.player.y;
			spawnZ = (int) minecraft.player.z;
			xText.setText(spawnX + "");
			yText.setText(spawnY + "");
			zText.setText(spawnZ + "");
		}));
		addButton(new NewButtonWidget(width / 2 - 100, height - 138, 200, 20, "Move to entity", btn -> {
			try {
				spawnX = (int) entities.get(selectedIndex).x;
				spawnY = (int) entities.get(selectedIndex).y;
				spawnZ = (int) entities.get(selectedIndex).z;
				xText.setText(spawnX + "");
				yText.setText(spawnY + "");
				zText.setText(spawnZ + "");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}));
		entities = minecraft.getServer().getWorld(MinecraftClient.getInstance().player.dimension).getEntities(MobEntity.class, minecraft.player.getBoundingBox().expand(32, 32, 32), Predicates.alwaysTrue());
		selectedIndex = 0;

		if (selectedIndex + 2 > entities.size()) {
			buttons.get(1).active = false;
		} else {
			buttons.get(1).active = true;
		}

		if (selectedIndex - 1 < 0) {
			buttons.get(0).active = false;
		} else {
			buttons.get(0).active = true;
		}
		super.init();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		int prev = selectedIndex;

		buttons.get(2).active = true;

		boolean i = super.mouseClicked(mouseX, mouseY, mouseButton);

		xText.setText(spawnX + "");
		yText.setText(spawnY + "");
		zText.setText(spawnZ + "");

		if (prev != selectedIndex) {
			try {
				spawnX = entities.get(selectedIndex).getNavigation().getCurrentPath().getEnd().x;
				spawnY = entities.get(selectedIndex).getNavigation().getCurrentPath().getEnd().y;
				spawnZ = entities.get(selectedIndex).getNavigation().getCurrentPath().getEnd().z;

				xText.setText(spawnX + "");
				yText.setText(spawnY + "");
				zText.setText(spawnZ + "");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (selectedIndex + 2 > entities.size()) {
			buttons.get(1).active = false;
		} else {
			buttons.get(1).active = true;
		}

		if (selectedIndex - 1 < 0) {
			buttons.get(0).active = false;
		} else {
			buttons.get(0).active = true;
		}

		xText.mouseClicked(mouseX, mouseY, mouseButton);
		yText.mouseClicked(mouseX, mouseY, mouseButton);
		zText.mouseClicked(mouseX, mouseY, mouseButton);
		return i;
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		if (Character.isDigit(typedChar) || !Character.isLetter(typedChar)) {
			xText.charTyped(typedChar, keyCode);
			yText.charTyped(typedChar, keyCode);
			zText.charTyped(typedChar, keyCode);
		}
		try {
			spawnX = Integer.parseInt(xText.getText());
			spawnY = Integer.parseInt(yText.getText());
			spawnZ = Integer.parseInt(zText.getText());
			buttons.get(2).active = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.charTyped(typedChar, keyCode);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);
		if (entities.size() == 0)
			return;
		xText.render(mouseX, mouseY, partialTicks);
		yText.render(mouseX, mouseY, partialTicks);
		zText.render(mouseX, mouseY, partialTicks);
		drawCenteredString(MinecraftClient.getInstance().textRenderer, entities.get(selectedIndex).getClass().getSimpleName().replaceFirst("Entity", "") + " (" + entities.get(selectedIndex).x + ", " + entities.get(selectedIndex).y + ", " + entities.get(selectedIndex).z + ")", width / 2, 5, 0xFFFFFF);
	}

}
