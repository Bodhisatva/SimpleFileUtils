package com.ricardolorenzo.file.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

import com.ricardolorenzo.file.lock.FileLock;
import com.ricardolorenzo.file.lock.FileLockException;
import com.ricardolorenzo.file.security.FileSummation;

public class FileUtils {
    public static final void copyFile(final File f1, final File f2) throws IOException, FileLockException {
        if (f1.exists() && f1.isDirectory()) {
            if (!f2.exists()) {
                f2.mkdirs();
            }
            for (File f : f1.listFiles()) {
                copyFile(f, new File(f2.getAbsolutePath() + File.separator + f.getName()));
            }
        } else if (f1.exists() && f1.isFile()) {
            FileLock fl = new FileLock(f2);
            try {
                fl.lock();
                InputStream is = new BufferedInputStream(new FileInputStream(f1));
                OutputStream os = new BufferedOutputStream(new FileOutputStream(f2));

                try {
                    IOStreamUtils.write(is, os);
                } finally {
                    IOStreamUtils.closeQuietly(is);
                    IOStreamUtils.closeQuietly(os);
                }
            } finally {
                fl.unlock();
            }
        }
    }

    public static void emptyFile(final File file) throws IOException, FileLockException {
        BufferedOutputStream os = null;
        FileLock fl = new FileLock(file);
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            fl.lock();
            os.write("".getBytes());
        } finally {
            fl.unlockQuietly();
            IOStreamUtils.closeQuietly(os);
        }
    }

    /**
     * Compress bytes into different formats
     * 
     * @throws NoSuchMethodException
     *             , IOException
     * */
    public static byte[] compress(final int type, final byte[] input) throws IOException, NoSuchMethodException {
        ByteArrayInputStream is = new ByteArrayInputStream(input);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            IOStreamUtils.compress(type, is, os);
        } finally {
            IOStreamUtils.closeQuietly(is);
            IOStreamUtils.closeQuietly(os);
        }
        return os.toByteArray();
    }

    /**
     * Decompress bytes into different formats
     * 
     * @throws NoSuchMethodException
     *             , IOException
     * */
    public static void decompress(final int type, final File input, final File output) throws IOException,
            NoSuchMethodException {
        FileInputStream is = new FileInputStream(input);
        FileOutputStream os = new FileOutputStream(output);
        try {
            IOStreamUtils.compress(type, is, os);
        } finally {
            IOStreamUtils.closeQuietly(is);
            IOStreamUtils.closeQuietly(os);
        }
    }

    /**
     * Decompress bytes into different formats
     * 
     * @throws NoSuchMethodException
     *             , IOException
     * */
    public static byte[] decompress(final int type, final byte[] input) throws IOException, NoSuchMethodException {
        ByteArrayInputStream is = new ByteArrayInputStream(input);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            IOStreamUtils.compress(type, is, os);
        } finally {
            IOStreamUtils.closeQuietly(is);
            IOStreamUtils.closeQuietly(os);
        }
        return os.toByteArray();
    }

    /**
     * Compress bytes into different formats
     * 
     * @throws NoSuchMethodException
     *             , IOException
     * */
    public static void compress(final int type, final File input, final File output) throws IOException,
            NoSuchMethodException {
        FileInputStream is = new FileInputStream(input);
        FileOutputStream os = new FileOutputStream(output);
        try {
            IOStreamUtils.compress(type, is, os);
        } finally {
            IOStreamUtils.closeQuietly(is);
            IOStreamUtils.closeQuietly(os);
        }
    }

    public static byte[] readFile(final File file) throws IOException {
        FileInputStream is = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            is = new FileInputStream(file);
            IOStreamUtils.write(is, os);
        } finally {
            IOStreamUtils.closeQuietly(is);
            IOStreamUtils.closeQuietly(os);
        }
        return os.toByteArray();
    }

    public static String readFileAsString(final File file) throws IOException {
        return new String(readFile(file));
    }

    public static boolean updateFile(final File f1, final File f2) throws FileNotFoundException,
            NoSuchAlgorithmException, IOException, FileLockException {
        if (f1.exists()) {
            if (!FileSummation.compare(f1, f2)) {
                copyFile(f1, f2);
                return true;
            }
        }
        return false;
    }

    public static boolean writeFile(final File file, final byte[] content) throws IOException, FileLockException {
        if (content == null) {
            return false;
        }

        FileOutputStream os = null;
        FileLock fl = new FileLock(file);
        try {
            fl.lock();
            ByteArrayInputStream is = new ByteArrayInputStream(content);
            try {
                os = new FileOutputStream(file);
                IOStreamUtils.write(is, os);
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                IOStreamUtils.closeQuietly(is);
                IOStreamUtils.closeQuietly(os);
            }
        } finally {
            fl.unlock();
        }
    }

    public static boolean writeFile(final File file, final String content) throws IOException, FileLockException {
        if (content == null) {
            return false;
        }
        return writeFile(file, content.getBytes());
    }
}