package edu.sdp.filesystem;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        String suffix = args.length > 0 ? args[0] : ".txt";
        long minimumSize = args.length > 1 ? Long.parseLong(args[1]) : 600;
        DirectoryNode root = DemoData.createTree();
        TreeWalker walker = new TreeWalker();
        TotalSizeVisitor size = new TotalSizeVisitor();
        SearchVisitor search = new SearchVisitor(name -> name.endsWith(suffix), minimumSize);
        StatisticsVisitor statistics = new StatisticsVisitor();

        walker.walk(root, size);
        walker.walk(root, search);
        walker.walk(root, statistics);
        System.out.println("Visitor Pattern - Advanced File System");
        System.out.println("Root: " + root.getName());
        System.out.println("Total size: " + size.getTotalSize() + " bytes");
        System.out.println("Search: suffix=" + suffix + ", minimum size=" + minimumSize + " bytes");
        search.getMatches().forEach(path -> System.out.println("  " + path));
        System.out.println("Files: " + statistics.getFileCount());
        System.out.println("Directories (including root): " + statistics.getDirectoryCount());
        System.out.println("Maximum depth (root = 0): " + statistics.getMaximumDepth());

        walker.walk(root, size);
        System.out.println("Repeated size calculation: " + size.getTotalSize() + " bytes");
    }
}
