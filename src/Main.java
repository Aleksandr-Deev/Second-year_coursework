import tack.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Scanner;

public class Main {

    public static final Schedule SCHEDULE = new Schedule();
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d.M.yyyy");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            label:
            while (true) {
                printMenu();
                System.out.print("Выберите пункт меню: ");
                if (scanner.hasNextInt()) {
                    int menu = scanner.nextInt();
                    switch (menu) {
                        case 1:
                            addTask(scanner);
                            break;
                        case 2:
                            removeTasks(scanner);
                            break;
                        case 3:
                            printTaskForDate(scanner);
                            break;
                        case 0:
                            break label;
                    }
                } else {
//                    scanner.next();
                    System.out.println("Выберите пункт меню из списка!");
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println(
                """
                        1. Добавить задачу
                        2. Удалить задачу
                        3. Получить задачу на указанный день
                        0. Выход
                        """
        );
    }

    private static void addTask(Scanner scanner) {
        String title = readString("Введите название задачи: ", scanner);
        String description = readString("Введиде описание задачи: ", scanner);
        LocalDateTime tackDate = readDateTime(scanner);
        TaskType taskType = readType(scanner);
        Repeatability repeatability = readRepeatability(scanner);
        Task task = switch (repeatability) {
            case SINGLE -> new SingleTask(title, description, tackDate, taskType);
            case DAILY -> new DailyTask(title, description, tackDate, taskType);
            case WEEKLY -> new WeeklyTask(title, description, tackDate, taskType);
            case MONTHLY -> new MonthlyTask(title, description, tackDate, taskType);
            case YEARLY -> new YearlyTask(title, description, tackDate, taskType);
        };
        SCHEDULE.addTask(task);
    }

    private static Repeatability readRepeatability(Scanner scanner) {
        while (true) {
            try {
                System.out.print("Выберите тип повторяемости задачи: ");
                for (Repeatability repeatability : Repeatability.values()) {
                    System.out.println(repeatability.ordinal() + ". " + localizeRepeatability(repeatability));
                }
                System.out.print("Введите тип: ");
                String ordinalLine = scanner.nextLine();
                int ordinal = Integer.parseInt(ordinalLine);
                return Repeatability.values()[ordinal];
            } catch (NumberFormatException e) {
                System.out.println("Введен неверный номер типа задачи");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Тип задачи не найден");
            }
        }
    }

    private static TaskType readType(Scanner scanner) {
        while (true) {
            try {
                System.out.println("Выберите тип задачи: ");
                for (TaskType taskType : TaskType.values()) {
                    System.out.println(taskType.ordinal() + ". " + localizeType(taskType));
                }
                System.out.print("Введите тип: ");
                String ordinalLine = scanner.nextLine();
                int ordinal = Integer.parseInt(ordinalLine);
                return TaskType.values()[ordinal];
            } catch (NumberFormatException e) {
                System.out.println("Введен неверный номер типа задачи");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Тип задачи не найден");
            }
        }
    }

    private static LocalDateTime readDateTime(Scanner scanner) {
        LocalDate localDate = readDate(scanner);
        LocalTime localTime = readTime(scanner);
        return localDate.atTime(localTime);
    }

    private static String readString(String message, Scanner scanner) {
        while (true) {
                System.out.print(message);
                String readString = scanner.nextLine();
            if (readString == null || readString.isBlank()) {
                System.out.println("Введено пустое значение");
            } else return readString;
        }
    }

    private static void removeTasks(Scanner scanner) {
        System.out.println("Все задачи: ");
        for (Task task : SCHEDULE.getAllTasks()) {
            System.out.printf("%d. %s [%s](%s)%n",
                    task.getId(),
                    task.getTitle(),
                    localizeType(task.getTaskType()),
                    localizeRepeatability(task.getRepeatabilityType()));
        }
        while (true) {
            try {
                System.out.print("Выберите задачу которую надо удалить: ");
                String idLine = scanner.nextLine();
                int id = Integer.parseInt(idLine);
                SCHEDULE.removeTAsk(id);
                System.out.println("Задача удалена.");
                break;
            } catch (NumberFormatException e) {
                System.out.println("Введен неверный id задачи");
            } catch (TaskNotFoundException e) {
                System.out.println("Задача для удаления не найдена");
            }
        }
    }

    private static void printTaskForDate(Scanner scanner) {
        LocalDate localDate = readDate(scanner);
        Collection<Task> tasksForDAte = SCHEDULE.getTaskForDay(localDate);
        System.out.println("Задачи на " + localDate.format(DATE_FORMAT));
        for (Task task : tasksForDAte) {
            System.out.printf("[%s] %s: %s (%s)%n" ,
                    localizeType(task.getTaskType()),
                    task.getTitle(),
                    task.getTaskDateTime().format(TIME_FORMAT),
                    task.getDescription());
        }
    }

    private static LocalDate readDate(Scanner scanner) {
        while (true) {
            try {
                System.out.print("Введите дату задачи в формате dd.MM.yyyy: ");
                String dateLine = scanner.nextLine();
                return LocalDate.parse(dateLine, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Введена дата в неверном формате");
            }
        }

    }

    private static LocalTime readTime(Scanner scanner) {
        while (true) {
            try {
                System.out.print("Введите время задачи в формате hh:mm: ");
                String dateLine = scanner.nextLine();
                return LocalTime.parse(dateLine, TIME_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Введена дата в неверном формате");
            }
        }

    }

    private static String localizeType(TaskType taskType) {
        return switch (taskType) {
            case WORK -> "Рабочая задача";
            case PERSONAL -> "Персональная задача";
        };
    }

    private static String localizeRepeatability(Repeatability repeatability) {
        return switch (repeatability) {
            case SINGLE -> "Разовая";
            case DAILY -> "Ежедневная";
            case WEEKLY -> "Еженедельная";
            case MONTHLY -> "Ежемесечная";
            case YEARLY -> "Ежегодная";
        };
    }
}