package vstu.oop.view.component;

import javax.swing.*;

import static java.util.Objects.requireNonNull;

public abstract class Component<M> extends JComponent {

    private final M model;

    protected Component(M model) {
        requireNonNull(model);
        this.model = model;
    }

    protected M model() {
        return model;
    }
}
