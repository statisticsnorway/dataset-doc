package no.ssb.dapla.dataset.doc.template;

import no.ssb.dapla.dataset.doc.builder.GsimBuilder;
import no.ssb.dapla.dataset.doc.model.gsim.InstanceVariable;
import no.ssb.dapla.dataset.doc.model.gsim.LogicalRecord;
import no.ssb.dapla.dataset.doc.model.gsim.PersistenceProvider;
import no.ssb.dapla.dataset.doc.model.gsim.UnitDataSet;
import no.ssb.dapla.dataset.doc.model.gsim.UnitDataStructure;

import java.util.Collections;

public class SimpleToGsim {
    private final no.ssb.dapla.dataset.doc.model.simple.Dataset root;
    private final PersistenceProvider persistenceProvider;

    private GsimBuilder.BaseBuilder createDefault(String id, String name, String description) {
        // for now just add hardcoded default values
        String date = "2020-01-01T00:00:00Z";
        return GsimBuilder.create()
                .id(id)
                .languageCode("nb")
                .name(name)
                .description(description)
                .createdBy("rl")
                .addProperty("administrativeStatus", "DRAFT")
                .addProperty("createdDate", date)
                .addProperty("validFrom", date)
                .addProperty("version", "1.0.0")
                .addProperty("versionValidFrom", date);
    }

    public SimpleToGsim(no.ssb.dapla.dataset.doc.model.simple.Dataset root, PersistenceProvider persistenceProvider) {
        this.root = root;
        this.persistenceProvider = persistenceProvider;
    }

    public void createGsimObjects() {
        var rootLogicalRecord = this.root.getRoot();
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
                .dataSourcePath(root.getPath())
                .build();
        persistenceProvider.save(unitDataset);

        processAll(rootLogicalRecord, 0);
    }

    void processAll(no.ssb.dapla.dataset.doc.model.simple.LogicalRecord logicalRecord, int level) {
        LogicalRecord gsimLogicalRecord =
                createDefault(createId(logicalRecord), logicalRecord.getName(), null)
                        .logicalRecord()
                        .isPlaceholderRecord(false)// TODO: add and get from simple
                        .unitType(logicalRecord.getUnitType(), "UnitType_DUMMY")
                        .shortName(logicalRecord.getName())
                        .instanceVariables(logicalRecord.getInstanceVariableIds(i -> createId(logicalRecord, i)))
                        .build();

//        System.out.println(getIntendString(level) + gsimLogicalRecord.getShortName() + " (lr)");
        persistenceProvider.save(gsimLogicalRecord);

        for (var instanceVariable : logicalRecord.getInstanceVariables()) {
            InstanceVariable gsimInstanceVariable =
                    createDefault(createId(logicalRecord, instanceVariable), instanceVariable.getName(), instanceVariable.getDescription())
                            .instanceVariable()
                            .shortName(instanceVariable.getName())
                            .population("Population_DUMMY")
                            .dataStructureComponentType(instanceVariable.getDataStructureComponentType(), "MEASURE")
                            .dataStructureComponentRole(instanceVariable.getDataStructureComponentRole(), "ENTITY")
                            .sentinelValueDomain(instanceVariable.getSentinelValueDomain(), "DescribedValueDomain_DUMMY")
                            .representedVariable(instanceVariable.getRepresentedVariable(), "RepresentertVariable_DUMMY")
                            .build();

//            System.out.println(getIntendString(level + 1) + gsimInstanceVariable.getShortName());
            persistenceProvider.save(gsimInstanceVariable);
        }

        for (var child : logicalRecord.getLogicalRecords()) {
            processAll(child, level + 1);
        }
    }

    private String createId(no.ssb.dapla.dataset.doc.model.simple.LogicalRecord logicalRecord) {
        String path = root.getPath().substring(1); // Remove first slash
        return path.replace("/", ".") + "." + logicalRecord.getPath();
    }

    private String createId(no.ssb.dapla.dataset.doc.model.simple.LogicalRecord logicalRecord,
                            no.ssb.dapla.dataset.doc.model.simple.InstanceVariable instanceVariable) {
        return createId(logicalRecord) + "." + instanceVariable.getName();
    }

    String getIntendString(int level) {
        if (level == 0) return "";
        if (level == 1) return " |-- ";
        return String.join("", Collections.nCopies(level, " |   ")) + " |-- ";
    }
}
