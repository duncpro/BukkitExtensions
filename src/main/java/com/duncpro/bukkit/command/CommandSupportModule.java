package com.duncpro.bukkit.command;

import com.duncpro.bukkit.command.context.CommandContextElementType;
import com.duncpro.bukkit.command.context.CuboidRegionSelectionContextElementType;
import com.duncpro.bukkit.command.context.PlayerCommandContextElementType;
import com.duncpro.bukkit.command.context.SenderCommandContextElementType;
import com.duncpro.bukkit.command.datatypes.*;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class CommandSupportModule extends AbstractModule {
    @Override
    public void configure() {
        final var parameterTypes = Multibinder.newSetBinder(binder(), CommandParameterDataType.class);
        parameterTypes.addBinding().to(PlayerCommandParameterDataType.class);
        parameterTypes.addBinding().to(VectorCommandParameterDataType.class);
        parameterTypes.addBinding().to(IntegerCommandParameterDataType.class);
        parameterTypes.addBinding().to(DoubleCommandParameterDataType.class);
        parameterTypes.addBinding().to(StringCommandParameterDataType.class);
        parameterTypes.addBinding().to(DurationCommandParameterDataType.class);

        final var contextTypes = Multibinder.newSetBinder(binder(), CommandContextElementType.class);
        contextTypes.addBinding().to(CuboidRegionSelectionContextElementType.class);
        contextTypes.addBinding().to(SenderCommandContextElementType.class);
        contextTypes.addBinding().to(PlayerCommandContextElementType.class);
    }
}
