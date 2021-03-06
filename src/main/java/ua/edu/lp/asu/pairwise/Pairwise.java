package ua.edu.lp.asu.pairwise;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pairwise implements Iterable<Case> {

    @Getter
    private final List<Parameter<?>> parameters;

    @Getter
    private final List<Case> cases;

    private Pairwise(List<Parameter<?>> parameters, List<Case> cases) {
        this.parameters = parameters;
        this.cases = cases;
    }

    public List<Case> verify() {
        return InParameterOrderStrategy
            .generatePairs(parameters)
            .stream()
            .flatMap(List::stream)
            .filter(pair -> !stream()
                .filter(pair::matches)
                .findFirst()
                .isPresent())
            .collect(Collectors.toList());
    }

    public Object[][] toTestNG() {
        return stream()
            .map(Map::values)
            .map(Collection::toArray)
            .collect(Collectors.toList())
            .toArray(new Object[0][0]);
    }

    public Collection<Object[]> toJUnit() {
        return stream()
            .map(Map::values)
            .map(Collection::toArray)
            .collect(Collectors.toList());
    }

    @Override
    public Iterator<Case> iterator() {
        return cases.iterator();
    }

    public Stream<Case> stream() {
        return cases.stream();
    }

    public static class Builder {

        private static Random random = new Random();

        @Getter
        @Setter
        private List<Parameter<?>> parameters;

        public Builder() {
            this.parameters = new ArrayList<>();
        }

        public Builder withParameter(Parameter<?> parameter) {
            this.parameters.add(parameter);
            return this;
        }

        public Builder withParameters(List<Parameter<?>> parameters) {
            this.parameters.addAll(parameters);
            return this;
        }

        public Pairwise build() {
            final Case prototype = parameters.stream()
                .collect(Case::new, (o, p) -> o.put(p.getName(), null), Case::putAll);
            final Map<String, Object[]> params = parameters.stream()
                .collect(Collectors.toMap(Parameter::getName, List::toArray));

            return new Pairwise(parameters,
                InParameterOrderStrategy
                    .generatePairs(parameters).stream()
                    .reduce(new ArrayList<>(), (cases, pairs) -> {
                        if (cases.isEmpty()) return pairs;
                        cases = InParameterOrderStrategy
                            .horizontalGrowth(cases, pairs);
                        cases.addAll(InParameterOrderStrategy
                            .verticalGrowth(pairs));
                        return cases;
                    }).stream()
                    .map(c -> prototype.clone().union(c))
                    .peek(c -> c.putAll(c.entrySet().stream()
                        .filter(e -> e.getValue() == null)
                        .peek(e -> e.setValue(random(params.get(e.getKey()))))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
                    .collect(Collectors.toList()));
        }

        private static Object random(Object[] array) {
            return array[random.nextInt(array.length)];
        }

    }

}
