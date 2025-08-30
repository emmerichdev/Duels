package com.meteordevelopments.duels.util.function;

@FunctionalInterface
public interface TriFunction<S, T, U, R> {

    R apply(S s, T t, U u);

}