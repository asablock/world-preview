package caeruleusTait.world.preview.backend.color;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public abstract class OldSimpleJsonResourceReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Gson gson;
    private final String directory;

    public OldSimpleJsonResourceReloadListener(Gson gson, String directory) {
        this.gson = gson;
        this.directory = directory;
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, JsonElement> map = new HashMap<>();
        scanDirectory(resourceManager, this.directory, this.gson, map);
        return map;
    }

    public static void scanDirectory(ResourceManager resourceManager, String name, Gson gson, Map<ResourceLocation, JsonElement> output) {
        FileToIdConverter fileToIdConverter = FileToIdConverter.json(name);

        for(Map.Entry<ResourceLocation, Resource> entry : fileToIdConverter.listMatchingResources(resourceManager).entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            ResourceLocation resourceLocation2 = fileToIdConverter.fileToId(resourceLocation);

            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement jsonElement = GsonHelper.fromJson(gson, reader, JsonElement.class);
                JsonElement jsonElement2 = output.put(resourceLocation2, jsonElement);
                if (jsonElement2 != null) {
                    throw new IllegalStateException("Duplicate data file ignored with ID " + resourceLocation2);
                }
            } catch (IllegalArgumentException | IOException | JsonParseException exception) {
                LOGGER.error("Couldn't parse data file {} from {}", resourceLocation2, resourceLocation, exception);
            }
        }
    }
}