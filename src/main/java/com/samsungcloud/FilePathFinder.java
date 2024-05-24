package com.samsungcloud;

import java.util.List;

public interface FilePathFinder<T> {
    List<String> listFilePath(T based);
    boolean existsDirectory(T based);
}
