package io.github.codeutilities.util.translation;

import net.minecraft.text.TranslatableText;

public interface ITranslatable {
    default TranslatableText getTranslation(String key) {
        return ITranslatable.get(key);
    }

    static TranslatableText get(String key) {
        return new TranslatableText(key);
    }
}
