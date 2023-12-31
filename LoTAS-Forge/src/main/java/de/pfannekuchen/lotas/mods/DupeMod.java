package de.pfannekuchen.lotas.mods;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.pfannekuchen.lotas.core.MCVer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

/**
 * Dupe Mod for Pre 1.14.
 * @author Pancake
 * @since v1.0
 * @version v1.0
 */
public class DupeMod {
	/** List of all items saved */
	public static List<EntityItem> items;
	/** List of all tile entities saved */
	public static List<TileEntity> tileentities;
	/** List of all tracked items on the floor */
	public static ArrayList<EntityItem> trackedObjects = new ArrayList<>();
	
	/**
	 * Restores all nearby chests from the list
	 */
	public static synchronized void loadChests() {
		try {
			Minecraft mc = Minecraft.getMinecraft();
			if (DupeMod.tileentities == null) return;
			for (TileEntity tileentity : DupeMod.tileentities) {
				MCVer.world(mc.getIntegratedServer(), MCVer.player(mc).dimension).getTileEntity(tileentity.getPos()).deserializeNBT(((TileEntityChest) tileentity).serializeNBT());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Restores all items on the ground from the list
	 */
	public static synchronized void loadItems() {
		try {
			Minecraft mc = Minecraft.getMinecraft();
			
			// reset motion because we are actually "crashing" the game
			resetMotion(mc);
			
			if (DupeMod.items != null) {
				if (!DupeMod.items.isEmpty()) {
			        mc.displayGuiScreen((GuiScreen)null);
			        mc.setIngameFocus();
					for (Entity entity : new ArrayList<Entity>(MCVer.world(mc.getIntegratedServer(), MCVer.player(mc).dimension).loadedEntityList)) {
						if (entity instanceof EntityItem) MCVer.world(mc.getIntegratedServer(), MCVer.player(mc).dimension).removeEntity(entity);
					}
					for (EntityItem item : DupeMod.items) {
						World w = MCVer.world(Minecraft.getMinecraft().getIntegratedServer(), MCVer.player(Minecraft.getMinecraft()).dimension);
						//#if MC>=11200
						EntityItem itemDupe = new EntityItem(w, item.posX, item.posY, item.posZ, item.getItem().copy());
						//#else
//$$ 						EntityItem itemDupe = new EntityItem(w, item.posX, item.posY, item.posZ, item.getEntityItem().copy());
						//#endif
						itemDupe.motionX = item.motionX;
						itemDupe.motionY = item.motionY;
						itemDupe.motionZ = item.motionZ;
						itemDupe.setAgeToCreativeDespawnTime();
						itemDupe.setOwner(MCVer.player(mc).getName());
						itemDupe.setNoPickupDelay(); // does not get saved sometimes... very weird
						itemDupe.rotationYaw = item.rotationYaw;
						itemDupe.rotationPitch = item.rotationPitch;
						
						//#if MC>=11100
						w.spawnEntity(itemDupe);
						//#else
//$$ 						w.spawnEntityInWorld(itemDupe);
						//#endif
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * Save all items on the ground to a List
	 */
	public static synchronized void saveItems() {
		try {
			Minecraft mc = Minecraft.getMinecraft();
			double pX = MCVer.player(mc).posX;
			double pY = MCVer.player(mc).posY;
			double pZ = MCVer.player(mc).posZ;
			
			// relog
			resetMotion(mc);
			
			DupeMod.items = new LinkedList<EntityItem>();
			
			for (EntityItem item : MCVer.world(mc.getIntegratedServer(), MCVer.player(mc).dimension).getEntitiesWithinAABB(EntityItem.class, MCVer.aabb(pX - 16, pY - 16, pZ - 16, pX + 16, pY + 16, pZ + 16))) {
				//#if MC>=11200
				EntityItem itemDupe = new EntityItem(item.world, item.posX, item.posY, item.posZ, item.getItem().copy());
				//#else
				//#if MC>=11100
//$$ 				EntityItem itemDupe = new EntityItem(item.world, item.posX, item.posY, item.posZ, item.getEntityItem().copy());
				//#else
//$$ 				EntityItem itemDupe = new EntityItem(item.worldObj, item.posX, item.posY, item.posZ, item.getEntityItem().copy());
				//#endif
				//#endif
				itemDupe.motionX = item.motionX;
				itemDupe.motionY = item.motionY;
				itemDupe.motionZ = item.motionZ;
				itemDupe.rotationYaw = item.rotationYaw;
				itemDupe.rotationPitch = item.rotationPitch;
				DupeMod.items.add(itemDupe);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves all chests around the player to a file
	 */
	public static synchronized void saveChests() {
		try {
			Minecraft mc = Minecraft.getMinecraft();
			World world = MCVer.world(mc.getIntegratedServer(), MCVer.player(mc).dimension);
			
			DupeMod.tileentities = new LinkedList<TileEntity>();
			for (int x =- 5; x <= 5; x++) {
				for (int y =- 5; y <= 5; y++) {
					for (int z =- 5; z <= 5; z++) {
						if (MCVer.world(mc.getIntegratedServer(), MCVer.player(mc).dimension).getBlockState(MCVer.player(mc).getPosition().add(x, y, z)).getBlock() == MCVer.getBlock("CHEST") || world.getBlockState(MCVer.player(mc).getPosition().add(x, y, z)).getBlock() == MCVer.getBlock("TRAPPED_CHEST")) {
							TileEntityChest foundchest = (TileEntityChest) world.getTileEntity(MCVer.player(mc).getPosition().add(x,y,z));
							TileEntityChest newchest = new TileEntityChest(((TileEntityChest) world.getTileEntity(MCVer.player(mc).getPosition().add(x,y,z))).getChestType());
							newchest.deserializeNBT(foundchest.serializeNBT());
							tileentities.add(newchest);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Resets the motion
	 * @param mc
	 */
	private static void resetMotion(Minecraft mc) {
		MCVer.player(mc).motionX = 0;
		MCVer.player(mc).motionY = 0;
		MCVer.player(mc).motionZ = 0;
	}
	
}