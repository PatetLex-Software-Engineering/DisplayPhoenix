package com.patetlex.displayphoenix.util;

import com.patetlex.displayphoenix.Application;
import com.patetlex.displayphoenix.file.DetailedFile;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.LoadLibs;
import org.opencv.core.Core;

import java.io.File;

public class OpenCVHelper {

    public static final Tesseract TESSERACT = new Tesseract();

    public static void create() {
        try {
            String osName = System.getProperty("os.name");
            String opencvpath = null;
            if (osName.startsWith("Windows")) {
                int bitness = Integer.parseInt(System.getProperty("sun.arch.data.model"));
                if (bitness == 32) {
                    opencvpath = "/opencv/x86/";
                } else if (bitness == 64) {
                    opencvpath = "/opencv/x64/";
                } else {
                    opencvpath = "/opencv/x86/";
                }
            }
            DetailedFile file = FileHelper.storeTemporaryFile(OpenCVHelper.class.getResourceAsStream(opencvpath + Core.NATIVE_LIBRARY_NAME + ".dll"), StringHelper.id(Application.getTitle()) + "_" + Core.NATIVE_LIBRARY_NAME + ".dll");
            System.load(file.getFile().getPath());
            File tessdata = LoadLibs.extractTessResources("tessdata");
            TESSERACT.setDatapath(tessdata.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load opencv native library", e);
        }
    }
}
