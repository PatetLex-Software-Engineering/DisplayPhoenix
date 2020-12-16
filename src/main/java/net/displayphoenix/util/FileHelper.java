package net.displayphoenix.util;

import net.displayphoenix.interfaces.FileIteration;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
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

    public static final File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));

    public static void forEachSubFile(File directory, FileIteration iterator) {
        for (File subFile : directory.listFiles()) {
            if (subFile.isDirectory()) {
                forEachSubFile(directory, iterator);
            }
            else {
                iterator.iterate(subFile);
            }
        }
    }

    public static void deleteFolder(File directory) {
        for (File subFile : directory.listFiles()) {
            if (subFile.isDirectory()) {
                deleteFolder(subFile);
            }
            else {
                subFile.delete();
            }
        }
        directory.delete();
    }

    public static String readAllLines(URI uri) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(uri.toURL().openStream()));

            StringBuilder output = new StringBuilder();
            String out;
            while ((out = reader.readLine()) != null) {
                output.append(out + "\n");
            }
            reader.close();
            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readAllLines(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            StringBuilder output = new StringBuilder();
            String out;
            while ((out = reader.readLine()) != null) {
                output.append(out + "\n");
            }
            reader.close();
            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readAllLines(InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder output = new StringBuilder();
            String out;
            while ((out = reader.readLine()) != null) {
                output.append(out + "\n");
            }
            reader.close();
            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void changeFilePermissions(File file, PosixFilePermission... permissions) {
        if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
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
    }

    public static void changeFileAdministrator(File file) {
        if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
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

    public static void storeTemporaryFile(File file) {
        try {
            storeTemporaryFile(Files.readAllBytes(file.toPath()), file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String storeTemporaryFile(InputStream inputStream, String name) {
        return storeTemporaryFile(readAllBytesFromStream(inputStream), name);
    }

    public static String storeTemporaryFile(byte[] content, String name) {
        try {
            File tempFile = new File(TEMP_DIRECTORY.getPath() + "/" + name);
            tempFile.createNewFile();
            OutputStream fileWriter = new FileOutputStream(tempFile);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
            return tempFile.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] readAllBytesFromStream(InputStream inputStream) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
