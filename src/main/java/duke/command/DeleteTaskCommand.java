package duke.command;

import duke.core.DukeException;
import duke.core.Ui;
import duke.patient.Patient;
import duke.patient.PatientManager;
import duke.relation.PatientTask;
import duke.statistic.Counter;
import duke.storage.CounterStorage;
import duke.storage.PatientStorage;
import duke.storage.PatientTaskStorage;
import duke.storage.TaskStorage;
import duke.relation.PatientTaskList;
import duke.task.Task;
import duke.task.TaskManager;

import java.util.ArrayList;

public class DeleteTaskCommand extends Command {
    private int taskId;
    private String deletedTaskInfo;

    /**
     * .
     *
     * @param deletedTaskInfo .
     * @throws DukeException .
     */
    public DeleteTaskCommand(String deletedTaskInfo) throws DukeException {
        this.deletedTaskInfo = deletedTaskInfo;
    }

    /**
     * It extracts the task id based on the delete task command.
     * It checks whether user is trying to delete a task by id or description.
     *
     * @param deletedTaskInfo contains the delete command received from parser class which is a string.
     * @param ui let user choose the correct task to be deleted.
     * @param taskManager gets all the tasks with matched task description.
     * @return the task to be deleted which is a integer.
     * @throws DukeException if the task id is invalid or does not exist.
     */
    public int getTaskIdByDeleteTaskCommand(String deletedTaskInfo, Ui ui,
                                            TaskManager taskManager) throws DukeException {
        int id = 0;
        char firstChar = deletedTaskInfo.charAt(0);
        if (firstChar == '#') {
            try {
                id = Integer.parseInt(deletedTaskInfo.substring(1));
                try {
                    Task task = taskManager.getTask(id);
                } catch (Exception e) {
                    throw new DukeException("Task id does not exist. ");
                }
            } catch (Exception e) {
                throw new DukeException("Please follow format 'delete task #<id>'. ");
            }
        } else {
            ArrayList<Task> tasksWithSameDescription = taskManager.getTaskByDescription(deletedTaskInfo);
            if (tasksWithSameDescription.size() >= 1) {
                int numberChosen = ui.chooseTaskToDelete(tasksWithSameDescription.size());
                if (numberChosen >= 1) {
                    id = tasksWithSameDescription.get(numberChosen - 1).getID();
                }
            }
        }
        return id;
    }

    /**
     * It double confirms with user whether proceed to delete.
     * It alerts user if the task is assigned to any patient.
     * If user confirms the deletion, following steps will be performed.
     * It deletes the task from task list and standardTask.csv.
     * It deletes the patient task that linked to this task.
     * It promotes user the deleting of task is successful.
     *
     * @param patientTaskList        .
     * @param taskManager        .
     * @param patientManager     .
     * @param ui                 .
     * @param patientTaskStorage .
     * @param taskStorage        .
     * @param patientStorage     .
     * @throws DukeException .
     */
    @Override
    public void execute(PatientTaskList patientTaskList, TaskManager taskManager, PatientManager patientManager,
                        Ui ui, PatientTaskStorage patientTaskStorage, TaskStorage taskStorage,
                        PatientStorage patientStorage) throws DukeException {

        taskId = getTaskIdByDeleteTaskCommand(deletedTaskInfo, ui, taskManager);
        Task taskToBeDeleted = taskManager.getTask(taskId);
        ui.showTaskInfo(taskToBeDeleted);
        boolean toDelete;
        try {
            ArrayList<PatientTask> patientTask = patientTaskList.getTaskPatient(taskId);
            ArrayList<Patient> tempPatient = new ArrayList<>();
            for (PatientTask temppatientTask : patientTask) {
                tempPatient.add(patientManager.getPatient(temppatientTask.getPatientId()));
            }
            ui.taskPatientFound(taskToBeDeleted, patientTask, tempPatient);
            toDelete = ui.confirmTaskToBeDeleted(taskToBeDeleted, true);
        } catch (Exception e) {
            toDelete = ui.confirmTaskToBeDeleted(taskToBeDeleted,false);
        }

        if (toDelete) {
            taskManager.deleteTask(taskId);
            patientTaskList.deleteAllPatientTaskByTaskId(taskId);
            ui.taskDeleted();
            taskStorage.save(taskManager.getTaskList());
            patientTaskStorage.save(patientTaskList.fullPatientTaskList());
        }
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
