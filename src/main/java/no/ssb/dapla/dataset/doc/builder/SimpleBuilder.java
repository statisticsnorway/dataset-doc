package no.ssb.dapla.dataset.doc.builder;

import no.ssb.dapla.dataset.doc.model.simple.Dataset;
import no.ssb.dapla.dataset.doc.model.simple.Instance;
import no.ssb.dapla.dataset.doc.model.simple.LogicalRecord;


public class SimpleBuilder {

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

        public LogicalRecordBuilder unitType(String unitTypeId) {
            logicalRecord.setUnitType(unitTypeId);
            return this;
        }

        public LogicalRecord build() {
            return logicalRecord;
        }

    }

    public static class InstanceVariableBuilder {
        private final Instance instance = new Instance();

        public InstanceVariableBuilder dataStructureComponentType(String dataStructureComponentType) {
            instance.setDataStructureComponentType(dataStructureComponentType);
            return this;
        }

        public InstanceVariableBuilder identifierComponentIsComposite(boolean identifierComponentIsComposite) {
            instance.setIdentifierComponentIsComposite(identifierComponentIsComposite);
            return this;
        }


        public InstanceVariableBuilder identifierComponentIsUnique(boolean identifierComponentIsUnique) {
            instance.setIdentifierComponentIsUnique(identifierComponentIsUnique);
            return this;
        }

        public InstanceVariableBuilder dataStructureComponentRole(String dataStructureComponentRole) {
            instance.setDataStructureComponentRole(dataStructureComponentRole);
            return this;
        }

        public InstanceVariableBuilder representedVariable(String representedVariable) {
            instance.setRepresentedVariable(representedVariable);
            return this;
        }

        public InstanceVariableBuilder name(String shortName) {
            instance.setName(shortName);
            return this;
        }

        public InstanceVariableBuilder population(String population) {
            instance.setPopulation(population);
            return this;
        }

        public InstanceVariableBuilder sentinelValueDomain(String sentinelValueDomain) {
            instance.setSentinelValueDomain(sentinelValueDomain);
            return this;
        }

        public InstanceVariableBuilder description(String description) {
            instance.setDescription(description);
            return this;
        }

        public Instance build() {
            return instance;
        }
    }
}
