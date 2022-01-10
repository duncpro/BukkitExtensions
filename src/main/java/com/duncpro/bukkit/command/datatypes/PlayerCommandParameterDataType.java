package com.duncpro.bukkit.command.datatypes;

import com.duncpro.bukkit.command.CommandArgumentFormatException;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class PlayerCommandParameterDataType extends CommandParameterDataType {
    @Inject private Server server;

    public PlayerCommandParameterDataType() {
        super(Player.class);
    }

    @Override
    public Player parse(String arg) throws CommandArgumentFormatException {
        final var player = server.getPlayer(arg);
        if (player == null) throw new CommandArgumentFormatException(arg + " does not refer to an online player.");
        return player;
    }
}
