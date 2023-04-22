package com.github.loyada.jdollarx;

import static com.github.loyada.jdollarx.BasicPath.input;
import static com.github.loyada.jdollarx.CustomElementProperties.createPropertyGenerator;
import static com.github.loyada.jdollarx.CustomElementProperties.hasProperty;
import static java.lang.String.format;

import java.util.function.Function;

public class HighLevelPaths {
    private static final Function<String, ElementProperty> inputType =
            createPropertyGenerator(value -> format("@type='%s'", value), value -> format("has type %s", value));

    public static ElementProperty hasType(String theType) {
        return hasProperty(inputType, theType);
    }

    public static Path checkbox = input.that(hasType("checkbox"));
}
