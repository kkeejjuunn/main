package duke.commands.task;

import duke.commands.Command;
import duke.exceptions.DukeException;
import duke.models.tasks.Task;
import duke.util.DukeUi;
import duke.models.patients.PatientManager;
import duke.models.assignedtasks.AssignedTaskManager;
import duke.storages.StorageManager;
import duke.models.tasks.TaskManager;

import java.util.ArrayList;

public class ListTasksCommand implements Command {

    /**
     * .
     *
     * @param patientTask        .
     * @param tasks              .
     * @param patientList        .
     * @param dukeUi                 .
     * @param storageManager .
     * @throws DukeException .
     */
    @Override
    public void execute(AssignedTaskManager patientTask, TaskManager tasks, PatientManager patientList,
                        DukeUi dukeUi, StorageManager storageManager) {
        ArrayList<Task> list = tasks.getTaskList();
        dukeUi.listAllTasks(list);
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
