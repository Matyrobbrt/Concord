package tk.sciwhiz12.concord.command.discord.checks;

import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.minecraft.server.MinecraftServer;

import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import tk.sciwhiz12.concord.ChatBot;

public record SlashCommandContext(SlashCommandEvent event, ChatBot bot) {

    public net.dv8tion.jda.api.entities.User user() {
        return event.getUser();
    }
    
    public ReplyCallbackAction deferReply(boolean ephermeal) {
        return event.deferReply(ephermeal);
    }
    
    public MinecraftServer minecraftServer() {
        return bot.getServer();
    }
    
}