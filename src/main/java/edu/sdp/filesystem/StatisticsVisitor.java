package edu.sdp.filesystem;

/** A new operation added without editing the element classes or visitor contract. */
public final class StatisticsVisitor implements Visitor {
    private long fileCount;
    private long directoryCount;
    private int maximumDepth;

    @Override
    public void reset() {
        fileCount = 0;
        directoryCount = 0;
        maximumDepth = 0;
    }

    @Override
    public void beforeVisit(String relativePath, int depth) {
        maximumDepth = Math.max(maximumDepth, depth);
    }

    @Override
    public void visit(FileNode file) {
        fileCount++;
    }

    @Override
    public void visit(DirectoryNode directory) {
        directoryCount++;
    }

    public long getFileCount() {
        return fileCount;
    }

    public long getDirectoryCount() {
        return directoryCount;
    }

    public int getMaximumDepth() {
        return maximumDepth;
    }
}
