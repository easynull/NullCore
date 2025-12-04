package com.mw.nullcore.core.builders;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class CommandBuilder {
    private static final Set<Consumer<CommandDispatcher<CommandSourceStack>>> commands = new HashSet<>();
    private final Set<ArgumentBuilder<CommandSourceStack, ?>> children = new HashSet<>();
    private final String id;
    private int permission = 2;
    private CommandExecutor executor;

    private CommandBuilder(String id) {
        this.id = id;
    }

    public static CommandBuilder builder(String id) {
        return new CommandBuilder(id);
    }

    public CommandBuilder requires(int permissionLevel) {
        this.permission = permissionLevel;
        return this;
    }

    public CommandBuilder executes(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    public CommandBuilder then(ArgumentBuilder<CommandSourceStack, ?> argument) {
        this.children.add(argument);
        return this;
    }

    public CommandBuilder then(LiteralArgumentBuilder<CommandSourceStack> argument) {
        this.children.add(argument);
        return this;
    }

    public void register() {
        commands.add(dispatcher -> {
            LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(id).requires(src -> src.hasPermission(permission));
            if (executor != null) {
                builder.executes(executor::execute);
            }
            for (ArgumentBuilder<CommandSourceStack, ?> child : children) {
                builder.then(child);
            }
            dispatcher.register(builder);
        });
    }

    public static void registerAll(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (Consumer<CommandDispatcher<CommandSourceStack>> command : commands) {
            command.accept(dispatcher);
        }
    }

    public static LiteralArgumentBuilder<CommandSourceStack> literal(String name) {
        return Commands.literal(name);
    }

    @FunctionalInterface
    public interface CommandExecutor {
        int execute(CommandContext<CommandSourceStack> context);
    }
}
