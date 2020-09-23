package no.ssb.dapla.dataset.doc.model.lds;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LineageObject {

    @JsonProperty
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
