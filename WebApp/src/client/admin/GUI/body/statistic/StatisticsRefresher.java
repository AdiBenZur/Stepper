package client.admin.GUI.body.statistic;

import client.admin.util.HttpAdminUtil;
import com.google.gson.Gson;
import dto.statistics.StatisticsDTO;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import servlets.util.Constants;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

public class StatisticsRefresher extends TimerTask {

    private Consumer<StatisticsDTO> statisticsConsumer;

    public StatisticsRefresher(Consumer<StatisticsDTO> statisticsConsumer) {
        this.statisticsConsumer = statisticsConsumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.STATISTICS)
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
                    String statJson = response.body().string();
                    Gson gson = new Gson();
                    StatisticsDTO statisticsDTO = gson.fromJson(statJson, StatisticsDTO.class);
                    statisticsConsumer.accept(statisticsDTO);
                }
            }
        });
    }
}
