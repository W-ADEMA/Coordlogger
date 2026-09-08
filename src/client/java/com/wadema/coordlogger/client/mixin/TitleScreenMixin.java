package com.wadema.coordlogger.client.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.Minecraft;
import net.fabricmc.loader.api.FabricLoader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    private String type;
    private String world;
    private String dimension;
    private String x;
    private String y;
    private String z;

    private void loadLastLocation() {
        Path file = FabricLoader.getInstance()
                .getConfigDir()
                .resolve("coordlogger-last-location.json");

        if (!Files.exists(file)) {
            return;
        }

        try {
            String jsonString = Files.readString(file);
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

            type = json.get("Type").getAsString();
            world = json.get("World").getAsString();
            dimension = json.get("Dimension").getAsString();
            x = json.get("X").getAsString();
            y = json.get("Y").getAsString();
            z = json.get("Z").getAsString();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawText(GuiGraphicsExtractor graphics, String text, int y) {
        Minecraft minecraft = Minecraft.getInstance();

        graphics.text(
                minecraft.font,
                text,
                10,
                y,
                0xFFFFFFFF,
                true
        );
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void loadLocation(CallbackInfo ci) {
        loadLastLocation();
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void renderCoordLoggerValues(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {
        drawText(graphics, "Type: " + type, 10);
        drawText(graphics, "World: " + world, 22);
        drawText(graphics, "Dimension: " + dimension, 34);
        drawText(graphics, "X: " + x, 46);
        drawText(graphics, "Y: " + y, 58);
        drawText(graphics, "Z: " + z, 70);
    }
}
