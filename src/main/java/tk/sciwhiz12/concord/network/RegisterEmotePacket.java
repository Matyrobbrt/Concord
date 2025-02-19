/*
 * Concord - Copyright (c) 2020-2022 SciWhiz12
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package tk.sciwhiz12.concord.network;

import java.util.List;
import java.util.Map;

import net.minecraft.network.FriendlyByteBuf;

import net.dv8tion.jda.api.entities.Emote;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import tk.sciwhiz12.concord.Concord;
import tk.sciwhiz12.concord.compat.emojiful.EmojifulCompat;

public final class RegisterEmotePacket {

    private final Map<String, List<EmoteData>> emotes;

    public RegisterEmotePacket(Map<String, List<EmoteData>> emotes) {
        this.emotes = emotes;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeMap(emotes, FriendlyByteBuf::writeUtf,
                (buf, list) -> buf.writeCollection(list, (buf2, d) -> d.write(buf2)));
    }

    public void handle(NetworkEvent.Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (Concord.emojifulLoaded()) {
                emotes.forEach((guildName, emts) -> {
                    emts.forEach(data -> EmojifulCompat.loadDiscordEmoji(guildName, data.emoteId(), data.emoteName(), data.animated()));
                    Concord.LOGGER.info("Registered {} Emojiful emojis from guild \"{}\"", emts.size(), guildName);
                });
                EmojifulCompat.indexEmojis();
            }
        });
    }

    public static RegisterEmotePacket decode(FriendlyByteBuf buffer) {
        return new RegisterEmotePacket(buffer.readMap(FriendlyByteBuf::readUtf, buf -> buf.readList(EmoteData::new)));
    }

    public record EmoteData(long emoteId, String emoteName, boolean animated) {

        public void write(FriendlyByteBuf buffer) {
            buffer.writeLong(emoteId);
            buffer.writeUtf(emoteName);
            buffer.writeBoolean(animated);
        }

        public EmoteData(FriendlyByteBuf buffer) {
            this(buffer.readLong(), buffer.readUtf(), buffer.readBoolean());
        }

        public EmoteData(Emote emote) {
            this(emote.getIdLong(), emote.getName(), emote.isAnimated());
        }
    }
}
