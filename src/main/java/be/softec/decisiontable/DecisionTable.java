package be.softec.decisiontable;

import com.google.common.base.Verify;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("WeakerAccess")
public class DecisionTable<T> {

    private final T fallBack;
    private final ImmutableList<Tree> domains;
    private final ImmutableList<DtRow<T>> rows;

    DecisionTable(T fallBack, ImmutableList<Tree> domains, ImmutableList<DtRow<T>> rows) {
        this.fallBack = fallBack;
        this.domains = domains;
        this.rows = rows;
    }

    public static <T> DecisionTableBuilder<T> of(T fallBack, Tree... domains) {
        return new DecisionTableBuilder<>(fallBack, ImmutableList.copyOf(domains));
    }

    //TODO: validate with predicate
    @SuppressWarnings("unchecked")
    static void validateNodes(ImmutableList<Tree> domains, Object[] values) {
        checkNotNull(domains);
        checkNotNull(values);
        checkArgument(domains.size() == values.length);
        IntStream.range(0, domains.size())
                .peek(i -> checkNotNull(values[i]))
                .forEach(i -> checkArgument(domains.get(i).containsNode(values[i])));
    }

    //TODO: validate with predicate
    @SuppressWarnings("unchecked")
    private static void validateLeafs(ImmutableList<Tree> domains, Object[] leafs) {
        checkNotNull(domains);
        checkNotNull(leafs);
        checkArgument(domains.size() == leafs.length);
        IntStream.range(0, domains.size())
                .peek(i -> checkNotNull(leafs[i]))
                .forEach(i -> checkArgument(domains.get(i).containsLeaf(leafs[i])));
    }


    public T accept(Object... leafs) {
        validateLeafs(domains, leafs);
        return rows.stream()
                .map(row -> row.match(leafs))
                .filter(Objects::nonNull)
                .sorted()
                .findFirst()
                .map(MatchResult::getAction)
                .orElse(fallBack);
    }


    public static class DecisionTableBuilder<T> {

        private final T fallBack;
        private final ImmutableList<Tree> domains;
        private final ImmutableList.Builder<DtRow<T>> rows = ImmutableList.builder();

        public DecisionTableBuilder(T fallBack, ImmutableList<Tree> domains) {
            checkNotNull(domains);
            checkArgument(!domains.isEmpty());
            this.fallBack = fallBack;
            this.domains = domains;
        }

        public DecisionTableBuilder<T> addRow(T result, Object... values) {
            DecisionTable.validateNodes(domains, values);
            rows.add(new DtRow<>(result, domains, values));
            return this;
        }

        public DecisionTable<T> build() {
            return new DecisionTable<>(fallBack, domains, rows.build());
        }

    }
}

@SuppressWarnings("WeakerAccess")
class MatchResult<T> implements Comparable<MatchResult<T>> {
    private final T action;
    private final List<Integer> scores;

    @SuppressWarnings("unused")
    MatchResult(T action) {
        this(action, new ArrayList<>());
    }

    @SuppressWarnings("unused")
    MatchResult(T action, List<Integer> scores) {
        this.action = action;
        this.scores = scores;
    }

    T getAction() {
        return action;
    }

    @SuppressWarnings("unused")
    void add(Integer score) {
        scores.add(score);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public int compareTo(MatchResult<T> o) {
        //noinspection ResultOfMethodCallIgnored
        Verify.verifyNotNull(o);
        Verify.verify(scores.size() == o.scores.size());
        return IntStream.range(0, scores.size())
                .map(i -> scores.get(i).compareTo(o.scores.get(i)))
                .filter(j -> j != 0)
                .findFirst()
                .orElse(0);
    }
}

class DtRow<T> {

    private final T result;
    private final ImmutableList<Tree> domains;
    private final Object[] values;

    DtRow(T result, ImmutableList<Tree> domains, Object[] values) {
        this.result = result;
        this.domains = domains;
        this.values = values;
    }

    //TODO: rewrite the following method with java streams
    MatchResult<T> match(Object[] leafs) {
        MatchResult<T> matchResult = new MatchResult<>(this.result);

        // for each cell in this row
        for (int i = 0; i < leafs.length; i++) {
            //noinspection unchecked
            int score = domains.get(i).getDistance(leafs[i], values[i]);
            if (score < 0) { //no match
                return null;
            }
            matchResult.add(score);
        }
        return matchResult;
    }
}
