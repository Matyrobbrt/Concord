package tk.sciwhiz12.concord.command.discord.checks;

import java.util.function.Predicate;

import tk.sciwhiz12.concord.Concord;
import tk.sciwhiz12.concord.ConcordConfig;

public enum Checks implements Predicate<SlashCommandContext> { 
    INTEGRATION_ENABLED(ctx -> {
        if (!Concord.isEnabled()) {
            ctx.deferReply(true).setContent("This command required Discord Integration to be active, but it isn't.").queue();
            return false;
        }
        return true;
    }),
    COMMAND_ENABLED(ctx -> {
        final var cmdName = ctx.event().getName();
        if (ConcordConfig.DISCORD_COMMANDS_ENABLED.containsKey(cmdName) && !ConcordConfig.DISCORD_COMMANDS_ENABLED.get(cmdName).get()) {
            ctx.deferReply(true).setContent("This command is disabled!").queue();
            return false;
        } 
        return true;
    }),
    ACCOUNTS_LINKED(ctx -> {
        if (!ctx.bot().isUserLinked(ctx.user())) {
             ctx.deferReply(true).setContent("This command requires your Minecraft account to be linked with your Discord Account!").queue();
            return false;
        }
        return true;
    }),
    SERVER_OP(ctx -> {
        final var isOp = ctx.bot().getLinkedAccount(ctx.user()).map(profile -> ctx.minecraftServer().getPlayerList().isOp(profile)).orElse(false);
        if (Boolean.FALSE.equals(isOp)) {
            ctx.deferReply(true).setContent("This command requires your Minecraft and Discord accounts linked, and requires OP permission levels on the Minecraft server.").queue();
        }
        return isOp;
    });
    
    private final Predicate<SlashCommandContext> predicate;

    private Checks(Predicate<SlashCommandContext> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(SlashCommandContext ctx) {
        return predicate.test(ctx);
    }

}