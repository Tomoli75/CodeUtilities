package io.github.codeutilities.config.config;

import io.github.codeutilities.config.structure.ConfigGroup;
import io.github.codeutilities.config.structure.ConfigSubGroup;
import io.github.codeutilities.config.types.BooleanSetting;
import io.github.codeutilities.config.types.IntegerSetting;

public class ScreenGroup extends ConfigGroup {
    public ScreenGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        // World Rendering
        ConfigSubGroup worldRendering = new ConfigSubGroup("world_rendering");
        worldRendering.register(new BooleanSetting("chestReplacement", false));
        worldRendering.register(new IntegerSetting("signRenderDistance", 100));
        this.register(worldRendering);

        // Non sub-grouped
        this.register(new BooleanSetting("chestToolTip", true));
        this.register(new BooleanSetting("dfButton", true));
        this.register(new BooleanSetting("dfNodeButtons", false));
        this.register(new BooleanSetting("variableScopeView", true));
        this.register(new BooleanSetting("cpuOnScreen", true));
        this.register(new BooleanSetting("f3Tps", true));
        this.register(new BooleanSetting("cosmeticsEnabled", true));
        this.register(new BooleanSetting("loadTabStars", true));
    }
}
