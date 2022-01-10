package com.duncpro.bukkit.command.context;

import org.bukkit.command.CommandSender;

public class SenderCommandContextElementType extends CommandContextElementType {
    public SenderCommandContextElementType() {
        super(CommandSender.class);
    }

    @Override
    public Object resolve(CommandSender sender) throws IncorrectCommandContextException {
        return sender;
    }
}
