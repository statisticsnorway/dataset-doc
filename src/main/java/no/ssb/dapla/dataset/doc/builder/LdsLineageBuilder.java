package no.ssb.dapla.dataset.doc.builder;

import no.ssb.dapla.dataset.doc.model.lds.LineageDataSet;

import java.util.List;
import java.util.stream.Collectors;

public class LdsLineageBuilder {
    private LdsLineageBuilder() {
    }

    public static LdsLineageBuilder.BaseBuilder create() {
        return new LdsLineageBuilder.BaseBuilder();
    }

    public static class BaseBuilder {
        private String id;

        public LdsLineageBuilder.BaseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public LineageDataSetBuilder lineageDataSet() {
            return new LineageDataSetBuilder(this);
        }
    }

    public static class LineageDataSetBuilder {
        private BaseBuilder baseBuilder;

        public LineageDataSetBuilder(BaseBuilder baseBuilder) {
            this.baseBuilder = baseBuilder;
        }

        private LineageDataSet lineageDataSet = new LineageDataSet();

        public LdsLineageBuilder.LineageDataSetBuilder dataset(String dataset) {
            lineageDataSet.setDataset("Dataset/" + dataset);
            return this;
        }

        public LdsLineageBuilder.LineageDataSetBuilder lineageDataSets(List<String> lineageDataSets) {
            List<String> lineage = lineageDataSets.stream().map(lr -> "/LineageDataSet/" + lr).collect(Collectors.toList());
            lineageDataSet.setLineage(lineage);
            return this;
        }
        public LineageDataSet build() {
            lineageDataSet.setId(baseBuilder.id);
            return lineageDataSet;
        }

    }
}

