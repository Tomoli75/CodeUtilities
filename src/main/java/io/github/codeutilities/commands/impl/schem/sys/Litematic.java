package io.github.codeutilities.commands.impl.schem.sys;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.impl.schem.sys.loaders.LitematicaBitArray;
import io.github.codeutilities.commands.impl.schem.sys.utils.DFText;
import io.github.codeutilities.commands.impl.schem.sys.utils.DFUtils;
import io.github.codeutilities.util.templates.TemplateUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

public class Litematic {

    public static String parse(File file) {
        try {
            InputStream inputStream = new FileInputStream(file);

            CompoundTag tag = NbtIo.readCompressed(inputStream);

            Schematic schematicData = new Schematic();

            CompoundTag metadata = tag.getCompound("Metadata");
            CompoundTag enclosingsize = metadata.getCompound("EnclosingSize");
            int width = enclosingsize.getInt("x");
            int height = enclosingsize.getInt("y");
            int length = enclosingsize.getInt("z");
            int regioncount = metadata.getInt("RegionCount");
            String name = metadata.getString("Name");
            String description = metadata.getString("Description");
            String author = metadata.getString("Author");
            int volume = metadata.getInt("TotalVolume");
            int blocks = metadata.getInt("TotalBlocks");
            long created = metadata.getLong("TimeCreated");
            long modified = metadata.getLong("TimeModified");

            CompoundTag regions = tag.getCompound("Regions");
            CompoundTag litematicdata = regions.getCompound(name);
            CompoundTag position = litematicdata.getCompound("Position");
            int offsetx = position.getInt("x");
            int offsety = position.getInt("y");
            int offsetz = position.getInt("z");
            CompoundTag size = litematicdata.getCompound("Size");
            ListTag palette = litematicdata.getList("BlockStatePalette", 10);
            long[] longblockstates = litematicdata.getLongArray("BlockStates");
            int nbits = (int) Math.max(Math.ceil(log2(palette.size())), 2) + 1;
            LitematicaBitArray arr = new LitematicaBitArray(nbits, volume, longblockstates);
            ArrayList<String> Properties = new ArrayList<>();
            ArrayList<String> PaletteBlocks = new ArrayList<>();
            ArrayList<Integer> intblocks = new ArrayList<>();
            int[] BlockIds = new int[volume];

            MinecraftClient.getInstance().player.sendMessage(new LiteralText("§8§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §r[§e§lSchem2DF§r]§8§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m"), false);
            MinecraftClient.getInstance().player.sendMessage(new LiteralText("§aLoader started asynchronously"), false);
            MinecraftClient.getInstance().player.sendMessage(new LiteralText("§6" + name + " §e§oBy " + author), false);
            MinecraftClient.getInstance().player.sendMessage(new LiteralText("§7Description: " + description), false);
            MinecraftClient.getInstance().player.sendMessage(new LiteralText("§d" + width + "x" + height + "x" + length + " " + volume + " Blocks including air, " + blocks + " Blocks excluding air."), false);
            MinecraftClient.getInstance().player.sendMessage(new LiteralText("§9Created §b" + new Date(created) + " §9Last Modified §b" + new Date(modified)), false);
            MinecraftClient.getInstance().player.sendMessage(new LiteralText("§8§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m"), false);

            for (Tag block : palette) {
                JsonParser parser = CodeUtilities.JSON_PARSER;
                JsonElement blockjson = parser.parse(block.toString());
                JsonObject properties = blockjson.getAsJsonObject().getAsJsonObject("Properties");
                JsonElement blocktype = blockjson.getAsJsonObject().get("Name");
                StringBuilder property = new StringBuilder();
                if (properties != null) {
                    for (Object prop : properties.entrySet().toArray()) {
                        property.append(",").append(prop.toString());
                    }
                }

                if (!property.toString().equals("")) {
                    property = new StringBuilder(property.substring(1));
                }
                String blockMetadata = "[" + property.toString().replaceAll("\"", "") + "]";
                schematicData.AddBlockToPalette(blocktype.getAsString() + ((blockMetadata == "[]") ? "" : blockMetadata));
            }
            int index2 = 0;
            for (int x = 0; x < Math.abs(width); x++) {
                for (int y = 0; y < Math.abs(height); y++) {
                    for (int z = 0; z < Math.abs(length); z++) {
                        arr.getAt(index2);
                        BlockIds[index2] = arr.getAt(index2);
                        schematicData.AddBlock(arr.getAt(index2));
                        index2++;
                    }
                }
            }

            int codeblocks = 2;
            int functions = 1;
            schematicData.name = name;
            schematicData.author = author;
            schematicData.description = description;
            schematicData.creationTime = created;
            schematicData.lastModified = modified;
            schematicData.setWidth(width);
            schematicData.setHeight(height);
            schematicData.setLength(length);
            schematicData.fileType = "Litematic";
            StringBuilder nbt = new StringBuilder("{\"blocks\":[" + DFUtils.GenerateFunctionHeader(schematicData.name) + "," + DFUtils.GenerateSchematicData(schematicData, FilenameUtils.removeExtension(file.getName())));

            nbt.append(",{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"Palette\",\"scope\":\"local\"}},\"slot\":0}");
            codeblocks++;
            int slots = 1;
            boolean createlist = true;
            for (DFText thing : schematicData.getPaletteTexts()) {
                if (slots >= 27) {
                    slots = 1;
                    if (createlist) {
                        nbt.append("]},\"action\":\"CreateList\"}");
                    } else {
                        nbt.append("]},\"action\":\"AppendValue\"}");
                    }
                    if (codeblocks >= 23) {
                        nbt.append(",{\"id\":\"block\",\"block\":\"call_func\",\"args\":{\"items\":[]},\"data\":\"Build").append(functions + 1).append("\"}");
                        nbt.append("]}");
                        ItemStack item = Blocks.TWISTING_VINES.asItem().getDefaultStack();
                        TemplateUtils.compressTemplateNBT(item, file.getName(), "SchemaDF", nbt.toString());
                        item.setCustomName(new LiteralText("§d" + file.getName() + functions));
                        MinecraftClient.getInstance().interactionManager.clickCreativeStack(item, functions + 8);
                        codeblocks = 1;
                        functions++;
                        nbt = new StringBuilder("{\"blocks\":[{\"id\":\"block\",\"block\":\"func\",\"args\":{\"items\":[{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"False\",\"tag\":\"Is Hidden\",\"action\":\"dynamic\",\"block\":\"func\"}},\"slot\":26}]},\"data\":\"Build" + functions + "\"}");
                    }
                    nbt.append(",{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"Palette\",\"scope\":\"local\"}},\"slot\":0}");
                    codeblocks++;
                    createlist = false;
                }
                nbt.append(",{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"").append(thing.text).append("\"}},\"slot\":").append(slots).append("}");
                slots++;
            }
            if (createlist) {
                nbt.append("]},\"action\":\"CreateList\"}");
            } else {
                nbt.append("]},\"action\":\"AppendValue\"}");
            }

            if (codeblocks >= 23) {
                nbt.append(",{\"id\":\"block\",\"block\":\"call_func\",\"args\":{\"items\":[]},\"data\":\"Build").append(functions + 1).append("\"}");
                nbt.append("]}");
                ItemStack item = Blocks.TWISTING_VINES.asItem().getDefaultStack();
                TemplateUtils.compressTemplateNBT(item, file.getName(), "SchemaDF", nbt.toString());
                item.setCustomName(new LiteralText("§d" + file.getName() + functions));
                MinecraftClient.getInstance().interactionManager.clickCreativeStack(item, functions + 8);
                codeblocks = 1;
                functions++;
                nbt = new StringBuilder("{\"blocks\":[{\"id\":\"block\",\"block\":\"func\",\"args\":{\"items\":[{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"False\",\"tag\":\"Is Hidden\",\"action\":\"dynamic\",\"block\":\"func\"}},\"slot\":26}]},\"data\":\"Build" + functions + "\"}");
            }

            nbt.append(",{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"BlockData\",\"scope\":\"local\"}},\"slot\":0}");
            codeblocks++;
            slots = 1;
            createlist = true;

            DFText[] blocksTexts = schematicData.getBlocksTexts();

            System.out.println("list length: " + blocksTexts.length);
            for (DFText thing : blocksTexts) {
                if (slots >= 27) {
                    slots = 1;
                    if (createlist) {
                        nbt.append("]},\"action\":\"CreateList\"}");
                    } else {
                        nbt.append("]},\"action\":\"AppendValue\"}");
                    }
                    if (codeblocks >= 23) {
                        nbt.append(",{\"id\":\"block\",\"block\":\"call_func\",\"args\":{\"items\":[]},\"data\":\"Build").append(functions + 1).append("\"}");
                        nbt.append("]}");
                        ItemStack item = Blocks.TWISTING_VINES.asItem().getDefaultStack();
                        TemplateUtils.compressTemplateNBT(item, file.getName(), "SchemaDF", nbt.toString());
                        item.setCustomName(new LiteralText("§d" + file.getName() + functions));
                        MinecraftClient.getInstance().interactionManager.clickCreativeStack(item, functions + 8);
                        codeblocks = 1;
                        functions++;
                        nbt = new StringBuilder("{\"blocks\":[{\"id\":\"block\",\"block\":\"func\",\"args\":{\"items\":[{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"False\",\"tag\":\"Is Hidden\",\"action\":\"dynamic\",\"block\":\"func\"}},\"slot\":26}]},\"data\":\"Build" + functions + "\"}");
                    }
                    nbt.append(",{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"BlockData\",\"scope\":\"local\"}},\"slot\":0}");
                    codeblocks++;
                    createlist = false;
                }
                nbt.append(",{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"").append(thing.text).append("\"}},\"slot\":").append(slots).append("}");
                slots++;
            }
            if (createlist) {
                nbt.append("]},\"action\":\"CreateList\"}");
            } else {
                nbt.append("]},\"action\":\"AppendValue\"}");
            }
            nbt.append("]}");

            ItemStack item = Blocks.TWISTING_VINES.asItem().getDefaultStack();
            TemplateUtils.compressTemplateNBT(item, file.getName(), "Schem2DF", nbt.toString());
            item.setCustomName(new LiteralText("§d" + file.getName() + functions));
            MinecraftClient.getInstance().interactionManager.clickCreativeStack(item, functions + 8);

            return nbt.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int log2(int N) {
        return (int) (Math.log(N) / Math.log(2));
    }
}