package no.ssb.dapla.dataset.doc.model.lineage;


import no.ssb.dapla.dataset.doc.model.lds.LineageObject;

public interface LineagePersistenceProvider {
    void save(LineageObject lineageObject);
}
