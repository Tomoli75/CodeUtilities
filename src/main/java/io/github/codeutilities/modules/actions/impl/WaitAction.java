package io.github.codeutilities.modules.actions.impl;

import io.github.codeutilities.modules.actions.Action;
import io.github.codeutilities.modules.actions.json.ActionJson;

public class WaitAction extends Action {

    @Override
    public String getId() {
        return "wait";
    }

    @Override
    public void execute(ActionJson params) {
        try { Thread.sleep(params.getInt("amount"));
        } catch (InterruptedException e) { e.printStackTrace(); }
    }

}
