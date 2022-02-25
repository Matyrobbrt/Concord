package tk.sciwhiz12.concord.command.discord.moderation;

import java.time.Instant;
import java.util.List;

import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.minecraft.network.chat.TextComponent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import tk.sciwhiz12.concord.ChatBot;
import tk.sciwhiz12.concord.command.discord.ConcordSlashCommand;
import tk.sciwhiz12.concord.command.discord.checks.Checks;
import tk.sciwhiz12.concord.command.discord.checks.ChecksSet;

public final class KickDiscordCommand extends ConcordSlashCommand {

    public KickDiscordCommand(ChatBot bot) {
        super(bot);
        name = "kick";
        help = "Kicks an user from the Minecraft server.";
        checks = ChecksSet.DEFAULT
            .toBuilder()
            .and(Checks.ACCOUNTS_LINKED)
            .and(Checks.SERVER_OP)
            .and(validPlayerChecker("player"))
            .build();
        options = List.of(
                new OptionData(OptionType.STRING, "player", "The player to kick.").setRequired(true).setAutoComplete(true),
                new OptionData(OptionType.STRING, "reason", "The reason to kick.").setRequired(true)
            );
    }
    
    @Override
    protected void execute0(SlashCommandEvent event) {
        final var player = event.getOption("player", playerResolver);
        final var reason = event.getOption("reason", OptionMapping::getAsString);
        player.connection.disconnect(new TextComponent(reason));
        final var embed = new EmbedBuilder()
            .setColor(0xff0000)
            .setTitle("Player Kicked")
            .setDescription("%s kicked %s from the Minecraft server!".formatted(event.getUser().getAsMention(), player.getDisplayName().getString()))
            .addField("Reason", reason, false)
            .setTimestamp(Instant.now());
        event.deferReply().addEmbeds(embed.build()).queue();
    }
    
    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        bot.suggestPlayers(event, 5, e -> e.getFocusedOption().getName().equals("player"));
    }

}
