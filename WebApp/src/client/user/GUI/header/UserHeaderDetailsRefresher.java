package client.user.GUI.header;

import client.user.util.HttpUserUtil;
import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import servlets.util.Constants;
import user.User;
import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

public class
UserHeaderDetailsRefresher extends TimerTask {

    private Consumer<User> userConsumer;

    public UserHeaderDetailsRefresher(Consumer<User> userConsumer) {
        this.userConsumer = userConsumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.USERS)
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
                String jasonUser = response.body().string();
                Gson gson = new Gson();
                User user = gson.fromJson(jasonUser, User.class);
                userConsumer.accept(user);
            }
        });
    }
}
