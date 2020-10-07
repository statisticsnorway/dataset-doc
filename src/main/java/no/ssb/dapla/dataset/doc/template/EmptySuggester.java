package no.ssb.dapla.dataset.doc.template;

import no.ssb.dapla.dataset.doc.model.simple.Instance;
import no.ssb.dapla.dataset.doc.model.simple.Record;

import java.util.Optional;

public class EmptySuggester implements Suggester {

    @Override
    public Optional<Instance> suggestInstanceVariable(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Record> suggestRecord(String name) {
        return Optional.empty();
    }
}
