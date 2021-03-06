package io.github.overrun.squidcraft;

import io.github.overrun.squidcraft.block.Blocks;
import io.github.overrun.squidcraft.cmd.Commands;
import io.github.overrun.squidcraft.config.Configs;
import io.github.overrun.squidcraft.item.Items;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author squid233
 * @since 2020/12/19
 */
public final class SquidCraft implements ModInitializer {
    public static final Logger logger = LogManager.getLogger();
    public static final String MODID = "squidcraft";
    public static final Identifier SQUID_LOOT_TABLE_ID = new Identifier("entities/squid");

    @Override
    public void onInitialize() {
        Configs.init();
        registerGameObj();
        registerEvents();
    }

    private void registerGameObj() {
        try {
            Class.forName(Blocks.class.getName());
            Class.forName(Items.class.getName());
        } catch (ClassNotFoundException e) {
            logger.catching(e);
        }
    }

    private void registerEvents() {
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, builder, setter) -> {
            if (SQUID_LOOT_TABLE_ID.equals(id)) {
                builder.withPool(FabricLootPoolBuilder.builder()
                        .rolls(ConstantLootTableRange.create(1))
                        .withFunction(SetCountLootFunction.builder(new UniformLootTableRange(1.0f, 3.0f)).build())
                        .withFunction(FurnaceSmeltLootFunction.builder().conditionally(
                                EntityPropertiesLootCondition.builder(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.create().flags(
                                                EntityFlagsPredicate.Builder.create().onFire(true).build()
                                        )
                                )
                        ).build())
                        .withFunction(LootingEnchantLootFunction.builder(new UniformLootTableRange(0.0f, 1.0f)).build())
                        .withEntry(ItemEntry.builder(Items.SHREDDED_SQUID).build())
                        .build()
                );
            }
        });
        Commands.register();
    }
}
