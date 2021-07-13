package Main;

import Main.ErrorPage.ERRORS;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;

public class Requests {

    /**
     * The client we're currently connected to.
     */
    private final HttpClient client = HttpClient.newHttpClient();
    /**
     * The request that is wanted to be sent.
     */
    private HttpRequest request;
    /**
     * The current discord api URL.
     */
    private final String api = "https://discord.com/api/v9/";
    private int count = 0;

    public void BEGIN(Boolean channel) throws IOException {
        if (Config.amountOfMessages != 0) {
            Neutral.openProgress();
        } else {
            if (Config.errorPanel == null || !Config.errorPanel.getScene().getWindow().isShowing()) {
                Neutral.error(ERRORS.ERROR_TITLE_NO_MESSAGES, ERRORS.ERROR_MESSAGE_NO_MESSAGES);
            }
        }

        new Thread(() -> {
            try {
                Platform.runLater(() -> Config.status.setText("status: beginning..."));
                if (channel) {
                    DELETE_MESSAGES();
                } else {
                    DELETE_ALL_MESSAGES();
                }
            } catch (IOException | InterruptedException ex) {
                try {
                    if (Config.errorPanel == null || !Config.errorPanel.getScene()
                            .getWindow().isShowing()) {
                        Neutral.error(ERRORS.ERROR_TITLE_SOMETHING_WENT_WRONG,
                                ERRORS.ERROR_MESSAGE_DETAILS);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * This method simply retrieves messages in the channel.
     *
     * @param query The query sent by the user.
     * @return Returns the result of the sent request.
     */
    public final ArrayList<Long> GET(final String query) {
        String limit = "limit=" + 100;
        request = HttpRequest.newBuilder().uri(URI.create(
                api + "channels/" + Config.channelID + "/messages" + query + limit)).
                header("authorization", Config.code).GET().build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).
                thenApply(HttpResponse::body).thenApply(Requests::parseChannel).join();
    }

    public void GET_MESSAGES_BETWEEN(final Long message, final Object end) throws IOException {
        ArrayList<Long> messages = GET("?after=" + end + "&");

        if (message != null && messages.size() != 0) {
            if (!messages.contains(message)) {
                while (!messages.contains(message)) {
                    long lastLong = Config.nextSet.getJSONObject(0).getLong("id");
                    ArrayList<Long> data = GET("?after=" + lastLong + "&");
                    if (data.contains(message)) {
                        data.subList(0, data.indexOf(message)).clear();
                    }
                    messages.addAll(data);
                }
            } else {
                messages.subList(0, messages.indexOf(message)).clear();
            }
            messages.add((Long) end);
        } else {
            if (Config.errorPanel == null || !Config.errorPanel.getScene().getWindow().isShowing()) {
                Neutral.error(ERRORS.ERROR_TITLE_NO_MESSAGES, ERRORS.ERROR_MESSAGE_NO_MESSAGES);
            }
        }

        Config.messages = messages;
        Config.amountOfMessages = messages.size();
        BEGIN(true);
    }

    /**
     * This method simply retrieves messages in the channel and parses them using dates.
     *
     * @param query The query sent by the user.
     * @return Returns the result of the sent request.
     */
    public final ArrayList<ArrayList<Long>> GET_TIMED(final String query) {
        request = HttpRequest.newBuilder().uri(URI.create(
                api + "channels/" + Config.channelID + "/messages" + query + "limit=100")).
                header("authorization", Config.code).GET().build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).
                thenApply(HttpResponse::body).thenApply(Requests::parseMessage).join();
    }

    public void GET_ALL_MESSAGES() {
        ArrayList<Long> messages = Config.request.GET("?");
        if (messages.size() != 0) {
            int current;
            do {
                current = messages.size();
                long lastLong = Config.nextSet.getJSONObject(Config.nextSet.length() - 1).getLong("id");
                messages.addAll(Config.request.GET("?before=" + lastLong + "&"));
            } while (current != messages.size());

            Config.messages.addAll(messages);
            Config.amountOfMessages = Config.messages.size();
        }
    }

    /**
     * This method simply retrieves messages in the guild.
     *
     * @return Returns the result of the sent request
     */
    public final ArrayList<Long> GET_CHANNELS() {
        request = HttpRequest.newBuilder().uri(URI.create(
                api + "guilds/" + Config.guildID + "/channels")).
                header("authorization", Config.code).GET().build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).
                thenApply(HttpResponse::body).thenApply(Requests::parseGuild).join();
    }

    /**
     * This method simply deletes the passed message.
     *
     * @param message The message the is wanted to be deleted
     * @return Returns the response status code
     */
    public final int DELETE(final long id, final long message)
            throws IOException, InterruptedException {
        request = HttpRequest.newBuilder().uri(URI.create(
                api + "channels/" + id + "/messages/" + message)).
                header("authorization", Config.code).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 429) {
            getRetryTime(response.body());
        }
        return response.statusCode();
    }

    public final void DELETE_ALL_MESSAGES() throws IOException, InterruptedException {
        if (Config.messages.size() != 0 && !Config.cancel && Config.channelIDs.size() != 0) {
            final long item = Config.messages.get(0);
            int resp = DELETE(Config.channelIDs.get(count), item);

            if (resp == 204) {
                double result = (double) (Config.amountOfMessages - (Config.messages.size() - 1))
                        / Config.amountOfMessages * 100;

                Platform.runLater(() -> {
                    Config.deletedList.getItems().add("ID: " + item + " - " + (int) result + "%");
                    Config.progressBar.setProgress(result / 100);
                    Config.status.setText("status: deleting messages...");
                });

                Config.messages.remove(item);
            } else if (resp == 429) {
                Platform.runLater(() -> Config.status.setText("status: awaiting API..."));
                TimeUnit.MILLISECONDS.sleep((long) Config.retry);
            } else if (resp == 404) {
                Platform.runLater(() -> Config.status.setText("status: getting other messages"));
                if (count == Config.channelIDs.size() - 1) {
                    count = 0;
                } else {
                    count++;
                }
            } else {
                Config.messages.remove(item);
            }

            if (Config.messages.size() != 0) {
                DELETE_ALL_MESSAGES();
            } else {
                Platform.runLater(() -> Config.status.setText("status: completed"));
            }
        }
    }

    public final void DELETE_MESSAGES() throws IOException, InterruptedException {
        if (Config.messages.size() != 0 && !Config.cancel) {
            final long item = Config.messages.get(0);
            int resp = DELETE(Config.channelID, item);

            if (resp == 204) {
                double result = (double) (Config.amountOfMessages - (Config.messages.size() - 1))
                        / Config.amountOfMessages * 100;

                Platform.runLater(() -> {
                    Config.deletedList.getItems().add("ID: " + item + " - " + (int) result + "%");
                    Config.progressBar.setProgress(result / 100);
                    Config.status.setText("status: deleting messages...");
                });

                Config.messages.remove(item);
            } else if (resp == 429) {
                Platform.runLater(() -> Config.status.setText("status: awaiting API..."));
                TimeUnit.MILLISECONDS.sleep((long) Config.retry);
            } else {
                Config.messages.remove(item);
            }

            if (Config.messages.size() != 0) {
                DELETE_MESSAGES();
            } else {
                Platform.runLater(() -> Config.status.setText("status: completed"));
            }
        }
    }

    /**
     * This method simply retrieves the username and avatar of the user.
     *
     * @param body The body of the returned headers
     * @return Returns user information.
     */
    public static ArrayList<String> parseUser(final String body) {
        JSONObject data = new JSONObject(body);
        ArrayList<String> user = new ArrayList<>();
        user.add(data.getString("username"));
        user.add(data.getString("avatar"));
        return user;
    }

    /**
     * This method simply retrieves the messages with the timestamps.
     *
     * @param body The body of the returned headers
     * @return Returns the message IDs.
     */
    public static ArrayList<ArrayList<Long>> parseMessage(final String body) {
        JSONArray data = new JSONArray(body);
        Config.nextSet = data;
        ArrayList<ArrayList<Long>> ids = new ArrayList<>();
        ids.add(new ArrayList<>());
        ids.add(new ArrayList<>());
        boolean foundStart = false;
        boolean foundEnd = false;

        for (int i = 0; i < data.length(); i++) {
            JSONObject element = data.getJSONObject(i);
            if (element.getJSONObject("author").getLong("id") == Config.authorID) {
                long id;
                if (Config.startDateString != null) {
                    if (element.getString("timestamp").contains(Config.startDateString)) {
                        foundStart = true;
                        id = element.getLong("id");
                        ids.get(0).add(id);
                    }
                }
                if (Config.endDateString != null) {
                    if (element.getString("timestamp").contains(Config.endDateString)) {
                        foundEnd = true;
                        id = element.getLong("id");
                        ids.get(1).add(id);
                    }
                }
            }
        }

        if (foundStart) {
            Config.foundStart = true;
        }
        if (foundEnd) {
            Config.foundEnd = false;
        }

        return ids;
    }

    /**
     * This method simply retrieves the messages in the channel.
     *
     * @param body The body of the returned headers
     * @return Returns the message IDs.
     */
    public static ArrayList<Long> parseChannel(final String body) {
        JSONArray data = new JSONArray(body);
        ArrayList<Long> ids = new ArrayList<>();
        Config.nextSet = data;

        for (int i = 0; i < data.length(); i++) {
            JSONObject element = data.getJSONObject(i);
            if (element.getJSONObject("author").getLong("id") == Config.authorID) {
                long id = element.getLong("id");
                ids.add(id);
            }
        }
        return ids;
    }

    /**
     * This method simply retrieves the messages in the guild.
     *
     * @param body The body of the returned headers
     * @return Returns the message IDs.
     */
    public static ArrayList<Long> parseGuild(final String body) {
        JSONArray data = new JSONArray(body);
        ArrayList<Long> ids = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {
            JSONObject element = data.getJSONObject(i);
            if (element.getLong("guild_id") == Config.guildID) {
                long id = element.getLong("id");
                ids.add(id);
            }
        }
        return ids;
    }

    /**
     * This method simply sets the retry time.
     *
     * @param body The body of the returned headers
     */
    public static void getRetryTime(final String body) {
        JSONObject data = new JSONObject(body);
        double time = data.getDouble("retry_after");
        Config.retry = time * 1000;
    }

}
