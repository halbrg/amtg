package com.halbrg.amtg;

import com.google.gson.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AMTG
{
	public static final Logger LOGGER = LoggerFactory.getLogger("AMTG");
	public static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.create();

	protected static final Map<String, Material> MATERIAL_MAP;
	static {
		MATERIAL_MAP = new HashMap<>();
		MATERIAL_MAP.put("air", Material.AIR);
		MATERIAL_MAP.put("structure_void", Material.STRUCTURE_VOID);
		MATERIAL_MAP.put("portal", Material.PORTAL);
		MATERIAL_MAP.put("carpet", Material.CARPET);
		MATERIAL_MAP.put("plant", Material.PLANT);
		MATERIAL_MAP.put("underwater_plant", Material.UNDERWATER_PLANT);
		MATERIAL_MAP.put("replaceable_plant", Material.REPLACEABLE_PLANT);
		MATERIAL_MAP.put("nether_shoots", Material.NETHER_SHOOTS);
		MATERIAL_MAP.put("replaceable_underwater_plant", Material.REPLACEABLE_UNDERWATER_PLANT);
		MATERIAL_MAP.put("water", Material.WATER);
		MATERIAL_MAP.put("bubble_column", Material.BUBBLE_COLUMN);
		MATERIAL_MAP.put("lava", Material.LAVA);
		MATERIAL_MAP.put("snow_layer", Material.SNOW_LAYER);
		MATERIAL_MAP.put("fire", Material.FIRE);
		MATERIAL_MAP.put("decoration", Material.DECORATION);
		MATERIAL_MAP.put("cobweb", Material.COBWEB);
		MATERIAL_MAP.put("sculk", Material.SCULK);
		MATERIAL_MAP.put("redstone_lamp", Material.REDSTONE_LAMP);
		MATERIAL_MAP.put("organic_product", Material.ORGANIC_PRODUCT);
		MATERIAL_MAP.put("soil", Material.SOIL);
		MATERIAL_MAP.put("solid_organic", Material.SOLID_ORGANIC);
		MATERIAL_MAP.put("dense_ice", Material.DENSE_ICE);
		MATERIAL_MAP.put("aggregate", Material.AGGREGATE);
		MATERIAL_MAP.put("sponge", Material.SPONGE);
		MATERIAL_MAP.put("shulker_box", Material.SHULKER_BOX);
		MATERIAL_MAP.put("wood", Material.WOOD);
		MATERIAL_MAP.put("nether_wood", Material.NETHER_WOOD);
		MATERIAL_MAP.put("bamboo_sapling", Material.BAMBOO_SAPLING);
		MATERIAL_MAP.put("bamboo", Material.BAMBOO);
		MATERIAL_MAP.put("wool", Material.WOOL);
		MATERIAL_MAP.put("tnt", Material.TNT);
		MATERIAL_MAP.put("leaves", Material.LEAVES);
		MATERIAL_MAP.put("glass", Material.GLASS);
		MATERIAL_MAP.put("ice", Material.ICE);
		MATERIAL_MAP.put("cactus", Material.CACTUS);
		MATERIAL_MAP.put("stone", Material.STONE);
		MATERIAL_MAP.put("metal", Material.METAL);
		MATERIAL_MAP.put("snow_block", Material.SNOW_BLOCK);
		MATERIAL_MAP.put("repair_station", Material.REPAIR_STATION);
		MATERIAL_MAP.put("barrier", Material.BARRIER);
		MATERIAL_MAP.put("piston", Material.PISTON);
		MATERIAL_MAP.put("moss_block", Material.MOSS_BLOCK);
		MATERIAL_MAP.put("gourd", Material.GOURD);
		MATERIAL_MAP.put("egg", Material.EGG);
		MATERIAL_MAP.put("cake", Material.CAKE);
		MATERIAL_MAP.put("amethyst", Material.AMETHYST);
		MATERIAL_MAP.put("powder_snow", Material.POWDER_SNOW);
	}

	private static final String TAG_PATH = "data/apoli/tags/blocks/material/";
	private static final String PACK_MCMETA = """
			{
				"pack": {
					"description": "Contains definitions for the different materials used by the Apoli Material Block Condition",
					"pack_format": 15
				}
			}""";

	public static void generateDatapack()
	{


		Map<String, Set<String>> materialTagBlocksMap = AMTG.generateMaterialMappings(AMTG.MATERIAL_MAP, Registries.BLOCK);

		int tagCount = materialTagBlocksMap.size();
		int blockCount = 0;
		for (Set<String> blockSet : materialTagBlocksMap.values())
		{
			blockCount += blockSet.size();
		}
		AMTG.LOGGER.info("Generated {} tags from {} blocks.", tagCount, blockCount);

		Map<String, String> materialTagFileMap = new HashMap<>();
		for (Map.Entry<String, Set<String>> materialTagBlocksEntry : materialTagBlocksMap.entrySet())
		{
			String tagFileContents = generateTagFileContents(materialTagBlocksEntry.getValue());
			materialTagFileMap.put(materialTagBlocksEntry.getKey(), tagFileContents);
		}

		byte[] datapackZip = createDatapackZip(materialTagFileMap);
		if (datapackZip.length < 1) return;

		AMTG.LOGGER.info("Generated datapack with size {} bytes.", datapackZip.length);

		writeDatapack(datapackZip);
	}

	public static void writeDatapack(byte[] datapackZip)
	{
		try (FileOutputStream fileOutputStream = new FileOutputStream("Apoli-Material-Tags.zip"))
		{
			fileOutputStream.write(datapackZip);
		}
		catch (IOException exception)
		{
			LOGGER.error("Failed to write datapack Zip.", exception);
			return;
		}

		LOGGER.info("Wrote {} bytes to Apoli-Material-Tags.zip", datapackZip.length);
	}

	public static byte[] createDatapackZip(Map<String, String> tags)
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream))
		{
			ZipEntry packMCMETA = new ZipEntry("pack.mcmeta");
			zipOutputStream.putNextEntry(packMCMETA);
			zipOutputStream.write(PACK_MCMETA.getBytes(StandardCharsets.UTF_8));
			zipOutputStream.closeEntry();

			for (Map.Entry<String, String> tagMapEntry : tags.entrySet())
			{
				ZipEntry tagZipEntry = new ZipEntry(TAG_PATH + tagMapEntry.getKey() + ".json");
				zipOutputStream.putNextEntry(tagZipEntry);
				zipOutputStream.write(tagMapEntry.getValue().getBytes(StandardCharsets.UTF_8));
				zipOutputStream.closeEntry();
			}
		}
		catch (IOException exception)
		{
			LOGGER.error("Failed to create datapack Zip.", exception);
			return new byte[0];
		}

		return byteArrayOutputStream.toByteArray();
	}

	public static Map<String, Set<String>> generateMaterialMappings(Map<String, Material> materialMap, Iterable<Block> blockIterable)
	{
		Map<String, Set<String>> materialTagBlocksMap = new HashMap<>();

		for (Block block : blockIterable)
		{
			Material material = block.getDefaultState().getMaterial();
			for (Map.Entry<String, Material> entry : materialMap.entrySet())
			{
				if (material != entry.getValue()) continue;

				Identifier blockIdentifier = Registries.BLOCK.getId(block);
				if (blockIdentifier == null)
				{
					LOGGER.warn("Unregistered block {} found while mapping materials to block IDs, skipping!", block.getClass().getSimpleName());
					continue;
				}

				String key = entry.getKey();
				String blockIdentifierString = blockIdentifier.toString();

				Set<String> tagBlocksSet;
				if (materialTagBlocksMap.containsKey(key))
				{
					tagBlocksSet = materialTagBlocksMap.get(key);
				}
				else
				{
					tagBlocksSet = new TreeSet<>();
					materialTagBlocksMap.put(key, tagBlocksSet);
				}
				tagBlocksSet.add(blockIdentifierString);

				break;
			}
		}

		return materialTagBlocksMap;
	}

	public static String generateTagFileContents(Set<String> blocks)
	{
		JsonObject root = new JsonObject();
		root.addProperty("replace", false);

		JsonArray values = new JsonArray();
		for (String block : blocks)
		{
			JsonObject value = new JsonObject();
			value.addProperty("id", block);
			value.addProperty("required", false);

			values.add(value);
		}

		root.add("values", values);
		return GSON.toJson(root);
	}

	@Environment(EnvType.CLIENT)
	public static class Client implements ClientModInitializer
	{
		@Override
		public void onInitializeClient()
		{
			ClientLifecycleEvents.CLIENT_STARTED.register(client -> AMTG.generateDatapack());
		}
	}

	@Environment(EnvType.SERVER)
	public static class Server implements DedicatedServerModInitializer
	{
		@Override
		public void onInitializeServer()
		{
			ServerLifecycleEvents.SERVER_STARTING.register(server -> AMTG.generateDatapack());
		}
	}
}
