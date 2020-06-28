module no.ssb.dapla.dataset.uri {
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.google.gson;

    requires avro;
    requires avro.buddy.core;

    exports no.ssb.dapla.dataset.doc.model.simple;
    exports no.ssb.dapla.dataset.doc.model.gsim;

    opens no.ssb.dapla.dataset.doc.model.simple to com.fasterxml.jackson.databind, com.google.gson;
    opens no.ssb.dapla.dataset.doc.model.lineage to com.fasterxml.jackson.databind, com.google.gson;
    opens no.ssb.dapla.dataset.doc.model.gsim to com.fasterxml.jackson.databind, com.google.gson;
}