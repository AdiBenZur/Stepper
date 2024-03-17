package client.admin.GUI.body.history;

import client.admin.util.HttpAdminUtil;
import com.google.gson.Gson;
import dto.flow.ExecutionDetailsHeadlines;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import servlets.util.Constants;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class ExecutionsHistoryRefresher extends TimerTask {

    private Consumer<List<ExecutionDetailsHeadlines>> allExecutions;

    public ExecutionsHistoryRefresher(Consumer<List<ExecutionDetailsHeadlines>> allExecutionsDTO) {
        this.allExecutions = allExecutionsDTO;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.ALL_EXECUTIONS)
                .newBuilder()
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        HttpAdminUtil.runAsync(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String jasonArrayOfExecutions = response.body().string();
                    Gson gson = new Gson();
                    ExecutionDetailsHeadlines[] executions = gson.fromJson(jasonArrayOfExecutions, ExecutionDetailsHeadlines[].class);
                    allExecutions.accept(Arrays.asList(executions));
                }
            }
        });
    }
}
