package io.github.codeutilities.util.chat;

import io.github.codeutilities.util.render.color.MinecraftColors;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    public static String EMPTY_TEXT = "{\"text\":\"\"}";

    public static String textComponentToColorCodes(Text message) {
        List<Text> siblings = message.getSiblings();

        //System.out.println(message);

        StringBuilder newMsg = new StringBuilder();
        String currentText;

        for (Text sibling : siblings) {
            Style style = sibling.getStyle();

            // color
            TextColor color = style.getColor();
            String literalColor = String.valueOf(color);
            String code = MinecraftColors.getMcFromFormatting(color);
            if (code == null) currentText = MinecraftColors.hexToMc(literalColor);
            else currentText = code;

            if (style.isBold()) currentText = currentText + "§l";
            if (style.isItalic()) currentText = currentText + "§o";
            if (style.isStrikethrough()) currentText = currentText + "§m";
            if (style.isUnderlined()) currentText = currentText + "§n";
            if (style.isObfuscated()) currentText = currentText + "§k";

            currentText = currentText + sibling.getString();
            newMsg.append(currentText);
        }

        return newMsg.toString();
    }

    public static Text colorCodesToTextComponent(String message) {
        if(message.equals("")) return Text.Serializer.fromJson("{\"text\": \"\"}");
        message = "§f" + message;
        String sibling, literalColorCodes, color = null, text;
        String bold, italic, underlined, strikethrough, obfuscated;
        List<String> sections = new java.util.ArrayList<>(Collections.emptyList());
        List<String> literalColorSections = new java.util.ArrayList<>(Collections.emptyList());
        MutableText result = Text.Serializer.fromJson("{\"text\": \"\"}");
        char literalColorCode;
        int lastColorOccurrence;

        Pattern pattern = Pattern.compile("(§x(§[^§]){6}([^§]|§[lomnk])+)|(§([^§]|§[lomnk])+)");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) sections.add(matcher.group());

        for (String section : sections) {
            sibling = section;

            // reset modifiers
            bold = "null";
            italic = "null";
            underlined = "null";
            strikethrough = "null";
            obfuscated = "null";

            // text
            text = sibling.replaceAll("^(§.)+", "");

            // color
            pattern = Pattern.compile("(§x(§([0-f]|r)){6})|(§([0-f]|r))");
            matcher = pattern.matcher(sibling);
            while (matcher.find()) literalColorSections.add(matcher.group());
            literalColorCodes = literalColorSections.get(literalColorSections.size() - 1);
            if (literalColorSections.size() - 1 != 0) {
                literalColorCode = literalColorCodes.charAt(1);
                if (literalColorCode == 'x') color = MinecraftColors.mcToHex(literalColorCodes);
                else
                    color = String.valueOf(Objects.requireNonNull(MinecraftColors.fromCode(literalColorCodes.charAt(1))).getFormatting());
            }

            // modifiers
            lastColorOccurrence = sibling.lastIndexOf(literalColorCodes);
            if (sibling.indexOf("§l") > lastColorOccurrence) bold = "true";
            if (sibling.indexOf("§o") > lastColorOccurrence) italic = "true";
            if (sibling.indexOf("§n") > lastColorOccurrence) underlined = "true";
            if (sibling.indexOf("§m") > lastColorOccurrence) strikethrough = "true";
            if (sibling.indexOf("§k") > lastColorOccurrence) obfuscated = "true";

            // serializer
            assert false;
            if (!text.equals("")) {
                result.append(Text.Serializer.fromJson("{" +
                        "\"text\": \"" + text +
                        "\", \"color\": \"" + color +
                        "\", \"bold\": \"" + bold +
                        "\", \"italic\": \"" + italic +
                        "\", \"underlined\": \"" + underlined +
                        "\", \"strikethrough\": \"" + strikethrough +
                        "\", \"obfuscated\": \"" + obfuscated +
                        "\"}"));
            }
        }

        return result;
    }

    public static String toString(Text text){
        if(text.getString().equals("")) return "{\"text\": \"\"}";
        return "{\"extra\":[" + String.join(",", toExtraString(text.getSiblings())) + "],\"text\":\"\"}";
    }

    private static ArrayList<String> toExtraString(List<Text> siblings){
        ArrayList<String> result = new ArrayList<>();
        for(Text sibling : siblings){
            result.add("{\"text\": \"" + sibling.getString() +
                    "\", \"color\": \"" + sibling.getStyle().getColor() +
                    "\", \"bold\": " + sibling.getStyle().isBold() +
                    ", \"italic\": " + sibling.getStyle().isItalic() +
                    ", \"underlined\": " + sibling.getStyle().isUnderlined() +
                    ", \"strikethrough\": " + sibling.getStyle().isStrikethrough() +
                    ", \"obfuscated\": " + sibling.getStyle().isObfuscated() + "}");
        }
        return result;
    }

    public static String toTextString(String text){
        return TextUtil.toString(TextUtil.colorCodesToTextComponent(text.replaceAll("&", "§").replaceAll("\"", "''"))).replaceAll("''", "\\\\\"");
    }

}
