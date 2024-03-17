package client.admin.GUI.body.user.management;

import client.user.util.HttpUserUtil;
import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import servlets.util.Constants;
import user.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class UserListRefresher extends TimerTask {

    private Consumer<List<User>> usersListConsumer;


    public UserListRefresher(Consumer<List<User>> usersListConsumer) {
        this.usersListConsumer = usersListConsumer;
    }


    @Override
    public void run() {

        String finalUrl = HttpUrl
                .parse(Constants.USERS_LIST)
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
                String jsonArrayOfUsersNames = response.body().string();
                Gson gson = new Gson();
                User[] usersNames = gson.fromJson(jsonArrayOfUsersNames, User[].class);
                usersListConsumer.accept(Arrays.asList(usersNames));
            }
        });
    }
}
