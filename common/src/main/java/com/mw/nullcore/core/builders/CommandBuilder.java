package com.mw.nullcore.core.builders;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class CommandBuilder {
    private static final List<Consumer<CommandDispatcher<CommandSourceStack>>> commands = new ArrayList<>();
    private final List<LiteralArgumentBuilder<CommandSourceStack>> arguments = new ArrayList<>();
    private int permission = 2;
    private String id;

    public static CommandBuilder builder() {
        return new CommandBuilder();
    }

    public CommandBuilder command(String id) {
        this.id = id;
        return this;
    }

    public CommandBuilder requires(int permissionLevel) {
        this.permission = permissionLevel;
        return this;
    }

    public CommandBuilder then(LiteralArgumentBuilder<CommandSourceStack> argument) {
        this.arguments.add(argument);
        return this;
    }

    public CommandBuilder executes(CommandExecutor executor) {
        LiteralArgumentBuilder<CommandSourceStack> cmd = Commands.literal(id).requires(src -> src.hasPermission(permission)).executes(executor::execute);
        arguments.forEach(cmd::then);
        return this;
    }

    public CommandBuilder create(CommandExecutor executor) {
        LiteralArgumentBuilder<CommandSourceStack> cmd = Commands.literal(id).requires(src -> src.hasPermission(permission)).executes(executor::execute);
        commands.add(dispatcher -> dispatcher.register(cmd));
        return this;
    }

    public static void registers(CommandDispatcher dispatcher) {
        commands.forEach(consumer -> consumer.accept(dispatcher));
    }

    @FunctionalInterface
    public interface CommandExecutor {
        int execute(CommandContext<CommandSourceStack> context);
    }
}
