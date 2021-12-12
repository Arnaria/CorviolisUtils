package corviolis.corviolisutils.services.api.airtable;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Report {

    private String id;
    private String createdTime;
    private String created;

    @SerializedName("Reporter")
    private String reporter;
    @SerializedName("Reporter Id")
    private String reporterUuid;
    @SerializedName("Offender")
    private String offender;
    @SerializedName("Offender Id")
    private String offenderUuid;
    @SerializedName("Reason")
    private String reason;

    public void setId(String id) {
        this.id = id;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public void setReporterUuid(String reporterUuid) {
        this.reporterUuid = reporterUuid;
    }

    public void setOffender(String offender) {
        this.offender = offender;
    }

    public void setOffenderUuid(String offenderUuid) {
        this.offenderUuid = offenderUuid;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getCreated() {
        return created;
    }

    public String getReporter() {
        return reporter;
    }

    public UUID getReporterUuid() {
        return UUID.fromString(reporterUuid);
    }

    public String getOffender() {
        return offender;
    }

    public UUID getOffenderUuid() {
        return UUID.fromString(offenderUuid);
    }

    public String getReason() {
        return reason;
    }
}
