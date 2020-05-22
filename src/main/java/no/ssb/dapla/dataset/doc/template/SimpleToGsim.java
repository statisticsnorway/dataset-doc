package no.ssb.dapla.dataset.doc.template;

import no.ssb.dapla.dataset.doc.builder.GsimBuilder;
import no.ssb.dapla.dataset.doc.model.gsim.InstanceVariable;
import no.ssb.dapla.dataset.doc.model.gsim.LogicalRecord;
import no.ssb.dapla.dataset.doc.model.gsim.PersistenceProvider;

import java.util.Collections;

public class SimpleToGsim {
    private final no.ssb.dapla.dataset.doc.model.simple.Dataset root;
    private final PersistenceProvider persistenceProvider;

    public SimpleToGsim(no.ssb.dapla.dataset.doc.model.simple.Dataset root, PersistenceProvider persistenceProvider) {
        this.root = root;
        this.persistenceProvider = persistenceProvider;
    }

    public void createGsimObjects() {
        processAll(root.getRoot(), 0);
    }

    void processAll(no.ssb.dapla.dataset.doc.model.simple.LogicalRecord logicalRecord, int level) {
        for (var instanceVariable : logicalRecord.getInstanceVariables()) {
            InstanceVariable gsimInstanceVariable = GsimBuilder.create()
                    .id(createId(logicalRecord, instanceVariable))
                    .name(instanceVariable.getName())
                    .description(instanceVariable.getDescription())
                    .instanceVariable()
                    .shortName(instanceVariable.getName())
                    .dataStructureComponentType(instanceVariable.getDataStructureComponentType(), "MEASURE")
                    .dataStructureComponentRole(instanceVariable.getDataStructureComponentRole(), "ENTITY")
                    .sentinelValueDomain(instanceVariable.getSentinelValueDomain(), "DescribedValueDomain_DUMMY")
                    .representedVariable(instanceVariable.getRepresentedVariable(), "RepresentertVariable_DUMMY")
                    .build();

            System.out.println(getIntendString(level + 1) + gsimInstanceVariable.getShortName());
            persistenceProvider.save(gsimInstanceVariable);
        }

        for (var child : logicalRecord.getLogicalRecords()) {
            LogicalRecord gsimLogicalRecord = GsimBuilder.create()
                    .id("id")
                    .id(createId(child))
                    .name(child.getName())
                    .logicalRecord()
                    .unitType(child.getUnitType(), "UnitType_DUMMY")
                    .shortName(child.getName())
                    .instanceVariables(logicalRecord.getInstanceVariableIds(root.getPath()))
                    .build();

            System.out.println(getIntendString(level) + gsimLogicalRecord.getShortName() + " (lr)");
            persistenceProvider.save(gsimLogicalRecord);


            processAll(child, level + 1);
        }
    }

    private String createId(no.ssb.dapla.dataset.doc.model.simple.LogicalRecord logicalRecord) {
        return root.getPath() + "/" + logicalRecord.getPath();
    }

    private String createId(no.ssb.dapla.dataset.doc.model.simple.LogicalRecord logicalRecord,
                            no.ssb.dapla.dataset.doc.model.simple.InstanceVariable instanceVariable) {
        return root.getPath() + "/" + logicalRecord.getPath() + "/" + instanceVariable.getName();
    }

    String getIntendString(int level) {
        if (level == 0) return "";
        if (level == 1) return " |-- ";
        return String.join("", Collections.nCopies(level, " |   ")) + " |-- ";
    }
}
