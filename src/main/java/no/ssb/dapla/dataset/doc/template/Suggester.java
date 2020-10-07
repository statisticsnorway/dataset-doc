package no.ssb.dapla.dataset.doc.template;

import no.ssb.dapla.dataset.doc.model.simple.Instance;
import no.ssb.dapla.dataset.doc.model.simple.Record;

import java.util.Optional;

public interface Suggester {

    Optional<Instance> suggestInstanceVariable(String name);
    Optional<Record> suggestRecord(String name);

}
