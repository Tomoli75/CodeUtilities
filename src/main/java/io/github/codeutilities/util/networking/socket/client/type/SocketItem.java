package io.github.codeutilities.util.networking.socket.client.type;

import net.minecraft.item.ItemStack;

public abstract class SocketItem {

    public abstract String getIdentifier();

    public abstract ItemStack getItem(String data) throws Exception;

}
