# Assignment 6 - Visitor Pattern

**Iskander Shaimerdinov · SE-2524**  
Repository: https://github.com/IShaymerden/SDP_Assignment6

An in-memory file system with independent size, filename search and statistics operations.
Java 17 language/API target, Maven and JUnit 5. No runtime application dependencies.

## Run

From the project root, with JDK 17+ and Maven on PATH:

```shell
mvn test
mvn compile
java -cp target/classes edu.sdp.filesystem.Main
```

Change the filename suffix and inclusive size threshold without editing code:

```shell
java -cp target/classes edu.sdp.filesystem.Main .java 500
```

The default suffix is `.txt` and the default threshold is `600` bytes. Matching is case-sensitive.
Program arguments are optional; a supplied threshold must be a non-negative integer in the `long` range.
Maven downloads build/test dependencies on the first run if they are not cached.

## IntelliJ IDEA

1. Open this project's `pom.xml` as a project, or right-click it and choose **Add as Maven Project**.
2. Use an installed JDK 17 or newer as the Project SDK. Maven compiles with `--release 17`.
3. Run `edu.sdp.filesystem.Main.main()` using the green arrow beside the method.
4. Run Maven's **Lifecycle > test** to execute all tests. No console input is required.
5. To change the demo, edit `DemoData.createTree()` or add `.java 500` in the run configuration's Program arguments.

## Demo data

```text
coursework/                         depth 0
|-- README.txt              600
|-- documents/
|   |-- notes.txt          1200
|   |-- report.pdf         4096
|   `-- archive/
|       `-- draft.txt       300      depth 3
|-- src/
|   |-- Main.java           850
|   `-- Visitor.java        450
|-- empty/
`-- todo.txt                 0
```

```text
Visitor Pattern - Advanced File System
Root: coursework
Total size: 7496 bytes
Search: suffix=.txt, minimum size=600 bytes
  README.txt
  documents/notes.txt
Files: 7
Directories (including root): 5
Maximum depth (root = 0): 3
Repeated size calculation: 7496 bytes
```

## Design and lifecycle

- `Element` defines `accept(Visitor)`, `getName()` and read-only `getChildren()`.
  `AbstractNode` shares immutable naming and controlled parent ownership. A leaf returns an empty list.
- Both concrete elements implement `accept` as `visitor.visit(this)`.
  The actual element chooses its `accept` implementation; the statically known `this` selects
  the overload, and virtual dispatch chooses the concrete visitor implementation. There are no type switches.
- **TreeWalker is the sole traversal owner.** It uses an explicit stack for depth-first traversal;
  elements and visitors do not recurse. Each reachable node is dispatched once per operation.
- Call `walker.walk(root, visitor)` for every complete operation. It invokes `reset()` before
  dispatch, so each visitor instance can be reused sequentially with the same or a different tree.
  Direct `accept` visits only one node and does not start/reset a whole operation.
- Paths and depth are traversal context, supplied by `beforeVisit(path, depth)`. The selected root
  has depth 0 and path `.`. A file chosen as the root therefore matches as `.`.
  Descendant paths exclude the selected root's name, join names with `/`, and ignore ancestors
  outside that subtree. `SearchVisitor.getMatches()` returns a lexicographically sorted immutable snapshot.
- The minimum-size comparison is inclusive. Search accepts any `Predicate<String>` over a filename,
  e.g. suffix, exact name, substring, or regular-expression matching. It combines that predicate with size using AND.
- Statistics count every file and directory, including an empty directory root. Maximum depth includes
  empty directories as well as files. An empty directory has `(files=0, directories=1, depth=0)`.
- File sizes are non-negative `long` values. Total overflow throws `ArithmeticException` instead of wrapping.
- Mutations are sequential: do not change the tree during a walk, share a visitor across concurrent walks,
  or re-enter a walk from a filename predicate. Predicates should be pure. After an aborted walk, ignore
  partial results; the next walk resets them. Concurrency is outside the assignment's scope.

## Tree integrity

`DirectoryNode.addChild(AbstractNode)` validates everything before changing either collection or parent.
Duplicate names, existing ownership and cycles throw `IllegalArgumentException`.
The cycle check walks the destination's ancestors by identity, so it also prevents adding the root
under a descendant. Removing a child releases its parent and permits safe reparenting of its subtree.
Names cannot be blank, `.` or `..`, or contain `/` or `\`; names and file sizes are immutable.
Null required arguments throw `NullPointerException`. Child lists are immutable snapshots.
The sealed hierarchy prevents unsupported custom nodes from bypassing the model's public invariants.

## Extension boundary

The first implementation commit defines both overloads, lifecycle/context hooks, elements, walker,
size and search. The next commit adds `StatisticsVisitor` and demo calls only.
`FileNode`, `DirectoryNode`, `Element`, `Visitor` and `TreeWalker` remain unchanged in that extension.
You can inspect the second commit with `git show`.

A new **operation** requires another `Visitor` implementation and client invocation.
A new **element type** is more expensive: extend the sealed hierarchy, add a visitor overload,
implement that overload in every visitor, and define children/ownership semantics and tests.
The pattern is appropriate when element types are stable but operations change frequently.

## Verification

33 tests passed, 0 failures, 0 errors, 0 skipped. See `docs/test-results.txt`.
Verified with Maven 3.9.12 and the installed JDK 25.0.1 compiling with `--release 17`
(class-file major version 61). A separate Java 17 runtime execution was not performed.
The verification used Maven offline with a workspace copy of the local dependency cache;
normal usage is the `mvn test` command above.

| Test class | Tests | Assignment coverage |
|---|---:|---|
| VisitorDispatchTest | 2 | T1: both overloads; accept does not recurse |
| TotalSizeVisitorTest | 4 | T2/T6: nested sums, empty root, large sizes, overflow |
| SearchVisitorTest | 8 | T3/T4/T6: AND filters, boundary, sorted relative paths, no matches |
| StatisticsVisitorTest | 5 | T5/T6: counts, root semantics, subtree depth, empty directories |
| TreeIntegrityTest | 9 | T8: invalid mutations, snapshots, valid nesting and reparenting |
| TraversalLifecycleTest | 5 | T7/R7: reuse, changed data, exactly-once dispatch, depth 2001 |

## Clean Code evidence

1. **Meaningful names:** `minimumSize`, `filenameCriterion`, `getMaximumDepth()` state their purpose and units/context.
2. **Single Responsibility:** `DirectoryNode` protects tree structure; `TreeWalker` traverses; each visitor implements one operation.
3. **Small methods:** `addChild()` delegates precondition checks to `validateAttachment()` and then performs the mutation.
4. **Encapsulation:** private collections, immutable names/sizes and `getChildren()` snapshots preserve ownership invariants.
5. **No type-checking chains:** the two concrete `accept()` methods select overloaded visitor methods by double dispatch.

## Report and study notes

- `docs/SDP_Assignment6_Report_Iskander_Shaimerdinov.pdf`: 3 report pages plus a UML appendix.
- `docs/UML.puml`: editable PlantUML source.
- `docs/DEFENSE_RU.txt`: Russian walkthrough and questions for preparing the defense.
- `docs/demo-output.txt`: actual default application output.

Sources: the supplied Assignment 06 specification and Almas Ospanov's Lecture 9
(Visitor architecture, double dispatch, traversal and Clean Code).
Implementation and documentation were prepared with OpenAI Codex assistance; the repository records this assistance.
Repository publication and Moodle submission are separate actions.
