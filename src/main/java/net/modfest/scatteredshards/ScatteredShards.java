package net.modfest.scatteredshards;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.modfest.scatteredshards.api.ScatteredShardsAPI;
import net.modfest.scatteredshards.load.ShardTypeLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.modfest.scatteredshards.api.shard.ShardType;
import net.modfest.scatteredshards.command.ShardCommand;
import net.modfest.scatteredshards.networking.ScatteredShardsNetworking;

public class ScatteredShards implements ModInitializer {

	public static final String ID = "scattered_shards";

	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static Identifier id(String path) {
		return Identifier.of(ID, path);
	}
	
	public static String permission(String path) {
		return ID + "." + path;
	}

	@Override
	public void onInitialize() {
		ShardType.register();
		ShardTypeLoader.register();
		ShardCommand.register();
		ScatteredShardsNetworking.register();
		ScatteredShardsContent.register();
		ServerLifecycleEvents.SERVER_STOPPED.register(ScatteredShardsAPI::serverStopped);
	}
}
