package io.github.codeutilities.commands.impl.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.commands.sys.Command;
import io.github.codeutilities.commands.sys.arguments.ArgBuilder;
import io.github.codeutilities.commands.sys.arguments.types.StringListArgumentType;
import io.github.codeutilities.util.actiondump.Action;
import io.github.codeutilities.util.actiondump.ActionDump;
import io.github.codeutilities.util.actiondump.Types;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.codeutilities.util.chat.TextUtil;
import io.github.codeutilities.util.misc.ItemUtil;
import io.github.codeutilities.util.networking.DFInfo;
import io.github.codeutilities.util.networking.State;
import io.github.codeutilities.util.templates.SearchUtil;
import java.util.ArrayList;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class SearchCommand extends Command {
    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        String[] allcodeblocks = new String[SearchUtil.SearchType.values().length];
        int i = 0;
        for(SearchUtil.SearchType searchType : SearchUtil.SearchType.values()) {
            allcodeblocks[i] = searchType.name();
            i++;
        }
        cd.register(ArgBuilder.literal("search")
                .executes(ctx -> { // /search clear shortcut
                    ChatUtil.sendMessage(new TranslatableText("codeutilities.template_search.cleared"), ChatType.SUCCESS);
                    SearchUtil.clearSearch();
                    return 1;
                })
            .then(ArgBuilder.literal("clear")
                .executes(ctx -> { // /search clear
                    ChatUtil.sendMessage(new TranslatableText("codeutilities.template_search.cleared"), ChatType.SUCCESS);
                    SearchUtil.clearSearch();
                    return 1;
                }))
                .then(ArgBuilder.argument("action", StringArgumentType.greedyString())
                    .executes(ctx -> { // /search <action..>
                        try {
                            String query = ctx.getArgument("action", String.class);
                            ArrayList<Action> actions = ActionDump.getActions(query);
                            mc.player.sendMessage(TextUtil.colorCodesToTextComponent("§x§0§0§b§5§f§c✎ §x§0§0§e§0§b§0" + new TranslatableText("codeutilities.template_search.begin_search", "§x§0§0§f§8§f§c" + query + "§x§0§0§e§0§b§0").parse(mc.player.getCommandSource(), mc.player, 1).getString()).shallowCopy(), false);
                            mc.player.sendMessage(new LiteralText(""), false);
                            for(Action action : actions){
                                try {
                                    mc.player.sendMessage(createMessage(action.getCodeBlock().getIdentifier(), action.getName(), action.getCodeBlock().getItem().toItemStack(), action.getIcon().toItemStack()), false);
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                            mc.player.sendMessage(createMessage("func", query, ActionDump.getCodeBlock("function").get(0).getItem().toItemStack(), ""), false);
                            mc.player.sendMessage(createMessage("process", query, ActionDump.getCodeBlock("process").get(0).getItem().toItemStack(), ""), false);
                        }catch (Exception e) {
                            e.printStackTrace();

                            ChatUtil.sendMessage(new TranslatableText("codeutilities.template_search.invalid", ctx.getArgument("action", String.class)).parse(mc.player.getCommandSource(), mc.player, 1), ChatType.FAIL);
                        }
                        return 1;

                    })));

        cd.register(ArgBuilder.literal("exactsearch")
                .then(ArgBuilder.argument("codeblock", StringListArgumentType.string(allcodeblocks))
                        .then(ArgBuilder.argument("action", StringArgumentType.greedyString())
                                .executes(ctx -> { // /exactsearch <codeblock> <action..> (mostly used for the clickevent)
                                    try {
                                        String actionArgument = ctx.getArgument("action", String.class);
                                        String codeblockArgument = ctx.getArgument("codeblock", String.class);
                                        SearchUtil.SearchType searchType = SearchUtil.SearchType.valueOf(codeblockArgument.toUpperCase());
                                        if (DFInfo.isOnDF() && DFInfo.currentState.getMode() == State.Mode.DEV && mc.player.isCreative()) {
                                            SearchUtil.beginSearch(searchType, actionArgument);
                                        }else {
                                            ChatUtil.sendMessage(new TranslatableText("codeutilities.command.require_dev_mode", ctx.getArgument("action", String.class)).parse(mc.player.getCommandSource(), mc.player, 1), ChatType.FAIL);
                                        }

                                    }catch (Exception e) {
                                        e.printStackTrace();

                                        ChatUtil.sendMessage(new TranslatableText("codeutilities.template_search.invalid").parse(mc.player.getCommandSource(), mc.player, 1), ChatType.FAIL);
                                    }
                                    return 1;

                                })
                        )));

    }

    private MutableText createMessage(String codeblockid, String actionname, Object codeblock, Object action){

        String clickHere = "§x§f§c§6§6§0§3⏩ §x§f§c§d§3§0§3Click to Search §x§f§c§6§6§0§3⏪";
        Text clickHereText = TextUtil.colorCodesToTextComponent(clickHere);

        MutableText actionmsg = TextUtil.colorCodesToTextComponent("§x§f§c§f§f§5§9" + actionname).shallowCopy();
        actionmsg.setStyle(actionmsg.getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/exactsearch " + codeblockid.toUpperCase() + " " + actionname))
                .withHoverEvent(createHoverEvent(action, clickHere)));

        Types block = ActionDump.valueOf(codeblockid.toUpperCase());
        MutableText blockmsg = TextUtil.colorCodesToTextComponent(block.getColor() + block.getName()).shallowCopy();
        blockmsg.setStyle(blockmsg.getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/exactsearch " + codeblockid.toUpperCase() + " " + actionname))
                .withHoverEvent(createHoverEvent(codeblock, clickHere)));

        MutableText startarrow = TextUtil.colorCodesToTextComponent(" §x§f§c§4§7§0§0⏵ ").shallowCopy();
        startarrow.setStyle(startarrow.getStyle()
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, clickHereText))
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/exactsearch " + codeblockid.toUpperCase() + " " + actionname)));

        MutableText middlearrow = TextUtil.colorCodesToTextComponent(" §x§f§c§8§b§0§0⇒ ").shallowCopy();
        middlearrow.setStyle(middlearrow.getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/exactsearch " + codeblockid.toUpperCase() + " " + actionname))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, clickHereText)));

        return startarrow.append(blockmsg).append(middlearrow).append(actionmsg);
    }

    private HoverEvent createHoverEvent(Object hover, String clickHere){
        if(hover instanceof String){
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtil.colorCodesToTextComponent(clickHere));
        }
        if(hover instanceof ItemStack){
            return new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(ItemUtil.addLore((ItemStack) hover, new String[]{TextUtil.toTextString(""), TextUtil.toTextString(clickHere)})));
        }
        return null;
    }
}
