package com.patetlex.displayphoenix.interfaces;

import java.io.File;
import java.io.InputStream;

public interface FileIteration {
    default void iterate(File file) {

    }
    default void iterate(String directory, InputStream stream) {

    }
}
