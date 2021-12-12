package corviolis.corviolisutils.services.api.airtable;

import com.sybit.airtable.Airtable;
import com.sybit.airtable.Base;
import com.sybit.airtable.Table;
import com.sybit.airtable.exception.AirtableException;
import corviolis.corviolisutils.CorviolisUtils;
import org.apache.http.client.HttpResponseException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class AirtableAPI {

    public static Base base;
    public static Table<Report> reports;
    public static Table<Report> bans;
    public static Table<Rule> rules;

    public static void init() {
        try {
            base = new Airtable().configure(CorviolisUtils.settings.airtableToken).base(CorviolisUtils.settings.airtableBaseId);

            reports = base.table(CorviolisUtils.settings.airtableReportsId, Report.class);
            bans = base.table(CorviolisUtils.settings.airtableBansId, Report.class);
            rules = base.table(CorviolisUtils.settings.airtableRulesId, Rule.class);
        } catch (AirtableException e) {
            e.printStackTrace();
        }
    }

    public static void createReport(String reporter, String offender, String reason, String type) {
        try {
            Report report = new Report();
            report.setReporter(reporter);
            report.setOffender(offender);
            report.setReason(reason);

            if (type.equals("report")) reports.create(report);
            if (type.equals("ban")) bans.create(report);
        } catch (AirtableException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Rule> getRules() {
        try {
            return (ArrayList<Rule>) rules.select();
        } catch (AirtableException | HttpResponseException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static ArrayList<Report> getBans() {
        try {
            return (ArrayList<Report>) bans.select();
        } catch (AirtableException | HttpResponseException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
