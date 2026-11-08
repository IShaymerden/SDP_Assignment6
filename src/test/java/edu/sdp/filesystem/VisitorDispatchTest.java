package edu.sdp.filesystem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VisitorDispatchTest {
    @Test
    void fileSelectsFileOverloadThroughElementReference() {
        Element element = new FileNode("one.txt", 1);
        RecordingVisitor visitor = new RecordingVisitor();
        element.accept(visitor);
        assertSame(element, visitor.file);
        assertNull(visitor.directory);
    }

    @Test
    void directorySelectsDirectoryOverloadWithoutRecursing() {
        DirectoryNode directory = new DirectoryNode("folder");
        directory.addChild(new FileNode("child", 1));
        Element element = directory;
        RecordingVisitor visitor = new RecordingVisitor();
        element.accept(visitor);
        assertSame(element, visitor.directory);
        assertNull(visitor.file);
    }

    private static class RecordingVisitor implements Visitor {
        private FileNode file;
        private DirectoryNode directory;

        @Override
        public void visit(FileNode file) {
            this.file = file;
        }

        @Override
        public void visit(DirectoryNode directory) {
            this.directory = directory;
        }
    }
}
