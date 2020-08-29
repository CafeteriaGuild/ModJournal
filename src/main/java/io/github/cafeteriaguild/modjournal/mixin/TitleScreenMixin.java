package io.github.cafeteriaguild.modjournal.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.options.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    public TitleScreenMixin(Text text) {
        super(text);
    }

    @Inject(
        at = @At(
            value = "INVOKE",
            ordinal = 1,
            target = "Lnet/minecraft/client/gui/screen/TitleScreen;addButton(Lnet/minecraft/client/gui/widget/AbstractButtonWidget;)Lnet/minecraft/client/gui/widget/AbstractButtonWidget;"
        ),
        method = "initWidgetsNormal"
    )
    public void addModjournalButton(int y, int spacingY, CallbackInfo ci) {
        addButton(
            new TexturedButtonWidget(
                this.width / 2 - 124,
                y + spacingY * 2,
                20, 20,
                0, 0,
                20,
                new Identifier("modjournal", "textures/gui/open_journal.png"), 20, 40,
                (buttonWidget) -> {
                    MinecraftClient.getInstance().openScreen(new LanguageOptionsScreen(
                        this, client.options, client.getLanguageManager()
                    ));
                }, new TranslatableText("button.modjournal.open_journal"))
        );
    }
}
