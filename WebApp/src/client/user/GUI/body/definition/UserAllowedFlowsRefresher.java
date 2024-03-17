package client.user.GUI.body.definition;

import client.user.util.HttpUserUtil;
import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import servlets.util.Constants;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class UserAllowedFlowsRefresher extends TimerTask {

    private Consumer<List<String>> allowedFlowsListConsumer;

    public UserAllowedFlowsRefresher(Consumer<List<String>> allowedFlowsListConsumer) {
        this.allowedFlowsListConsumer = allowedFlowsListConsumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.ALLOWED_FLOWS)
                .newBuilder()
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        HttpUserUtil.runAsync(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfUsersAllowedFlowNames = response.body().string();
                Gson gson = new Gson();
                String[] usersNames = gson.fromJson(jsonArrayOfUsersAllowedFlowNames, String[].class);
                allowedFlowsListConsumer.accept(Arrays.asList(usersNames));
            }
        });
    }
}
