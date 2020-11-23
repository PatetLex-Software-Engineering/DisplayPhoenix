package net.displayphoenix.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author TBroski
 */
public class FileHelper {
    public static void deleteFolder(File directory) {
        for (File subFile : directory.listFiles()) {
            if(subFile.isDirectory()) {
                deleteFolder(subFile);
            }
            else {
                subFile.delete();
            }
        }
        directory.delete();
    }

    public static String readAllLines(URI uri) {
        return readAllLines(new File(uri));
    }

    public static String readAllLines(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            StringBuilder output = new StringBuilder();
            String out;
            while ((out = reader.readLine()) != null) {
                output.append(out + "\n");
            }
            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void changeFilePermissions(File file, PosixFilePermission... permissions) {
        Set<PosixFilePermission> permissionSet = new HashSet<>();
        for (PosixFilePermission permission : permissions) {
            permissionSet.add(permission);
        }
        try {
            Files.setPosixFilePermissions(file.toPath(), permissionSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void changeFileAdministrator(File file) {
        Set<PosixFilePermission> permissionSet = new HashSet<>();
        permissionSet.add(PosixFilePermission.OWNER_READ);
        permissionSet.add(PosixFilePermission.OWNER_WRITE);
        permissionSet.add(PosixFilePermission.OWNER_EXECUTE);

        permissionSet.add(PosixFilePermission.OTHERS_READ);
        permissionSet.add(PosixFilePermission.OTHERS_WRITE);
        permissionSet.add(PosixFilePermission.OTHERS_EXECUTE);

        permissionSet.add(PosixFilePermission.GROUP_READ);
        permissionSet.add(PosixFilePermission.GROUP_WRITE);
        permissionSet.add(PosixFilePermission.GROUP_EXECUTE);

        try {
            Files.setPosixFilePermissions(file.toPath(), permissionSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
