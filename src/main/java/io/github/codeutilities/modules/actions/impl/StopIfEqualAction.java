package io.github.codeutilities.modules.actions.impl;

import io.github.codeutilities.modules.actions.Action;
import io.github.codeutilities.modules.actions.json.ActionJson;
import io.github.codeutilities.modules.tasks.Task.TaskExecutorThread;

public class StopIfEqualAction extends Action {

    @Override
    public String getId() {
        return "stopIfEqual";
    }

    @Override
    public void execute(ActionJson params) {
        String a = params.getString("a");
        String b = params.getString("b");

        if (a.equals(b)) {
            TaskExecutorThread.status = -1;
        }
    }

}
