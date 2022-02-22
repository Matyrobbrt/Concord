package tk.sciwhiz12.concord.msg;

import java.util.Map;

import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;
import tk.sciwhiz12.concord.ChatBot;
import tk.sciwhiz12.concord.ConcordConfig;
import tk.sciwhiz12.concord.network.ConcordNetwork;
import tk.sciwhiz12.concord.network.RegisterEmotePacket;
import tk.sciwhiz12.concord.network.RegisterEmotePacket.EmoteData;
import tk.sciwhiz12.concord.network.RemoveEmotePacket;

public class PlayerListener {
    private final ChatBot bot;

    public PlayerListener(ChatBot bot) {
        this.bot = bot;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().getCommandSenderWorld().isClientSide()) return;
        if (!ConcordConfig.PLAYER_JOIN.get()) return;

        TranslatableComponent text = new TranslatableComponent("message.concord.player.join",
            event.getPlayer().getDisplayName());

        Messaging.sendToChannel(bot.getDiscord(), text.getString());

        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
        	bot.getDiscord().getGuilds().forEach(guild -> {
        		// Split the guilds, just so the packet isn't giant
        		ConcordNetwork.EMOJIFUL_CHANNEL.sendTo(new RegisterEmotePacket(Map.of(guild.getName(), guild.getEmotes().stream().map(EmoteData::new).toList())), 
        				serverPlayer.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        	});
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity().getCommandSenderWorld().isClientSide()) return;
        if (!ConcordConfig.PLAYER_LEAVE.get()) return;

        TranslatableComponent text = new TranslatableComponent("message.concord.player.leave",
            event.getPlayer().getDisplayName());

        Messaging.sendToChannel(bot.getDiscord(), text.getString());
        
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
        	bot.getDiscord().getGuilds().forEach(guild -> {
        		// Split the guilds, just so the packet isn't giant
        		ConcordNetwork.EMOJIFUL_CHANNEL.sendTo(new RemoveEmotePacket(Map.of(guild.getName(), guild.getEmotes().stream().map(EmoteData::new).toList())), 
        				serverPlayer.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        	});
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().getCommandSenderWorld().isClientSide()) return;
        if (!ConcordConfig.PLAYER_DEATH.get()) return;

        if (event.getEntity() instanceof ServerPlayer player) {
            Messaging.sendToChannel(bot.getDiscord(), player.getCombatTracker().getDeathMessage().getString());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    void onAdvancement(AdvancementEvent event) {
        Level world = event.getEntity().getCommandSenderWorld();
        if (world.isClientSide()) return;

        if (ConcordConfig.PLAYER_ADV_GAMERULE.get() && !world.getGameRules().getBoolean(GameRules.RULE_ANNOUNCE_ADVANCEMENTS))
            return;

        final DisplayInfo info = event.getAdvancement().getDisplay();
        if (info != null && info.shouldAnnounceChat()) {
            boolean enabled = switch (info.getFrame()) {
                case TASK -> ConcordConfig.PLAYER_ADV_TASK.get();
                case CHALLENGE -> ConcordConfig.PLAYER_ADV_CHALLENGE.get();
                case GOAL -> ConcordConfig.PLAYER_ADV_GOAL.get();
            };
            if (!enabled) return;
            TranslatableComponent text = new TranslatableComponent(
                "message.concord.player.advancement." + info.getFrame().getName(),
                event.getPlayer().getDisplayName(),
                info.getTitle(),
                info.getDescription());

            Messaging.sendToChannel(bot.getDiscord(), text.getString());
        }
    }
}
