package duke.core;

import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.ToDo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Represents a Storage class that deals with reading tasks from
 * a file and saving tasks in the file.
 */
public class Storage {
    /**
     * A string that represents a relative file path from the project folder.
     */
    private String filePath;
    /**
     * Constructs a Storage object with a specific file path.
     * @param path A string that represents the path of the file to read or
     * write.
     */
    public Storage(String path) {
        this.filePath = path;
    }
    /**
     * Read tasks from the file and store into a ArrayList of task.
     * @return A ArrayList of tasks from the file.
     * @throws DukeExceptionThrow If file is not found.
     */
    public ArrayList<Task> load() throws DukeExceptionThrow
    {
        File newDuke = new File(filePath);
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            Scanner ss = new Scanner(newDuke);
            while (ss.hasNext()) {
                String[] newTask = ss.nextLine().split(" \\| ");
                if (newTask[0].equals("T"))
                {
                    Task x = new ToDo(newTask[2]);
                    if (newTask[1].equals("1"))
                    {
                        x.markAsDone();
                    }
                    tasks.add(x);
                }
                if (newTask[0].equals("D"))
                {
                    Task t = new Deadline(newTask[2], newTask[3]);
                    if (newTask[1].equals("1"))
                    {
                        t.markAsDone();
                    }
                    tasks.add(t);
                }
                if (newTask[0].equals("E"))
                {
                    Task t = new Event(newTask[2], newTask[3]);
                    if (newTask[1].equals("1"))
                    {
                        t.markAsDone();
                    }
                    tasks.add(t);
                }

            }
            return tasks;
        }
        catch (FileNotFoundException e)
        {
            throw new DukeExceptionThrow("File is not found!");
        }
    }
    /**
     * Saves tasks to the local file.
     * @param task The TaskList storing tasks.
     * @throws DukeExceptionThrow If writing to the local file failed.
     */
    public void save(ArrayList<Task> task) {
        try {
            FileWriter ww = new FileWriter("./data/duke.txt");
            for (Task t : task)
            {
                ww.write(t.writeTxt() + System.lineSeparator());
            }
            ww.close();
        } catch (IOException e)
        {
            System.out.println("File writing process encounters an error " + e.getMessage());
        }
    }

}