package io.github.guilherme0s.iam;

import org.slf4j.bridge.SLF4JBridgeHandler;

import io.micronaut.runtime.Micronaut;

public class Application {

    static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        Micronaut.run(Application.class, args);
    }
}
