package edu.sdp.filesystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class SearchVisitor implements Visitor {
    private final Predicate<String> filenameCriterion;
    private final long minimumSize;
    private final List<String> matches = new ArrayList<>();
    private String currentPath = ".";

    public SearchVisitor(Predicate<String> filenameCriterion, long minimumSize) {
        this.filenameCriterion = Objects.requireNonNull(filenameCriterion, "Criterion must not be null");
        if (minimumSize < 0) {
            throw new IllegalArgumentException("Minimum size must not be negative");
        }
        this.minimumSize = minimumSize;
    }

    @Override
    public void reset() {
        matches.clear();
        currentPath = ".";
    }

    @Override
    public void beforeVisit(String relativePath, int depth) {
        currentPath = relativePath;
    }

    @Override
    public void visit(FileNode file) {
        if (filenameCriterion.test(file.getName()) && file.getSize() >= minimumSize) {
            matches.add(currentPath);
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        // Only filenames are search candidates.
    }

    public List<String> getMatches() {
        return matches.stream().sorted().toList();
    }
}
