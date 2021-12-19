package corviolis.corviolisutils.util;

import mc.microconfig.Comment;
import mc.microconfig.ConfigData;

public class Settings implements ConfigData {

    public String databaseName = "CorviolisUtils";
    public String databaseDirectory = "/my/dir";

    public String todoistToken = "xxx";

    public String nocodbToken = "xxx";
    public String nocodbUrl = "xxx";

    @Comment("delay in milliseconds - no longer than 9 digits - default is 24 hours")
    public int reportDelay = 86400000;
}
