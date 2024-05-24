package com.samsungcloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@Service
public class ResultWriter {
    private static final Logger logger = LoggerFactory.getLogger(ResultWriter.class);
    private final String path;

    @Autowired
    public ResultWriter(OutputFileConfig outputFileConfig) {
        this.path = outputFileConfig.getOutputDir() + "/" + outputFileConfig.getName();
        logger.info(String.format("result is written to %s", path));
    }

    public synchronized void writeResult(TaskResult taskResult) {
        try (PrintWriter out = new PrintWriter(new FileWriter(path, true))) {
            out.println(taskResult.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
