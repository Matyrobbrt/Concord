package tk.sciwhiz12.concord.util;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.RestAction;

public class RestActionWrapper<T> implements RestAction<T> {
    
    private final RestAction<T> delegate;
    
    private RestActionWrapper(RestAction<T> delegate) {
        this.delegate = delegate;
    }
    
    public static <T> RestActionWrapper<T> of(RestAction<T> delegate) {
        return new RestActionWrapper<>(delegate);
    }
    
    public <O> RestActionWrapper<O> flatMapIf(boolean condition, Function<? super T, RestAction<O>> mapper, Function<? super T, RestAction<O>> orElseMapper) {
        return flatMapIf(() -> condition, mapper, orElseMapper);
    }
    
    public <O> RestActionWrapper<O> flatMapIf(BooleanSupplier condition, Function<? super T, RestAction<O>> mapper, Function<? super T, RestAction<O>> orElseMapper) {
        final var newDelegate = delegate.flatMap(condition.getAsBoolean() ? mapper : orElseMapper);
        return of(newDelegate);
    }
    
    public <O> RestActionWrapper<O> mapIf(boolean condition, Function<? super T, O> mapper, Function<? super T, O> orElseMapper) {
        return mapIf(() -> condition, mapper, orElseMapper);
    }
    
    public <O> RestActionWrapper<O> mapIf(BooleanSupplier condition, Function<? super T, O> mapper, Function<? super T, O> orElseMapper) {
        final var newDelegate = delegate.map(condition.getAsBoolean() ? mapper : orElseMapper);
        return of(newDelegate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JDA getJDA() {
        return delegate.getJDA();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestActionWrapper<T> setCheck(BooleanSupplier checks) {
        delegate.setCheck(checks);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void queue(@Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure) {
        delegate.queue(success, failure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T complete(boolean shouldQueue) throws RateLimitedException {
        return delegate.complete(shouldQueue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<T> submit(boolean shouldQueue) {
        return delegate.submit(shouldQueue);
    }

}
