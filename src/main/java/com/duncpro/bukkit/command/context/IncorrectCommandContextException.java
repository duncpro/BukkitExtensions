package com.duncpro.bukkit.command.context;

public class IncorrectCommandContextException extends Exception {
    public IncorrectCommandContextException(String message) {
        super(message);
    }

    public IncorrectCommandContextException(String message, Exception e) {
        super(message, e);
    }
}
