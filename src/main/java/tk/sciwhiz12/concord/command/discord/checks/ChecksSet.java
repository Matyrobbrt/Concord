package tk.sciwhiz12.concord.command.discord.checks;

import java.util.function.Predicate;

public final class ChecksSet implements Predicate<SlashCommandContext> {
    
    public static final ChecksSet DEFAULT = builder().and(Checks.INTEGRATION_ENABLED, Checks.COMMAND_ENABLED).build();

    private final Predicate<SlashCommandContext> checker;
    private ChecksSet(Predicate<SlashCommandContext> checker) {
        this.checker = checker;
    }
    
    @Override
    public boolean test(SlashCommandContext t) {
        return checker.test(t);
    }
    
    public Builder toBuilder() {
        return new Builder(checker);
    }

    public static Builder builder() {
        return new Builder();
    }
    
    public static final class Builder {
        private Predicate<SlashCommandContext> predicate;
        
        private Builder(Predicate<SlashCommandContext> predicate) {
            this.predicate = predicate;
        }
        
        private Builder() {
            this(ctx -> true);
        }
        
        public Builder and(Predicate<SlashCommandContext> predicate) {
            this.predicate = this.predicate.and(predicate);
            return this;
        }
        
        public Builder and(Checks... perms) {
            for (final var perm : perms) {
                this.predicate = this.predicate.and(perm);
            }
            return this;
        }
        
        public ChecksSet build() {
            return new ChecksSet(predicate);
        }
    }
}