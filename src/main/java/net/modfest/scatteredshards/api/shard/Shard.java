package net.modfest.scatteredshards.api.shard;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.modfest.scatteredshards.ScatteredShards;

public class Shard {
	public static final Codec<Either<ItemStack, Identifier>> ICON_CODEC = Codec.either(ItemStack.CODEC, Identifier.CODEC);
	
	public static final Codec<Shard> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("shard_type_id").forGetter(Shard::shardTypeId),
			TextCodecs.CODEC.fieldOf("name").forGetter(Shard::name),
			TextCodecs.CODEC.fieldOf("lore").forGetter(Shard::lore),
			TextCodecs.CODEC.fieldOf("hint").forGetter(Shard::hint),
			TextCodecs.CODEC.fieldOf("source").forGetter(Shard::source),
			Identifier.CODEC.fieldOf("source_id").forGetter(Shard::sourceId),
			ICON_CODEC.fieldOf("icon").forGetter(Shard::icon)
		).apply(instance, Shard::new));
	
	public static final Either<ItemStack, Identifier> MISSING_ICON = Either.right(new Identifier("scattered_shards:textures/gui/shards/missing_icon.png"));
	public static final Identifier MISSING_SHARD_SOURCE = ScatteredShards.id("missing");
	public static final Identifier LOST_AND_FOUND_SHARD_SOURCE = ScatteredShards.id("lost_and_found");
	public static final Shard MISSING_SHARD = new Shard(ShardType.MISSING_ID, Text.of("Missing"), Text.of(""), Text.of(""), Text.of("None"), MISSING_SHARD_SOURCE, MISSING_ICON);

	protected Identifier shardTypeId;
	protected Text name;
	protected Text lore;
	protected Text hint;
	protected Text source;
	protected Identifier sourceId;
	protected Either<ItemStack, Identifier> icon;
	
	public Shard(Identifier shardTypeId, Text name, Text lore, Text hint, Text source, Identifier sourceId, Either<ItemStack, Identifier> icon) {
		Stream.of(name, lore, hint, source, icon).forEach(Objects::requireNonNull);
		this.shardTypeId = shardTypeId;
		this.name = name;
		this.lore = lore;
		this.hint = hint;
		this.source = source;
		this.sourceId = sourceId;
		this.icon = icon;
	}

	public Identifier shardTypeId() {
		return shardTypeId;
	}
	/*
	public ShardType getShardType() {
		return ScatteredShardsAPI.getShardTypes().get(shardTypeId);
	}*/

	public Text name() {
		return name;
	}

	public Text lore() {
		return lore;
	}

	public Text hint() {
		return hint;
	}

	public Text source() {
		return source;
	}

	public Identifier sourceId() {
		return sourceId;
	}
	
	public Either<ItemStack, Identifier> icon() {
		return icon;
	}

	public Shard setShardType(Identifier shardTypeId) {
		this.shardTypeId = shardTypeId;
		return this;
	}

	public Shard setName(Text value) {
		this.name = value;
		return this;
	}

	public Shard setLore(Text value) {
		this.lore = value;
		return this;
	}

	public Shard setHint(Text value) {
		this.hint = value;
		return this;
	}

	public Shard setIcon(Either<ItemStack, Identifier> icon) {
		this.icon = icon;
		return this;
	}

	public Shard setIcon(ItemStack itemValue) {
		this.icon = Either.left(itemValue);
		return this;
	}

	public Shard setIcon(Identifier textureValue) {
		this.icon = Either.right(textureValue);
		return this;
	}

	public Shard setSource(Text source) {
		this.source = source;
		return this;
	}

	public Shard setSourceId(Identifier id) {
		this.sourceId = id;
		return this;
	}
	
	/*
	private static Either<ItemStack, Identifier> iconFromNbt(NbtElement nbt) {
		if (nbt instanceof NbtString str) {
			return Either.right(new Identifier(str.asString()));
		} else if (nbt instanceof NbtCompound compound) {
			return Either.left(ItemStack.fromNbt(compound));
		} else {
			return MISSING_ICON;
		}
	}*/

	public static Shard fromNbt(NbtCompound nbt) {
		return CODEC.parse(NbtOps.INSTANCE, nbt).result().orElseThrow();
		/*
		Identifier shardTypeId = new Identifier(nbt.getString("ShardType"));
		Text name = Text.Serialization.fromLenientJson(nbt.getString("Name"));
		Text lore = Text.Serialization.fromLenientJson(nbt.getString("Lore"));
		Text hint = Text.Serialization.fromLenientJson(nbt.getString("Hint"));
		Text source = Text.Serialization.fromLenientJson(nbt.getString("Source"));
		Identifier sourceId = new Identifier(
				nbt.contains("SourceId", NbtElement.STRING_TYPE) ?
						nbt.getString("SourceId") :
						LOST_AND_FOUND_SHARD_SOURCE.toString()
				);
		var icon = iconFromNbt(nbt.get("Icon"));
		return new Shard(shardTypeId, name, lore, hint, source, sourceId, icon);*/
	}

	public NbtCompound toNbt() {
		return (NbtCompound) CODEC.encodeStart(NbtOps.INSTANCE, this).result().orElseThrow();
		/*
		nbt.putString("ShardType", shardTypeId.toString());
		nbt.putString("Name", Text.Serialization.toJsonString(name));
		nbt.putString("Lore", Text.Serialization.toJsonString(lore));
		nbt.putString("Hint", Text.Serialization.toJsonString(hint));
		nbt.putString("Source", Text.Serialization.toJsonString(source));
		nbt.putString("SourceId", sourceId.toString());

		icon.ifLeft((stack) -> {
			nbt.put("Icon", stack.writeNbt(new NbtCompound()));
		});
		icon.ifRight((texture) -> {
			nbt.putString("Icon", texture.toString());
		});

		return nbt;*/
	}

	public JsonObject toJson() {
		JsonObject result = new JsonObject();

		result.add("shard_type", new JsonPrimitive(shardTypeId.toString()));
		result.add("name", new JsonPrimitive(Text.Serialization.toJsonString(name)));
		result.add("lore", new JsonPrimitive(Text.Serialization.toJsonString(lore)));
		result.add("hint", new JsonPrimitive(Text.Serialization.toJsonString(hint)));
		result.add("source", new JsonPrimitive(Text.Serialization.toJsonString(source)));
		result.add("source_id", new JsonPrimitive(sourceId.toString()));

		icon.ifLeft((stack) -> {
			JsonElement stackTree = ItemStack.CODEC.encode(stack, JsonOps.INSTANCE, new JsonObject())
				.getOrThrow(false, (err) -> ScatteredShards.LOGGER.warn("Couldn't write the icon for a shard: " + err));

			result.add("icon", stackTree);
		});

		icon.ifRight((texture) -> {
			result.add("icon", new JsonPrimitive(texture.toString()));
		});

		return result;
	}
	/*
	public void write(PacketByteBuf buf) {
		buf.writeIdentifier(shardTypeId);
		buf.writeString(Text.Serialization.toJsonString(name));
		buf.writeString(Text.Serialization.toJsonString(lore));
		buf.writeString(Text.Serialization.toJsonString(hint));
		buf.writeString(Text.Serialization.toJsonString(source));
		buf.writeIdentifier(sourceId);
		buf.writeEither(icon, PacketByteBuf::writeItemStack, PacketByteBuf::writeIdentifier);
	}

	public static Shard read(PacketByteBuf buf) {
		Identifier shardTypeId = buf.readIdentifier();
		Text name = Text.Serialization.fromLenientJson(buf.readString());
		Text lore = Text.Serialization.fromLenientJson(buf.readString());
		Text hint = Text.Serialization.fromLenientJson(buf.readString());
		Text source = Text.Serialization.fromLenientJson(buf.readString());
		Identifier sourceId = buf.readIdentifier();
		var icon = buf.readEither(PacketByteBuf::readItemStack, PacketByteBuf::readIdentifier);
		return new Shard(shardTypeId, name, lore, hint, source, sourceId, icon);
	}*/
	
	public Shard copy() {
		Either<ItemStack, Identifier> icon = icon().mapBoth(stack -> stack, id -> id);
		return new Shard(shardTypeId, name.copy(), lore.copy(), hint.copy(), source.copy(), sourceId, icon);
	}

	public static Either<ItemStack, Identifier> iconFromJson(JsonElement element) {
		if (element instanceof JsonPrimitive primitive) {
			return Either.right(new Identifier(primitive.getAsString()));
		} else if (element instanceof JsonObject itemObj) {
			return Either.left(loadItemStack(itemObj));
		} else {
			return MISSING_ICON;
		}
	}

	public static Shard fromJson(JsonObject obj, Text source) {
		Identifier shardTypeId = new Identifier(JsonHelper.getString(obj, "shard_type"));
		Text name = Text.Serialization.fromLenientJson(JsonHelper.getString(obj, "name"));
		Text lore = Text.Serialization.fromLenientJson(JsonHelper.getString(obj, "lore"));
		Text hint = Text.Serialization.fromLenientJson(JsonHelper.getString(obj, "hint"));
		Identifier sourceId = new Identifier(JsonHelper.getString(obj, "source_id", LOST_AND_FOUND_SHARD_SOURCE.toString()));
		var icon = iconFromJson(obj.get("icon"));
		return new Shard(shardTypeId, name, lore, hint, source, sourceId, icon);
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
	
	public static Shard emptyOfType(Identifier id) {
		return MISSING_SHARD.copy().setShardType(id);
	}
	
	/*
	public static Shard emptyOfType(ShardType shardType) {
		return MISSING_SHARD.copy().setShardType(shardType);
	}*/

	private static ItemStack loadItemStack(JsonElement elem) {
		if (elem instanceof JsonObject obj) {
			Identifier itemId = new Identifier(obj.get("id").getAsString());
			int count = 1;
			if (obj.has("Count")) {
				count = obj.get("Count").getAsInt();
			}
			ItemStack stack = new ItemStack(Registries.ITEM.get(itemId), count);

			if (obj.has("tag")) {
				NbtCompound tag = NbtCompound.CODEC.decode(JsonOps.INSTANCE, obj.get("tag"))
					.getOrThrow(false, (err) -> ScatteredShards.LOGGER.warn("Couldn't deserialize an ItemStack tag."))
					.getFirst();
				stack.setNbt(tag);
			}

			return stack;
		} else {
			return new ItemStack(Items.AIR);
		}
	}

	public static Text getSourceForNamespace(String namespace) {
		return Text.translatable("shard_pack." + namespace + ".name");
	}

	public static Text getSourceForMod(ModContainer mod) {
		return Text.literal(mod.getMetadata().getName());
	}

	public static Optional<Text> getSourceForModId(String modId) {
		return FabricLoader.getInstance().getModContainer(modId).map(Shard::getSourceForMod);
	}
	
	public static Text getSourceForSourceId(Identifier id) {
		if (!id.getPath().equals("shard_pack")) {
			return Text.translatable("shard_pack." + id.getNamespace() + "." + id.getPath() + ".name");
		}
		
		return getSourceForModId(id.getNamespace())
				.orElse(Text.translatable("shard_pack." + id.getNamespace() + ".name"));
	}
	
	public static Identifier getSourceIdForNamespace(String namespace) {
		return new Identifier(namespace, "shard_pack");
	}
}
