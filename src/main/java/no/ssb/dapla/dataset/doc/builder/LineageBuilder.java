package no.ssb.dapla.dataset.doc.builder;

import no.ssb.dapla.dataset.doc.model.lineage.Dataset;
import no.ssb.dapla.dataset.doc.model.lineage.InstanceVariable;
import no.ssb.dapla.dataset.doc.model.lineage.LogicalRecord;
import no.ssb.dapla.dataset.doc.model.lineage.Source;


public class LineageBuilder {

    private LineageBuilder() {
    }

    public static LogicalRecordBuilder createLogicalRecordBuilder() {
        return new LogicalRecordBuilder();
    }

    public static InstanceVariableBuilder createInstanceVariableBuilder() {
        return new InstanceVariableBuilder();
    }

    public static DatasetBuilder createDatasetBuilder() {
        return new DatasetBuilder();
    }

    public static class DatasetBuilder {
        private final Dataset dataset = new Dataset();

        public DatasetBuilder root(LogicalRecord path) {
            dataset.setRoot(path);
            return this;
        }

        public Dataset build() {
            return dataset;
        }
    }

    public static class LogicalRecordBuilder {
        private final LogicalRecord logicalRecord = new LogicalRecord();

        public LogicalRecordBuilder name(String shortName) {
            logicalRecord.setName(shortName);
            return this;
        }

        public LogicalRecord build() {
            return logicalRecord;
        }

    }

    public static class InstanceVariableBuilder {
        private final InstanceVariable instanceVariable = new InstanceVariable();

        public InstanceVariableBuilder inherit(String name) {
            instanceVariable.setName(name);
            return this;
        }

        public InstanceVariableBuilder addSource(Source source) {
            instanceVariable.addSource(source);
            return this;
        }

        public InstanceVariable build() {
            return instanceVariable;
        }
    }
}
