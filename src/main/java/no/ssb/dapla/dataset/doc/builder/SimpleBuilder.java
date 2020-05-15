package no.ssb.dapla.dataset.doc.builder;

import no.ssb.dapla.dataset.doc.model.simple.Dataset;
import no.ssb.dapla.dataset.doc.model.simple.InstanceVariable;
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

        public DatasetBuilder path(String path) {
            dataset.setPath(path);
            return this;
        }

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

        public LogicalRecordBuilder parent(String parentId) {
            logicalRecord.setParentId(parentId);
            return this;
        }

        public LogicalRecord build() {
            return logicalRecord;
        }

    }

    public static class InstanceVariableBuilder {
        private final InstanceVariable instanceVariable = new InstanceVariable();

        public InstanceVariableBuilder dataStructureComponentType(String dataStructureComponentType) {
            instanceVariable.setDataStructureComponentType(dataStructureComponentType);
            return this;
        }

        public InstanceVariableBuilder identifierComponentIsComposite(boolean identifierComponentIsComposite) {
            instanceVariable.setIdentifierComponentIsComposite(identifierComponentIsComposite);
            return this;
        }


        public InstanceVariableBuilder identifierComponentIsUnique(boolean identifierComponentIsUnique) {
            instanceVariable.setIdentifierComponentIsUnique(identifierComponentIsUnique);
            return this;
        }

        public InstanceVariableBuilder dataStructureComponentRole(String dataStructureComponentRole) {
            instanceVariable.setDataStructureComponentRole(dataStructureComponentRole);
            return this;
        }

        public InstanceVariableBuilder representedVariable(String representedVariable) {
            instanceVariable.setRepresentedVariable(representedVariable);
            return this;
        }

        public InstanceVariableBuilder name(String shortName) {
            instanceVariable.setName(shortName);
            return this;
        }

        public InstanceVariableBuilder population(String population) {
            instanceVariable.setPopulation(population);
            return this;
        }

        public InstanceVariableBuilder sentinelValueDomain(String sentinelValueDomain) {
            instanceVariable.setSentinelValueDomain(sentinelValueDomain);
            return this;
        }

        public InstanceVariableBuilder description(String description) {
            instanceVariable.setDescription(description);
            return this;
        }

        public InstanceVariable build() {
            return instanceVariable;
        }
    }
}
