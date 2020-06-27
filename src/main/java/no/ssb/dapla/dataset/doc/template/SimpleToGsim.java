package no.ssb.dapla.dataset.doc.template;

import no.ssb.dapla.dataset.doc.builder.GsimBuilder;
import no.ssb.dapla.dataset.doc.model.gsim.InstanceVariable;
import no.ssb.dapla.dataset.doc.model.gsim.LogicalRecord;
import no.ssb.dapla.dataset.doc.model.gsim.PersistenceProvider;
import no.ssb.dapla.dataset.doc.model.gsim.UnitDataSet;
import no.ssb.dapla.dataset.doc.model.gsim.UnitDataStructure;
import no.ssb.dapla.dataset.doc.model.simple.Instance;

public class SimpleToGsim {
    private final no.ssb.dapla.dataset.doc.model.simple.Dataset root;
    private final String dataSetPath;
    private final PersistenceProvider persistenceProvider;
    private String userName = "Unknown";

    public GsimBuilder.BaseBuilder createDefault(String id, String name, String description) {
        // for now just add hardcoded default values
        String date = "2020-01-01T00:00:00Z";
        return GsimBuilder.create()
                .id(id)
                .languageCode("nb")
                .name(name)
                .description(description)
                .createdBy(userName)
                .addProperty("administrativeStatus", "DRAFT")
                .addProperty("createdDate", date)
                .addProperty("validFrom", date)
                .addProperty("version", "1.0.0")
                .addProperty("versionValidFrom", date);
    }

    public SimpleToGsim(no.ssb.dapla.dataset.doc.model.simple.Dataset root, String dataSetPath, PersistenceProvider persistenceProvider) {
        if (!dataSetPath.startsWith("/")) {
            throw new IllegalArgumentException("dataset path is expected to start with: '/' but was: " + dataSetPath);
        }
        this.root = root;
        this.dataSetPath = dataSetPath;
        this.persistenceProvider = persistenceProvider;
    }

    public SimpleToGsim createdBy(String userName) {
        this.userName = userName;
        return this;
    }

    public void createGsimObjects() {
        no.ssb.dapla.dataset.doc.model.simple.LogicalRecord rootLogicalRecord = this.root.getRoot();
        UnitDataStructure unitDataStructure = createDefault(createId(rootLogicalRecord), rootLogicalRecord.getName(), null)
                .unitDataStructure()
                .logicalRecord(createId(rootLogicalRecord))
                .build();
        persistenceProvider.save(unitDataStructure);

        UnitDataSet unitDataset = createDefault(createId(rootLogicalRecord), rootLogicalRecord.getName(), null)
                .unitDataSet()
                .unitDataStructure(unitDataStructure.getId())
                .temporalityType("EVENT") // TODO: get this from correct place
                .dataSetState("INPUT_DATA") // TODO: get this from correct place
                .dataSourcePath(dataSetPath)
                .build();
        persistenceProvider.save(unitDataset);

        processAll(rootLogicalRecord, null);
    }

    void processAll(no.ssb.dapla.dataset.doc.model.simple.LogicalRecord logicalRecord, String parentLogicalRecordId) {
        String logicalRecordId = parentLogicalRecordId == null ? createId(logicalRecord) : parentLogicalRecordId + "." + logicalRecord.getName();
        LogicalRecord gsimLogicalRecord =
                createDefault(logicalRecordId, logicalRecord.getName(), null)
                        .logicalRecord()
                        .isPlaceholderRecord(false)// TODO: add and get from simple
                        .unitType(logicalRecord.getUnitType(), "UnitType_DUMMY")
                        .shortName(logicalRecord.getName())
                        .instanceVariables(logicalRecord.getInstanceVariableIds(i -> createId(logicalRecord, i)))
                        .parent(parentLogicalRecordId)
                        .parentChildMultiplicity("ONE_MANY")
                        .build();

        persistenceProvider.save(gsimLogicalRecord);

        for (Instance instance : logicalRecord.getInstances()) {
            InstanceVariable gsimInstanceVariable =
                    createDefault(createId(logicalRecord, instance), instance.getName(), instance.getDescription())
                            .instanceVariable()
                            .shortName(instance.getName())
                            .population("Population_DUMMY")
                            .dataStructureComponentType(instance.getDataStructureComponentType(), "MEASURE")
                            .dataStructureComponentRole(instance.getDataStructureComponentRole(), "ENTITY")
                            .sentinelValueDomain(instance.getSentinelValueDomain(), "DescribedValueDomain_DUMMY")
                            .representedVariable(instance.getRepresentedVariable(), "RepresentedVariable_DUMMY")
                            .build();

            persistenceProvider.save(gsimInstanceVariable);
        }

        for (no.ssb.dapla.dataset.doc.model.simple.LogicalRecord child : logicalRecord.getLogicalRecords()) {
            processAll(child, logicalRecordId);
        }
    }

    private String createId(no.ssb.dapla.dataset.doc.model.simple.LogicalRecord logicalRecord) {
        String path = this.dataSetPath.substring(1); // Remove first slash
        return path.replace("/", ".") + "." + logicalRecord.getPath();
    }

    private String createId(no.ssb.dapla.dataset.doc.model.simple.LogicalRecord logicalRecord,
                            Instance instance) {
        return createId(logicalRecord) + "." + instance.getName();
    }
}
