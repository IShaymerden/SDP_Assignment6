package edu.sdp.filesystem;

import org.junit.jupiter.api.Test;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TraversalLifecycleTest {
    private final TreeWalker walker = new TreeWalker();

    @Test
    void repeatedRunsAndDifferentOperationOrderDoNotAccumulateState() {
        DirectoryNode root = TestTree.create();
        TotalSizeVisitor size = new TotalSizeVisitor();
        SearchVisitor search = new SearchVisitor(name -> name.endsWith(".txt"), 256);
        StatisticsVisitor statistics = new StatisticsVisitor();
        for (int i = 0; i < 3; i++) {
            walker.walk(root, search);
            walker.walk(root, statistics);
            walker.walk(root, size);
            assertEquals(1920, size.getTotalSize());
            assertEquals(List.of("alpha/beta.txt", "alpha/deep/report.txt"), search.getMatches());
            assertEquals(4, statistics.getFileCount());
            assertEquals(4, statistics.getDirectoryCount());
            assertEquals(3, statistics.getMaximumDepth());
        }
        DirectoryNode empty = new DirectoryNode("empty");
        walker.walk(empty, size);
        walker.walk(empty, search);
        walker.walk(empty, statistics);
        assertEquals(0, size.getTotalSize());
        assertTrue(search.getMatches().isEmpty());
        assertEquals(0, statistics.getFileCount());
        assertEquals(1, statistics.getDirectoryCount());
        assertEquals(0, statistics.getMaximumDepth());
    }

    @Test
    void changesBetweenRunsAreReflectedByAllVisitors() {
        DirectoryNode root = new DirectoryNode("root");
        TotalSizeVisitor size = new TotalSizeVisitor();
        SearchVisitor search = new SearchVisitor(name -> true, 0);
        StatisticsVisitor statistics = new StatisticsVisitor();
        walker.walk(root, size);
        walker.walk(root, search);
        walker.walk(root, statistics);
        root.addChild(new FileNode("new.txt", 42));
        walker.walk(root, size);
        walker.walk(root, search);
        walker.walk(root, statistics);
        assertEquals(42, size.getTotalSize());
        assertEquals(List.of("new.txt"), search.getMatches());
        assertEquals(1, statistics.getFileCount());
        assertEquals(1, statistics.getMaximumDepth());
    }

    @Test
    void visitsEachReachableObjectExactlyOnceAndResetsBeforeDispatch() {
        Map<Element, Integer> counts = new IdentityHashMap<>();
        Visitor visitor = new Visitor() {
            @Override
            public void reset() {
                counts.clear();
            }

            @Override
            public void visit(FileNode file) {
                counts.merge(file, 1, Integer::sum);
            }

            @Override
            public void visit(DirectoryNode directory) {
                counts.merge(directory, 1, Integer::sum);
            }
        };
        DirectoryNode root = TestTree.create();
        walker.walk(root, visitor);
        walker.walk(root, visitor);
        assertEquals(8, counts.size());
        assertTrue(counts.values().stream().allMatch(count -> count == 1));
    }

    @Test
    void iterativeTraversalHandlesDeepNesting() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode current = root;
        for (int i = 0; i < 2000; i++) {
            DirectoryNode next = new DirectoryNode("d" + i);
            current.addChild(next);
            current = next;
        }
        current.addChild(new FileNode("leaf", 13));
        StatisticsVisitor statistics = new StatisticsVisitor();
        walker.walk(root, statistics);
        assertEquals(1, statistics.getFileCount());
        assertEquals(2001, statistics.getDirectoryCount());
        assertEquals(2001, statistics.getMaximumDepth());
    }

    @Test
    void rejectsNullTraversalArguments() {
        assertThrows(NullPointerException.class, () -> walker.walk(null, new TotalSizeVisitor()));
        assertThrows(NullPointerException.class, () -> walker.walk(new DirectoryNode("root"), null));
    }
}
