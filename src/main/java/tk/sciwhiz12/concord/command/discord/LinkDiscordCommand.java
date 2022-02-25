package tk.sciwhiz12.concord.command.discord;

import java.util.List;

import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import tk.sciwhiz12.concord.ChatBot;
import tk.sciwhiz12.concord.util.RestActionWrapper;

public final class LinkDiscordCommand extends ConcordSlashCommand {

    public LinkDiscordCommand(ChatBot bot) {
        super(bot);
        name = "link";
        help = "Links your Discord account with your Minecraft account.";
        options = List.of(new OptionData(OptionType.INTEGER, "code", "The code to use for linking."));
    }

    @Override
    protected void execute0(SlashCommandEvent event) {
        final var code = event.getOption("code", OptionMapping::getAsInt);
        RestActionWrapper.of(event.deferReply(true))
            .flatMapIf(bot.getLinkedUsers().codeExists(code), hook -> {
                bot.getLinkedUsers().linkByCode(code, event.getUser().getIdLong());
                return hook.editOriginal("Successfully linked accounts!");
            }, hook -> hook.editOriginal("The code you provided is invalid, or has been used already!"))
            .queue();
    }

}