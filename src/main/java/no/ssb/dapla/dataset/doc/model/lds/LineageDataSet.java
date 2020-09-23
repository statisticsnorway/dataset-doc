package no.ssb.dapla.dataset.doc.model.lds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LineageDataSet extends LineageObject {

    @JsonProperty
    private List<String> lineage;

    @JsonProperty
    private String dataset;

    public List<String> getLineage() {
        return lineage;
    }

    public void setLineage(List<String> lineage) {
        this.lineage = lineage;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }
}
