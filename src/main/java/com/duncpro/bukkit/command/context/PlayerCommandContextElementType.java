package com.duncpro.bukkit.command.context;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommandContextElementType extends CommandContextElementType {
    public PlayerCommandContextElementType() {
        super(Player.class);
    }

    @Override
    public Object resolve(CommandSender sender) throws IncorrectCommandContextException {
        if (!(sender instanceof Player))
            throw new IncorrectCommandContextException("This command is only available to players.");

        return sender;
    }
}
