package com.duncpro.bukkit.command;

public class CommandArgumentFormatException extends Exception {
    public CommandArgumentFormatException(Exception e) {
        super(e);
    }

    public CommandArgumentFormatException(String message) {
        super(message);
    }
}
