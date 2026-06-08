package io.github.lunifo.finalfrontier.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.lunifo.finalfrontier.FinalFrontier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.io.Reader;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimpleDynamicRegistry<T> implements ResourceManagerReloadListener {
	private final String folder;
	private final Codec<T> codec;
	private final Map<ResourceLocation, T> entries = new HashMap<>();
	private final List<Consumer<SimpleDynamicRegistry<T>>> listeners = new ArrayList<>();

	private static final Map<String, SimpleDynamicRegistry<?>> ALL = new HashMap<>();

	public static void registerAll(BiConsumer<String, SimpleDynamicRegistry<?>> registryConsumer) {
		ALL.forEach(registryConsumer);
	}

	public SimpleDynamicRegistry(String folder, Codec<T> codec) {
		this.folder = folder;
		this.codec = codec;

		ALL.put(folder, this);
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		entries.clear();

		for (ResourceLocation rawLocation : resourceManager.listResources(folder, path -> path.toString().endsWith(".json")).keySet()) {
			try(Reader reader = resourceManager.openAsReader(rawLocation)) {
				JsonElement json = JsonParser.parseReader(reader);
				DataResult<T> result = codec.parse(JsonOps.INSTANCE, json);
				result.resultOrPartial(FinalFrontier.LOGGER::error).ifPresent(newValue -> {
					try {
						put(newValue, refineResourceLocation(rawLocation));
					} catch (IllegalArgumentException e) {
						FinalFrontier.LOGGER.error("Found duplicate entry: {}", e.toString());
					}
				});
			} catch (Exception e) {
				FinalFrontier.LOGGER.error("Failed to load dynamic registry entry '{}' from folder '{}'", rawLocation, folder);
			}
		}

		listeners.forEach(listener -> listener.accept(this));
	}

	private ResourceLocation refineResourceLocation(ResourceLocation rawLocation) {
		String path = rawLocation.getPath();

		path = path.replace(folder + "/", "");
		path = path.replace(".json", "");

		return rawLocation.withPath(path);
	}

	public void addListener(Consumer<SimpleDynamicRegistry<T>> listener) {
		listeners.add(listener);
	}

	public boolean contains(ResourceLocation location) {
		return entries.containsKey(location);
	}

	public T get(ResourceLocation location) {
		return entries.get(location);
	}

	public void put(T newValue, ResourceLocation location) throws IllegalArgumentException {
		if (contains(location)) {
			throw new IllegalArgumentException(
					String.format(
							"Tried to overwrite dynamic registry '%s' entry '%s' with '%s' at location '%s'",
							folder,
							entries.get(location),
							newValue,
							location
					)
			);
		} else {
			forcePut(newValue, location);
		}
	}

	public void forcePut(T newValue, ResourceLocation location) {
		entries.put(location, newValue);
	}

	public Collection<T> values() {
		return entries.values();
	}

	public Collection<ResourceLocation> resourceLocations() {
		return entries.keySet();
	}

	public Map<ResourceLocation, T> entries() {
		return entries;
	}

	public Codec<T> byResourceLocationCodec() {
		return ResourceLocation.CODEC.flatXmap(
				DataResult.partialGet(this::get, () -> "Dynamic registry " + folder + " has no entry for key "),
				value -> entries
						.entrySet()
						.stream()
						.filter(entry -> value.equals(entry.getValue()))
						.findFirst()
						.map(entry -> DataResult.success(entry.getKey()))
						.orElseGet(() -> DataResult.error(() -> "Dynamic registry " + folder + " does not contain the entry " + value.toString()))
		);
	}
}
