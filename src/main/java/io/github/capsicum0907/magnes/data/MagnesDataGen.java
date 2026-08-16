package io.github.capsicum0907.magnes.data;

import java.util.concurrent.CompletableFuture;

import io.github.capsicum0907.magnes.Magnes;
import io.github.capsicum0907.magnes.MagnesRegistry;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Everything under {@code src/generated/resources} comes from here, so nothing in
 * that directory is written by hand.
 */
@EventBusSubscriber(modid = Magnes.MODID, value = { Dist.CLIENT, Dist.DEDICATED_SERVER })
public final class MagnesDataGen {
    private MagnesDataGen() {
    }

    @SubscribeEvent
    public static void gather(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new Models(output, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new Language(output));
        generator.addProvider(event.includeServer(), new Recipes(output, event.getLookupProvider()));
        generator.addProvider(event.includeServer(), new TestStructures(output));
    }

    private static class Models extends ItemModelProvider {
        Models(PackOutput output, ExistingFileHelper existingFileHelper) {
            super(output, Magnes.MODID, existingFileHelper);
        }

        /**
         * Two textures, chosen by whether the magnet is on. The base model is the
         * switched-off one and the override is the working one, because that is the
         * way round model overrides go: the plain model is what is drawn when no
         * predicate matches.
         *
         * <p>An enchantment glint would have been less work and is what this used to
         * do. It is also nearly invisible against a red and blue item, which makes it
         * a state indicator that does not indicate state.
         */
        @Override
        protected void registerModels() {
            ItemModelBuilder attracting = withExistingParent("magnet_on", mcLoc("item/generated"))
                    .texture("layer0", modLoc("item/magnet"));

            withExistingParent("magnet", mcLoc("item/generated"))
                    .texture("layer0", modLoc("item/magnet_off"))
                    .override()
                    .predicate(ResourceLocation.fromNamespaceAndPath(Magnes.MODID, "active"), 1.0F)
                    .model(attracting)
                    .end();
        }
    }

    private static class Language extends LanguageProvider {
        Language(PackOutput output) {
            super(output, Magnes.MODID, "en_us");
        }

        @Override
        protected void addTranslations() {
            add(MagnesRegistry.MAGNET.get(), "Magnet");
            add("item.magnes.magnet.on", "Attracting");
            add("item.magnes.magnet.off", "Switched off");
            add("item.magnes.magnet.reach", "Reaches %s blocks");
        }
    }

    /**
     * Four iron and a block of redstone, laid out as the thing it is: two arms and
     * the bar that joins them.
     */
    private static class Recipes extends RecipeProvider {
        Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected void buildRecipes(RecipeOutput output) {
            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, MagnesRegistry.MAGNET.get())
                    .pattern("I I")
                    .pattern("I I")
                    .pattern(" R ")
                    .define('I', Items.IRON_INGOT)
                    .define('R', Blocks.REDSTONE_BLOCK)
                    .unlockedBy("has_redstone_block", has(Blocks.REDSTONE_BLOCK))
                    .save(output);
        }
    }
}
