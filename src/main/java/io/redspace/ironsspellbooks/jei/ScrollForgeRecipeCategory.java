package io.redspace.ironsspellbooks.jei;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ScrollForgeRecipeCategory implements IRecipeCategory<ScrollForgeRecipe> {
    public static final RecipeType<ScrollForgeRecipe> SCROLL_FORGE_RECIPE_RECIPE_TYPE = RecipeType.create(IronsSpellbooks.MODID, "scroll_forge", ScrollForgeRecipe.class);
    private final IDrawable background;
    private final IDrawable icon;
    private final String inkSlotName = "inkSlot";
    private final String paperSlotName = "paperSlot";
    private final String focusSlotName = "focusSlot";
    private final String outputSlotName = "outputSlot";

    public ScrollForgeRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = JeiPlugin.SCROLL_FORGE_GUI;
        background = guiHelper.drawableBuilder(location, 11, 16, 64, 49)
                .addPadding(0, 0, 0, 0)
                .build();
        icon = guiHelper.createDrawableItemStack(new ItemStack(BlockRegistry.SCROLL_FORGE_BLOCK.get()));
    }

    @Override
    public RecipeType<ScrollForgeRecipe> getRecipeType() {
        return SCROLL_FORGE_RECIPE_RECIPE_TYPE;
    }

    @Override
    public ITextComponent getTitle() {
        return BlockRegistry.SCROLL_FORGE_BLOCK.get().getName();
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ScrollForgeRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> inkInputs = recipe.inkInputs();
        ItemStack paperInput = recipe.paperInput();
        ItemStack focusInput = recipe.focusInput();
        List<ItemStack> outputs = recipe.scrollOutputs();

        IRecipeSlotBuilder inkInputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .addItemStacks(inkInputs)
                .setSlotName(inkSlotName);

        IRecipeSlotBuilder paperInputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 24, 1)
                .addItemStack(paperInput)
                .setSlotName(paperSlotName);

        IRecipeSlotBuilder focusInputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 46, 1)
                .addItemStack(focusInput)
                .setSlotName(focusSlotName);

        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 23, 31)
                .addItemStacks(outputs)
                .setSlotName(outputSlotName);

        if (inkInputs.size() == outputs.size()) {
            builder.createFocusLink(inkInputSlot, outputSlot);
        }
    }
}