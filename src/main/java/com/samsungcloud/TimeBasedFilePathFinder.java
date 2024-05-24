package com.samsungcloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TimeBasedFilePathFinder implements FilePathFinder<LocalDateTime> {
    private static final Logger logger = LoggerFactory.getLogger(TimeBasedFilePathFinder.class);
    private final DirectoryConfig directoryConfig;

    @Autowired
    public TimeBasedFilePathFinder(DirectoryConfig directoryConfig) {
        this.directoryConfig = directoryConfig;
    }

    @Override
    public List<String> listFilePath(LocalDateTime based) {
        Path dirPath = convertToTimeBasedDirectory(based);
        List<String> files = getAllFileList(dirPath);

        while (files.size() < directoryConfig.getMaxFileSize()) {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(directoryConfig.getFilePollingSec()));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            logger.info(String.format("wait for adding file. current: %s%n", dirPath));
            files = getAllFileList(dirPath);
        }

        return files;
    }

    @Override
    public boolean existsDirectory(LocalDateTime dateTime) {
        Path dirPath = convertToTimeBasedDirectory(dateTime);
        return Files.exists(dirPath) && Files.isDirectory(dirPath);
    }

    private Path convertToTimeBasedDirectory(LocalDateTime dateTime) {
        String dirPath = directoryConfig.getBase() + "/" + DirNameConvertor.convertDateToDirectory(dateTime);
        return Paths.get(dirPath);
    }

    private List<String> getAllFileList(Path dirPath) {
        List<String> fileList = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    fileList.add(path.toString());
                }
            }
            return fileList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
