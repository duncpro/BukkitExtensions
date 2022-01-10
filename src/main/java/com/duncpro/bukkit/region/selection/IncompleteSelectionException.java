package com.duncpro.bukkit.region.selection;

public class IncompleteSelectionException extends Exception {
    public IncompleteSelectionException(Exception e) {
        super(e);
    }

    public IncompleteSelectionException() {}
}
