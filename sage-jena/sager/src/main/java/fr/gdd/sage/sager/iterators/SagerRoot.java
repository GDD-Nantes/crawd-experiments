package fr.gdd.sage.sager.iterators;

import fr.gdd.sage.sager.SagerConstants;
import fr.gdd.sage.sager.pause.Pause2SPARQL;
import org.apache.jena.sparql.engine.ExecutionContext;

import java.util.Iterator;

/**
 * Iterator that is in charge of catching the Pause event thrown
 * by a downstream iterator.
 * @param <T> The type returned by the iterators.
 */
public class SagerRoot<T> implements Iterator<T> {

    final Pause2SPARQL saver;
    final Iterator<T> wrapped;
    final ExecutionContext context;
    final Long limit;

    long count = 0L;
    boolean doesHaveNext = false;
    boolean consumed = true;
    T buffered = null;

    public SagerRoot(ExecutionContext context, Iterator<T> wrapped) {
        this.wrapped = wrapped;
        this.saver = context.getContext().get(SagerConstants.SAVER);
        this.context = context;
        this.limit = context.getContext().get(SagerConstants.LIMIT);
    }

    @Override
    public boolean hasNext() {
        if (count >= limit) {
            return false;
        }

        if (!consumed) {
            return doesHaveNext;
        }

        try {
            doesHaveNext = wrapped.hasNext();
        } catch (PauseException e) {
            return false;
        }

        if (doesHaveNext) {
            try {
                buffered = wrapped.next();
                // TODO double check if with custom engine it's still the case. might not be as complex now.
                // may save during the `.next()` which would set `.hasNext()` as false while
                // it expects and checks `true`. When it happens, it throws a `NoSuchElementException`
            } catch (PauseException e) {
                return false;
            }
            consumed = false;
            return true;
        }
        return false;
    }

    @Override
    public T next() {
        count += 1;
        consumed = true;
        return buffered;
    }
}