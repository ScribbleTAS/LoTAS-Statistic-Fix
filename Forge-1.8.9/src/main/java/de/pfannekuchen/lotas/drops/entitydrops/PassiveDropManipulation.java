package de.pfannekuchen.lotas.drops.entitydrops;

import java.util.List;

import com.google.common.collect.ImmutableList;

import de.pfannekuchen.lotas.gui.GuiLootManipulation;
import de.pfannekuchen.lotas.gui.parts.CheckboxWidget;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class PassiveDropManipulation extends GuiLootManipulation.DropManipulation {

    public static GuiCheckBox optimizeChicken = new GuiCheckBox(999, 0, 0, "Optimize Chicken Drops", false);
    public static GuiCheckBox optimizeCow = new GuiCheckBox(999, 0, 0, "Optimize Cow Drops", false);
    public static GuiCheckBox optimizeMooshroom = new GuiCheckBox(999, 0, 0, "Optimize Mooshroom Drops", false);
    public static GuiCheckBox optimizePig = new GuiCheckBox(999, 0, 0, "Optimize Pig Drops", false);
    public static GuiCheckBox optimizeRabbit = new GuiCheckBox(999, 0, 0, "Optimize Rabbit Drops", false);
    public static GuiCheckBox optimizeSheep = new GuiCheckBox(999, 0, 0, "Optimize Sheep Drops", false);
    public static GuiCheckBox optimizeSnowgolem = new GuiCheckBox(999, 0, 0, "Optimize Snowgolem Drops", false);
    public static GuiCheckBox optimizeSquid = new GuiCheckBox(999, 0, 0, "Optimize Squid Drops", false);
    public static GuiCheckBox optimizeHorses = new GuiCheckBox(999, 0, 0, "Optimize All Horse Drops", false);
    public static GuiCheckBox optimizeIronGolem = new GuiCheckBox(999, 0, 0, "Optimize Iron Golem Drops", false);


    public PassiveDropManipulation(int x, int y, int width, int height) {
    	PassiveDropManipulation.x = x;
    	PassiveDropManipulation.y = y;
    	PassiveDropManipulation.width = width;
        PassiveDropManipulation.height = height;
        enabled = new CheckboxWidget(x, y, 150, 20, "Override Passive Mob Drops", false);
    }

    @Override
    public String getName() {
        return "Passive Mobs";
    }

    @Override
    public List<ItemStack> redirectDrops(IBlockState blockstate) { return ImmutableList.of(); }

    @Override
    public List<ItemStack> redirectDrops(Entity entity) {
        if (entity instanceof EntityChicken && optimizeChicken.isChecked()) {
            if (!((EntityLiving) entity).isChild())
                return ImmutableList.of(new ItemStack(Items.feather, 2), entity.isBurning() ? new ItemStack(Items.cooked_chicken) : new ItemStack(Items.chicken));
        } else if (entity instanceof EntityCow && optimizeCow.isChecked()) {
            if (!((EntityLiving) entity).isChild())
                return ImmutableList.of(new ItemStack(Items.leather), entity.isBurning() ? new ItemStack(Items.cooked_beef, 3) : new ItemStack(Items.beef, 3));
        } else if (entity instanceof EntityMooshroom && optimizeMooshroom.isChecked()) {
            if (!((EntityLiving) entity).isChild())
                return ImmutableList.of(new ItemStack(Items.leather, 2), entity.isBurning() ? new ItemStack(Items.cooked_beef, 3) : new ItemStack(Items.beef, 3));
        } else if (entity instanceof EntityPig && optimizePig.isChecked()) {
            if (!((EntityPig) entity).isChild())
                return ImmutableList.of(entity.isBurning() ? new ItemStack(Items.cooked_porkchop, 3) : new ItemStack(Items.porkchop, 3));
        } else if (entity instanceof EntityRabbit && optimizeRabbit.isChecked()) {
            if (!((EntityRabbit) entity).isChild())
               return ImmutableList.of(new ItemStack(Items.rabbit_foot, 1), new ItemStack(Items.rabbit_hide, 1), entity.isBurning() ? new ItemStack(Items.cooked_rabbit) : new ItemStack(Items.rabbit));
        } else if (entity instanceof EntitySheep && optimizeSheep.isChecked()) {
            if (!((EntitySheep) entity).isChild()) 
               return ImmutableList.of(entity.isBurning() ? new ItemStack(Items.cooked_mutton, 3) : new ItemStack(Items.mutton, 3), new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, ((EntitySheep) entity).getFleeceColor().getMetadata()));
        } else if (entity instanceof EntitySnowman && optimizeSnowgolem.isChecked()) {
            if (!((EntitySnowman) entity).isChild())
                return ImmutableList.of(new ItemStack(Items.snowball, 15));
        } else if (entity instanceof EntitySquid && optimizeSquid.isChecked()) {
            if (!((EntitySquid) entity).isChild())
                return ImmutableList.of(new ItemStack(Items.dye, 3, 0));
        } else if ((entity instanceof EntityHorse) && optimizeHorses.isChecked()) {
            if (!((EntityHorse) entity).isChild())
                return ImmutableList.of(new ItemStack(Items.leather, 2));
        } else if (entity instanceof EntityIronGolem && optimizeIronGolem.isChecked()) {
            if (!((EntityIronGolem) entity).isChild())
                return ImmutableList.of(new ItemStack(Items.iron_ingot, 5), new ItemStack(Blocks.red_flower));
        }
        return ImmutableList.of();
    }

    @Override
    public void update() {
        enabled.xPosition = x;
        enabled.yPosition = y;
        optimizeChicken.yPosition = 64;
        optimizeMooshroom.yPosition = 112;
        optimizeCow.yPosition = 128;
        optimizePig.yPosition = 144;
        optimizeRabbit.yPosition = 176;
        optimizeSnowgolem.yPosition = 192;
        optimizeSheep.yPosition = 208;
        optimizeSquid.yPosition = 160;
        optimizeHorses.yPosition = 80;
        optimizeIronGolem.yPosition = 96;
        optimizeChicken.xPosition = x;
        optimizeMooshroom.xPosition = x;
        optimizeCow.xPosition = x;
        optimizePig.xPosition = x;
        optimizeRabbit.xPosition = x;
        optimizeSnowgolem.xPosition = x;
        optimizeSheep.xPosition = x;
        optimizeSquid.xPosition = x;
        optimizeHorses.xPosition = x;
        optimizeIronGolem.xPosition = x;
    }

    @Override
    public void mouseAction(int mouseX, int mouseY, int button) {
        enabled.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
        if (enabled.isChecked()) {
            optimizeChicken.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizeCow.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizeMooshroom.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizePig.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizeSheep.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizeRabbit.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizeSnowgolem.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizeSquid.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizeHorses.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizeIronGolem.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        enabled.render(mouseX, mouseY, delta);

        if (!enabled.isChecked()) {
            GlStateManager.color(.5f, .5f, .5f, .4f);
        } else {
            optimizeChicken.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizeCow.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizeMooshroom.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizePig.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizeSnowgolem.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizeSheep.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizeRabbit.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizeSquid.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizeIronGolem.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
            optimizeHorses.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("lotas", "drops/sheep.png"));
        Gui.drawModalRectWithCustomSizedTexture(width - 128, y + 24, 0.0F, 0.0F, 102, 120, 102, 120);
    }

}
