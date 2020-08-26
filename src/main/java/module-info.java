module no.ssb.dapla.dataset.doc {
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    requires avro;
    requires avro.buddy.core;
    requires gson;

    exports no.ssb.dapla.dataset.doc.model.simple;
    exports no.ssb.dapla.dataset.doc.template;
    exports no.ssb.dapla.dataset.doc.model.gsim;

    opens no.ssb.dapla.dataset.doc.model.simple to com.fasterxml.jackson.databind, gson;
    opens no.ssb.dapla.dataset.doc.model.lineage to com.fasterxml.jackson.databind, gson;
    opens no.ssb.dapla.dataset.doc.model.gsim to com.fasterxml.jackson.databind, gson;

    opens no.ssb.dapla.dataset.doc.traverse;
    opens no.ssb.dapla.dataset.doc.template;
}