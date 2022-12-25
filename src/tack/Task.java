package tack;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class Task implements Comparable<Task> {

    private static int counter = 0;
    private final int id;
    private final String title;
    private final String description;
    private final LocalDateTime taskDateTime;
    private final TaskType taskType;

    public Task(String title, String description, LocalDateTime taskDateTime, TaskType taskType) {
        this.id = counter++;
        this.title = title;
        this.description = description;
        this.taskDateTime = taskDateTime;
        this.taskType = taskType;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTaskDateTime() {
        return taskDateTime;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public abstract boolean appearsIn(LocalDate localDate);

    public abstract Repeatability getRepeatabilityType();

    @Override
    public int compareTo(Task otherTask) {
        if (otherTask == null) {
            return 0;
        }
        return this.taskDateTime.toLocalTime().compareTo(otherTask.taskDateTime.toLocalTime());
    }
}
