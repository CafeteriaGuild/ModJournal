package io.github.cafeteriaguild.modjournal.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class JournalScreen extends Screen {
    public JournalScreen() {
        super(new TranslatableText("screen.modjournal.journal"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        renderBackground(matrices);
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        client.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_TEXTURE);
        DrawableHelper.drawTexture(
            matrices,
            0, 0,
            0, 0,
            width, height,
            16, 16
        );

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(0, height, 0.0D).texture(0 / 32.0F, height / 32.0F).color(64, 64, 64, 255).next();
        buffer.vertex(width, height, 0.0D).texture(width / 32.0F, height / 32.0F).color(64, 64, 64, 255).next();
        buffer.vertex(width, 0, 0.0D).texture(width / 32.0F, 0 / 32.0F).color(64, 64, 64, 255).next();
        buffer.vertex(0, 0, 0.0D).texture(0 / 32.0F, 0 / 32.0F).color(64, 64, 64, 255).next();
        tessellator.draw();
    }
}
