package com.samsungcloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class QueryExecutorRunner implements CommandLineRunner {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd'T'HH:mm");
    private static final Logger logger = LoggerFactory.getLogger(QueryExecutorRunner.class);

    private final TimeConfig timeConfig;
    private final FilePathFinder<LocalDateTime> filePathFinder;
    private final QueryRunner queryRunner;

    //    public Launcher(FilePathFinder filePathFinder, QueryRunner queryRunner) {
    public QueryExecutorRunner(TimeConfig timeConfig,
                               FilePathFinder<LocalDateTime> filePathFinder,
                               QueryRunner queryRunner) {
        this.filePathFinder = filePathFinder;
        this.timeConfig = timeConfig;
        this.queryRunner = queryRunner;
    }

    @Override
    public void run(String... args) {

        LocalDateTime dateTime = LocalDateTime.parse(timeConfig.getStartTime(), DATE_TIME_FORMATTER);
        List<String> fileList = filePathFinder.listFilePath(dateTime);

        if (!queryRunner.runQueryFromFiles(fileList)) {
            exitApplication();
        }

        LocalDateTime next = dateTime.plusMinutes(timeConfig.getMinuteInterval());
        while (filePathFinder.existsDirectory(next)) {

            fileList = filePathFinder.listFilePath(next);
            if (!queryRunner.runQueryFromFiles(fileList)) {
                exitApplication();
            }

            next = next.plusMinutes(timeConfig.getMinuteInterval());
            waitForNewDirectory(next);
        }
    }

    private void waitForNewDirectory(LocalDateTime dateTime) {
        while (!filePathFinder.existsDirectory(dateTime)) {
            try {
                logger.info(String.format("waiting for new directory: %s%n", dateTime));
                Thread.sleep(TimeUnit.SECONDS.toMillis(10));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void exitApplication() {

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(5));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.exit(1);
    }
}
