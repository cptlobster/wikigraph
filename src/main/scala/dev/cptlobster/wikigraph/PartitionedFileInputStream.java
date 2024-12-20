package dev.cptlobster.wikigraph;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * FileInputStream that handles multiple partitions of one contiguous file as one big "file".
 */
public class PartitionedFileInputStream extends FileInputStream {
    private ArrayList<String> files;
    private int fileIndex = 0;
    private FileInputStream currentStream;
    public PartitionedFileInputStream(ArrayList<String> files) {
        this.files = files;
        this.currentStream = getStream();
    }

    @Override
    public int available() {
        return currentStream.available();
    }

    @Override
    public void close() {
        currentStream.close();
    }

    @Override
    public int read() {
        updateStreamIfNeeded();
        return currentStream.read();
    }

    @Override
    public int read(byte[] b) {
        updateStreamIfNeeded();
        return currentStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) {
        updateStreamIfNeeded();
        return currentStream.read(b, off, len);
    }

    @Override
    public long skip(long n) {
        updateStreamIfNeeded();
        return currentStream.skip(n);
    }

    @Override
    public FileChannel getChannel() {
        return currentStream.getChannel();
    }

    @Override
    public FileDescriptor getFD() {
        return currentStream.getFD();
    }

    /**
     * If we reach the end of a file, update the currentStream accordingly
     */
    private void updateStreamIfNeeded() {
        if (currentStream.available() == 0) {
            currentStream.close();
            fileIndex++;
            currentStream = getStream();
        }
    }

    /**
     * Get the current file as a FileInputStream
     * @return
     */
    private FileInputStream getStream() {
        File f = new File(this.files[this.fileIndex]);
        FileInputStream in = new FileInputStream(f);
        return in;
    }
}
