package tools;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * File utilities
 *
 * @version $Revision: 691982 $
 */
public final class FileTools {

    /**
     * Zip up a directory
     *
     * @param directory
     * @param zipName
     * @param deleteAfterZip
     * @throws IOException
     */
    public static void zipDir(String directory, String zipName, boolean deleteAfterZip) throws IOException {
        // create a ZipOutputStream to zip the data to
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipName))) {
            String path = "";
            zipDir(directory, zos, path);
            // close the stream
        }
        if (deleteAfterZip) {
            deleteDir(new File(directory));
        }
    }

    /**
     * Zip up a directory path
     *
     * @param directory
     * @param zos
     * @param path
     * @throws IOException
     */
    public static void zipDir(String directory, ZipOutputStream zos, String path) throws IOException {
        File zipDir = new File(directory);
        // get a listing of the directory content
        String[] dirList = zipDir.list();
        byte[] readBuffer = new byte[2156];
        int bytesIn = 0;
        // loop through dirList, and zip the files
        for (String dirList1 : dirList) {
            File f = new File(zipDir, dirList1);
            if (f.isDirectory()) {
                String filePath = f.getPath();
                zipDir(filePath, zos, path + f.getName() + "/");
                continue;
            }
            try (FileInputStream fis = new FileInputStream(f)) {
                ZipEntry anEntry = new ZipEntry(path + f.getName());
                zos.putNextEntry(anEntry);
                bytesIn = fis.read(readBuffer);
                while (bytesIn != -1) {
                    zos.write(readBuffer, 0, bytesIn);
                    bytesIn = fis.read(readBuffer);
                }
            }
        }
    }

    /**
     * @param zipFile
     * @param toDir
     * @throws java.io.IOException
     */
    public static void unzipIntoDirectory(ZipFile zipFile, File toDir) throws IOException {
        Enumeration files = zipFile.entries();
        File f;
        FileOutputStream fos;

        while (files.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) files.nextElement();
            InputStream eis = zipFile.getInputStream(entry);
            byte[] buffer = new byte[1024];
            int bytesRead = 0;

            f = new File(toDir.getAbsolutePath() + File.separator + entry.getName());

            if (entry.isDirectory()) {
                f.mkdirs();
                continue;
            } else {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            fos = new FileOutputStream(f);
            while ((bytesRead = eis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.close();
        }
    }

    /**
     * Empty and delete a folder (and subfolders).
     *
     * @param folder folder to empty
     */
    public static void deleteDir(final File folder) {
        // check if folder file is a real folder
        if (folder.isDirectory()) {
            File[] list = folder.listFiles();
            if (list != null) {
                for (File tmpF : list) {
                    if (tmpF.isDirectory()) {
                        deleteDir(tmpF);
                    }
                    tmpF.delete();
                }
            }
            if (!folder.delete()) {
                System.err.println("can't delete folder : " + folder);
            }
        }
    }
    
    public static void main(String args[]) throws IOException {
        ZipFile zf = new ZipFile("/home/alilo/Development/Projects/NetBeans/GeCom/eTajer/Backup/03.05.2015@04.36.tajer");
        File tmpDir = new File("/home/alilo/tmpDB");
        FileTools.unzipIntoDirectory(zf, tmpDir);
    }
}
