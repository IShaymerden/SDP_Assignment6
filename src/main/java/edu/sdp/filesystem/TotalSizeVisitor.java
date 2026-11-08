package edu.sdp.filesystem;

public final class TotalSizeVisitor implements Visitor {
    private long totalSize;

    @Override
    public void reset() {
        totalSize = 0;
    }

    @Override
    public void visit(FileNode file) {
        totalSize = Math.addExact(totalSize, file.getSize());
    }

    @Override
    public void visit(DirectoryNode directory) {
        // A directory has no byte size of its own.
    }

    public long getTotalSize() {
        return totalSize;
    }
}
