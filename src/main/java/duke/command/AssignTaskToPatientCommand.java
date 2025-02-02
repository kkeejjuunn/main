package duke.command;

import duke.core.DukeException;
import duke.core.Ui;
import duke.patient.PatientManager;
import duke.relation.EventPatientTask;
import duke.relation.StandardPatientTask;
import duke.statistic.Counter;
import duke.storage.CounterStorage;
import duke.storage.PatientStorage;
import duke.storage.PatientTaskStorage;
import duke.storage.TaskStorage;
import duke.relation.PatientTask;
import duke.relation.PatientTaskList;
import duke.task.TaskManager;

public class AssignTaskToPatientCommand extends Command {

    private String command;
    private String[] taskAssignmentInfo;
    private PatientTask newPatientTask;

    /**
     * .
     *
     * @param taskAssignmentInfo .
     * @throws DukeException .
     */
    public AssignTaskToPatientCommand(String[] taskAssignmentInfo) throws DukeException {
        super();
        this.taskAssignmentInfo = taskAssignmentInfo;
    }

    /**
     * .
     *
     * @param patientTaskList    .
     * @param tasksList          .
     * @param patientList        .
     * @param ui                 .
     * @param patientTaskStorage .
     * @param taskStorage        .
     * @param patientStorage     .
     * @throws DukeException .
     */
    @Override
    public void execute(PatientTaskList patientTaskList, TaskManager tasksList, PatientManager patientList,
                        Ui ui, PatientTaskStorage patientTaskStorage, TaskStorage taskStorage,
                        PatientStorage patientStorage) throws DukeException {

        char firstChar = taskAssignmentInfo[1].charAt(0);

        try {
            if (taskAssignmentInfo[0].equals("S")) {
                if (firstChar == '#') {
                    int tempPid = Integer.parseInt(taskAssignmentInfo[1].replace("#","").trim());
                    int tempTid = Integer.parseInt(taskAssignmentInfo[2]);
                    String temptime = taskAssignmentInfo[3];
                    newPatientTask = new StandardPatientTask(tempPid, tempTid, temptime, taskAssignmentInfo[0]);
                } else {
                    int tempPid = patientList.getPatientByName(taskAssignmentInfo[1]).get(0).getID();
                    int tempTid = tasksList.getTaskByDescription(taskAssignmentInfo[2]).get(0).getID();
                    String temptime = taskAssignmentInfo[3];
                    newPatientTask = new StandardPatientTask(tempPid, tempTid, temptime, taskAssignmentInfo[0]);
                }
            } else if (taskAssignmentInfo[0].equals("E")) {
                if (firstChar == '#') {
                    int tempPid = Integer.parseInt(taskAssignmentInfo[1].replace("#","").trim());
                    int tempTid = Integer.parseInt(taskAssignmentInfo[2]);
                    String stime = taskAssignmentInfo[3].split(" to ", 2)[0];
                    String etime = taskAssignmentInfo[3].split(" to ", 2)[1];
                    newPatientTask = new EventPatientTask(tempPid, tempTid, stime, etime,
                            taskAssignmentInfo[0]);
                } else {
                    int tempPid = patientList.getPatientByName(taskAssignmentInfo[1]).get(0).getID();
                    int tempTid = tasksList.getTaskByDescription(taskAssignmentInfo[2]).get(0).getID();
                    String stime = taskAssignmentInfo[3].split(" to ", 2)[0];
                    String etime = taskAssignmentInfo[3].split(" to ", 2)[1];
                    newPatientTask = new EventPatientTask(tempPid, tempTid, stime, etime,
                            taskAssignmentInfo[0]);
                }
            } else {
                throw new DukeException("Wrong format is detected!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DukeException("You are missing some information!");
        }

        if (patientList.isExist(newPatientTask.getPatientId()) && tasksList.doesExist(newPatientTask.getTaskID())) {
            if (patientTaskList.isIdExist(newPatientTask.getUid()) || patientTaskList.isSameTaskExist(newPatientTask)) {
                throw new DukeException("Either the unique task id is repeated or the same task exists");
            } else {
                patientTaskList.addPatientTask(newPatientTask);
                patientTaskStorage.save(patientTaskList.fullPatientTaskList());
                ui.patientTaskAssigned(newPatientTask, patientList.getPatient(newPatientTask.getPatientId()).getName(),
                        tasksList.getTask(newPatientTask.getTaskID()).getDescription());
            }
        } else {
            throw new DukeException("Either the patient or the task does not exist in our data record");
        }

    }

    /**
     * .
     *
     * @return .
     */
    @Override
    public boolean isExit() {
        return false;
    }
}