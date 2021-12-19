package corviolis.corviolisutils.services.api;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import corviolis.corviolisutils.CorviolisUtils;
import net.minecraft.entity.player.PlayerEntity;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class NocodbAPI {

    private static HttpClient client;
    private static String token;
    private static String url;

    public static void init() {
        client = HttpClient.newHttpClient();
        token = CorviolisUtils.settings.nocodbToken;
        url = CorviolisUtils.settings.nocodbUrl;
    }

    private static JsonElement get(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .setHeader("xc-auth", token)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return JsonParser.parseString(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void post(String url, JsonObject body) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .uri(URI.create(url))
                    .setHeader("Content-Type", "application/json")
                    .header("xc-auth", token)
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void delete(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(URI.create(url))
                    .header("xc-auth", token)
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void createReport(PlayerEntity reporter, GameProfile offender, String reason, String type) {
        JsonObject report = new JsonObject();
        report.addProperty("Reporter_Name", reporter.getEntityName());
        report.addProperty("Reporter_Id", reporter.getUuid().toString());
        report.addProperty("Offender_Name", offender.getName());
        report.addProperty("Offender_Id", offender.getId().toString());
        report.addProperty("Reason", reason);

        if (type.equals("report")) post(url + "/nc/admin_ywij/api/v1/Reports", report);
        if (type.equals("ban")) post(url + "/nc/admin_ywij/api/v1/Bans", report);
    }

    public static JsonArray getRules() {
        JsonElement json = get(url + "/nc/admin_ywij/api/v1/Rules");
        if (json != null) return json.getAsJsonArray();
        else return new JsonArray();
    }

    public static JsonArray getBans() {
        JsonElement json = get(url + "/nc/admin_ywij/api/v1/Bans");
        if (json != null) return json.getAsJsonArray();
        else return new JsonArray();
    }

    public static void removeBan(UUID offenderId) {
        for (JsonElement element : getBans()) {
            JsonObject ban = element.getAsJsonObject();
            if (ban.get("Offender_Id").getAsString().equals(offenderId.toString())) {
                delete(url + "/nc/admin_ywij/api/v1/Bans/" + ban.get("id").getAsString());
                break;
            }
        }
    }
}
