package no.ssb.dapla.dataset.doc.builder;

import no.ssb.dapla.dataset.doc.model.simple.Dataset;
import no.ssb.dapla.dataset.doc.model.simple.Info;
import no.ssb.dapla.dataset.doc.model.simple.Instance;
import no.ssb.dapla.dataset.doc.model.simple.Record;
import no.ssb.dapla.dataset.doc.template.ConceptNameLookup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SimpleBuilder {

    private SimpleBuilder() {
    }

    public static LogicalRecordBuilder createLogicalRecordBuilder(ConceptNameLookup conceptNameLookup) {
        return new LogicalRecordBuilder(conceptNameLookup);
    }

    public static InstanceVariableBuilder createInstanceVariableBuilder(ConceptNameLookup conceptNameLookup) {
        return new InstanceVariableBuilder(conceptNameLookup);
    }

    public static DatasetBuilder createDatasetBuilder() {
        return new DatasetBuilder();
    }

    public static class DatasetBuilder {
        private final Dataset dataset = new Dataset();

        public DatasetBuilder root(Record path) {
            dataset.setRoot(path);
            return this;
        }

        public Dataset build() {
            return dataset;
        }
    }

    public static class LogicalRecordBuilder {
        static final String LDS_SCHEMA_NAME = "LogicalRecord";

        private final Record record = new Record();
        private final ConceptNameLookup conceptNameLookup;

        public LogicalRecordBuilder(ConceptNameLookup conceptNameLookup) {
            this.conceptNameLookup = conceptNameLookup;
        }

        public LogicalRecordBuilder name(String shortName) {
            record.setName(shortName);
            return this;
        }

        public LogicalRecordBuilder unitType(String unitTypeId) {
            Map<String, String> nameToIds = conceptNameLookup.getNameToIds("UnitType");
            Info info = new Info(unitTypeId, "UnitType", nameToIds);
            record.setUnitType(info);
            return this;
        }

        public Record build() {
            return record;
        }

    }

    public static class InstanceVariableBuilder {
        static final String LDS_SCHEMA_NAME = "InstanceVariable";

        private final Instance instance = new Instance();
        private final ConceptNameLookup conceptNameLookup;

        public InstanceVariableBuilder(ConceptNameLookup conceptNameLookup) {
            this.conceptNameLookup = conceptNameLookup;
        }

        public InstanceVariableBuilder dataStructureComponentType(String dataStructureComponentType) {
            List<String> enumList = conceptNameLookup.getGsimSchemaEnum(LDS_SCHEMA_NAME, "dataStructureComponentType");
            Info info = new Info(dataStructureComponentType, "string", enumList);
            instance.setDataStructureComponentType(info);
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
            List<String> enumList = conceptNameLookup.getGsimSchemaEnum(LDS_SCHEMA_NAME, "dataStructureComponentRole");
            Info info = new Info(dataStructureComponentRole, "string", enumList);
            instance.setDataStructureComponentRole(info);
            return this;
        }

        public InstanceVariableBuilder representedVariable(String representedVariable) {
            Map<String, String> nameToIds = conceptNameLookup.getNameToIds("RepresentedVariable");
            Info info = new Info(representedVariable, "RepresentedVariable", nameToIds);
            instance.setRepresentedVariable(info);
            return this;
        }

        public InstanceVariableBuilder name(String shortName) {
            instance.setName(shortName);
            return this;
        }

        public InstanceVariableBuilder population(String population) {
            Map<String, String> nameToIds = conceptNameLookup.getNameToIds("Population");
            Info info = new Info(population, "Population", nameToIds);
            instance.setPopulation(info);
            return this;
        }

        public InstanceVariableBuilder sentinelValueDomain(String sentinelValueDomain) {
            HashMap<String, String> result = new HashMap<>();
            result.putAll(conceptNameLookup.getNameToIds("EnumeratedValueDomain"));
            result.putAll(conceptNameLookup.getNameToIds("DescribedValueDomain"));
            Info info = new Info(sentinelValueDomain, "EnumeratedValueDomain,DescribedValueDomain", result);
            instance.setSentinelValueDomain(info);
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
