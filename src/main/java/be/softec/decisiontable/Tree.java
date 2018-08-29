package be.softec.decisiontable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.Traverser;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class Tree<T> {

    private final TreeNode<T> root;
    private final Map<T, TreeNode<T>> nodes;
    private final Map<T, TreeNode<T>> leafs;


    private Tree(TreeNode<T> root) {
        checkNotNull(root);
        this.root = root;

        ImmutableMap.Builder<T, TreeNode<T>> nodesBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<T, TreeNode<T>> leafsBuilder = ImmutableMap.builder();
        //noinspection UnstableApiUsage
        Traverser.<TreeNode<T>>forTree(node -> node.children)
                .breadthFirst(root)
                .forEach(node -> {
                    nodesBuilder.put(node.getData(), node);
                    if (node.isLeaf()) {
                        leafsBuilder.put(node.getData(), node);
                    }
                });
        this.nodes = nodesBuilder.build();
        this.leafs = leafsBuilder.build();
    }

    boolean containsNode(T node) {
        return nodes.containsKey(node);
    }

    boolean containsLeaf(T node) {
        return leafs.containsKey(node);
    }

    int getDistance(T leaf, T node) {
        checkNotNull(leaf);
        checkNotNull(node);
        Preconditions.checkArgument(containsNode(node));
        TreeNode<T> leafNode = leafs.get(leaf);
        Preconditions.checkArgument(leafNode != null);
        return leafNode.getDistanceTo(node);
    }

    @SuppressWarnings("unused")
    public TreeNode<T> getRoot() {
        return root;
    }

    private static class TreeNode<T> {

        private final T data;
        private ImmutableSet<TreeNode<T>> children;
        private final TreeNode<T> parent;

        TreeNode(TreeNode<T> parent, T data) {
            this.parent = parent;
            this.data = checkNotNull(data);
        }

        private TreeNode<T> withChildren(Set<TreeNode<T>> children) {
            this.children = children == null || children.isEmpty() ? ImmutableSet.of() : ImmutableSet.copyOf(children);
            return this;
        }

        boolean isLeaf() {
            return children.isEmpty();
        }


        private T getData() {
            return data;
        }

        private int getDistanceTo(Object node) {
            if (data.equals(node)) return 0;
            else if (parent == null) return -1;

            int distanceFromParent = parent.getDistanceTo(node);
            return distanceFromParent == -1 ? -1 : distanceFromParent + 1;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class TreeBuilder<T> {
        private final T data;
        private Set<TreeBuilder<T>> children = new HashSet<>();

        TreeBuilder(T data) {
            this.data = checkNotNull(data);
        }

        public static <T> TreeBuilder<T> node(T data) {
            return new TreeBuilder<>(data);
        }

        public TreeBuilder<T> child(TreeBuilder<T> node) {
            children.add(checkNotNull(node));
            return this;
        }

        public TreeBuilder<T> child(T data) {
            children.add(node(data));
            return this;
        }

        public Tree<T> build() {
            return new Tree<>(build(null));
        }

        private TreeNode<T> build(TreeNode<T> parent) {
            TreeNode<T> node = new TreeNode<>(parent, data);
            //noinspection UnstableApiUsage
            return node.withChildren(this.children
                    .stream()
                    .map(child -> child.build(node))
                    .collect(ImmutableSet.toImmutableSet())
            );
        }
    }
}


