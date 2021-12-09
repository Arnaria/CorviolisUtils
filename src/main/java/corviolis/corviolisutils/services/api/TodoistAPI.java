package corviolis.corviolisutils.services.api;

import corviolis.corviolisutils.CorviolisUtils;
import mrnavastar.todoistapi.api.Project;
import mrnavastar.todoistapi.api.Todoist;

public class TodoistAPI {

    public static Todoist todoist;
    public static Project project;

    public static void init() {
        todoist = new Todoist(CorviolisUtils.settings.todoistToken);
    }

    public static void sync() {
        todoist.sync();
        project = todoist.getProject("Admin");
    }

    public static void createReport(String reporter, String target, String description) {
        sync();
        project.createTask(reporter + " -> " + target)
                .setDescription(description)
                .setSection("Issues")
                .setPriority(4);
        todoist.commit();
    }
}
