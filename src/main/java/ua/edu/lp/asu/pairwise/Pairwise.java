package ua.edu.lp.asu.pairwise;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Pairwise implements Iterable<Case> {

    @Getter
    private final List<Parameter<?>> parameters;

    @Getter
    private final List<Case> cases;

    private Pairwise(List<Parameter<?>> parameters, List<Case> cases) {
        this.parameters = parameters;
        this.cases = cases;
    }

    @Override
    public Iterator<Case> iterator() {
        return cases.iterator();
    }

    public static class Builder {

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
            return new Pairwise(parameters, null);
        }

    }

}
