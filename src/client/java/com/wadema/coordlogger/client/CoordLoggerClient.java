package com.wadema.coordlogger.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CoordLoggerClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			if (client.player != null) {
				double x = client.player.getX();
				double y = client.player.getY();
				double z = client.player.getZ();

				String dimension = getDimensionName(client.player.level().dimension());

				String type;
				String locationName;

				if (client.isLocalServer()) {
					type = "Singleplayer";

					if (client.getSingleplayerServer() != null) {
						locationName = client.getSingleplayerServer()
								.getWorldData()
								.getLevelName();
					} else {
						locationName = "Unknown";
					}
				} else {
					type = "Multiplayer";

					ServerData server = client.getCurrentServer();

					if (server != null) {
						locationName = server.ip;
					} else {
						locationName = "Unknown";
					}
				}

				Path file = FabricLoader.getInstance()
						.getConfigDir()
						.resolve("coordlogger-last-location.json");

				JsonObject json = new JsonObject();

				json.addProperty("Type", type);

				if (type.equals("Singleplayer")) {
					json.addProperty("World", locationName);
				} else {
					json.addProperty("Address", locationName);
				}

				json.addProperty("Dimension", dimension);
				json.addProperty("X", x);
				json.addProperty("Y", y);
				json.addProperty("Z", z);

				Gson gson = new GsonBuilder()
						.setPrettyPrinting()
						.create();

				String data = gson.toJson(json);

				try {
					Files.writeString(file, data);

					System.out.println("Last location saved:");
					System.out.print(data);
				} catch (IOException e) {
					System.err.println("Failed to save coordinates!");
					e.printStackTrace();
				}
			}
		});
	}

	private static String getDimensionName(ResourceKey<Level> dimension) {
		if (dimension.equals(Level.OVERWORLD)) {
			return "Overworld";
		}

		if (dimension.equals(Level.NETHER)) {
			return "Nether";
		}

		if (dimension.equals(Level.END)) {
			return "The End";
		}

		return dimension.identifier().toString();
	}
}
