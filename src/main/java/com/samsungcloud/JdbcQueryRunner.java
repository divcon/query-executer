package com.samsungcloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

@Service
public class JdbcQueryRunner implements QueryRunner {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(30);

    private final JdbcTemplate jdbcTemplate;
    private final QueryConfig queryConfig;
    private final ResultWriter resultWriter;

    @Autowired
    public JdbcQueryRunner(JdbcTemplate jdbcTemplate,
                           QueryConfig queryConfig,
                           ResultWriter resultWriter) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryConfig = queryConfig;
        this.resultWriter = resultWriter;
    }

    @Override
    public boolean runQueryFromFiles(List<String> filePaths) {
        List<Callable<TaskResult>> tasks = new ArrayList<>();
        for (String filePath : filePaths) {
            tasks.add(() -> processFile(Paths.get(filePath)));
        }

        try {
            List<Future<TaskResult>> results = executorService.invokeAll(tasks);
            for (Future<TaskResult> result : results) {
                TaskResult taskResult = result.get();

                resultWriter.writeResult(taskResult);
                if (!taskResult.isSuccess()) {
                    executorService.shutdown();
                    return false;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            executorService.shutdown();
            throw new RuntimeException(e);
        }
        return true;
    }

    private TaskResult processFile(Path path) {
        StringBuilder message = new StringBuilder();
        message.append("Processing file: ").append(path.toString()).append(" - ");
        try (Stream<String> lines = Files.lines(path)) {
            List<List<String>> batchList = collectQueryAsBatchSize(lines);
            batchList.forEach(batch -> {
                String[] sqlQueries = batch.toArray(batch.toArray(new String[0]));
                jdbcTemplate.batchUpdate(sqlQueries);
            });
        } catch (IOException | RuntimeException e) {
            message.append("fail -");
            message.append(e.getMessage());
            return new TaskResult(false, message.toString());
        }
        message.append("Success");
        return new TaskResult(true, message.toString());
    }

    private List<List<String>> collectQueryAsBatchSize(Stream<String> lines) {
        List<List<String>> batches = new ArrayList<>();
        List<String> temp = new ArrayList<>();

        lines.filter(l -> !l.isEmpty())
                .forEach(l -> {
                    temp.add(l);
                    if (temp.size() == queryConfig.getBatchSize()) {
                        batches.add(new ArrayList<>(temp));
                        temp.clear();
                    }
                });
        if (!temp.isEmpty()) {
            batches.add(new ArrayList<>(temp));
        }
        return batches;
    }
}
