package no.ssb.dapla.dataset.doc.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import no.ssb.avro.convert.core.SchemaBuddy;
import no.ssb.dapla.dataset.doc.builder.SimpleBuilder;
import no.ssb.dapla.dataset.doc.model.simple.Dataset;
import no.ssb.dapla.dataset.doc.model.simple.InstanceVariable;
import no.ssb.dapla.dataset.doc.model.simple.LogicalRecord;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class SchemaToTemplate {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Schema schema;
    private final HashMap<String, LogicalRecord> pathToLogicalRecord = new HashMap<>();
    private String[] instanceVariableFilter = new String[]{};
    private String[] logicalRecordFilter = new String[]{};

    @Deprecated
    public SchemaToTemplate(Schema schema, String path) {
        this.schema = schema;
        if (path != null) {
            log.warn("Path {} will not be used", path);
        }
    }

    public SchemaToTemplate(Schema schema) {
        this.schema = schema;
    }

    public SchemaToTemplate withInstanceVariableFilter(String... ignoreFields) {
        instanceVariableFilter = ignoreFields;
        return this;
    }

    public SchemaToTemplate addInstanceVariableFilter(String... ignoreField) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(instanceVariableFilter));
        list.addAll(Arrays.asList(ignoreField));
        instanceVariableFilter = list.toArray(new String[0]);
        return this;
    }

    public SchemaToTemplate withLogicalRecordFilterFilter(String... ignoreFields) {
        logicalRecordFilter = ignoreFields;
        return this;
    }

    public SchemaToTemplate withDoSimpleFiltering(boolean simpleFiltering) {
        if (simpleFiltering) {
            return withLogicalRecordFilterFilter("unitType")
                    .withInstanceVariableFilter(
                            "dataStructureComponentRole",
                            "dataStructureComponentType",
                            "identifierComponentIsComposite",
                            "identifierComponentIsUnique",
                            "representedVariable",
                            "sentinelValueDomain",
                            "population");
        }
        return this;
    }

    public Dataset generateSimpleTemplate() {
        AtomicReference<LogicalRecord> logicalRecordRoot = new AtomicReference<>();
        SchemaBuddy.parse(schema, schemaWrapper -> {
            if (schemaWrapper.isBranch()) {
                String path = schemaWrapper.getPath();
                log.info("LogicalRecord name:{}", path);
                String pathParent = getParentFromPath(path);
                LogicalRecord logicalRecord = getLogicalRecord(schemaWrapper.getName(), pathParent);
                pathToLogicalRecord.put(path, logicalRecord);

                if (schemaWrapper.isRoot()) {
                    logicalRecordRoot.set(logicalRecord);
                }
                for (SchemaBuddy child : schemaWrapper.getSimpleTypeChildren()) {
                    String childDescription = (String) child.getProp("description");

                    InstanceVariable instanceVariable = getInstanceVariable(child.getName(), childDescription);
                    logicalRecord.addInstanceVariable(instanceVariable);

                    log.info("InstanceVariable name:{}", child.getName());
                }
            }
        });
        return SimpleBuilder.createDatasetBuilder()
                .root(logicalRecordRoot.get())
                .build();
    }

    public String generateSimpleTemplateAsJsonString() {
        try {
            Dataset dataset = generateSimpleTemplate();
            return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
                    .writer(getFilterProvider())
                    .writeValueAsString(dataset);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private FilterProvider getFilterProvider() {
        return new SimpleFilterProvider()
                .addFilter("LogicalRecord_MinimumFilter", SimpleBeanPropertyFilter.serializeAllExcept(logicalRecordFilter))
                .addFilter("InstanceVariable_MinimumFilter", SimpleBeanPropertyFilter.serializeAllExcept(instanceVariableFilter));
    }

    private String getParentFromPath(String path) {
        String[] split = path.split("/");
        return Arrays.stream(split).limit(split.length - 1L).collect(Collectors.joining("/"));
    }

    private InstanceVariable getInstanceVariable(String name, String description) {
        return SimpleBuilder.createInstanceVariableBuilder()
                .name(name)
                .description(description != null ? description : name)
                .dataStructureComponentType("MEASURE")
                .identifierComponentIsComposite(false)
                .identifierComponentIsUnique(false)
                .dataStructureComponentRole("ENTITY")
                .representedVariable("RepresentedVariable_DUMMY")
                .sentinelValueDomain("ValueDomain_DUMMY")
                .population("Population_DUMMY")
                .build();
    }

    private LogicalRecord getLogicalRecord(String name, String parentId) {
        LogicalRecord parent = pathToLogicalRecord.getOrDefault(parentId, null);
        LogicalRecord logicalRecord = SimpleBuilder.createLogicalRecordBuilder()
                .name(name)
                .unitType("UnitType_DUMMY")
                .parent(parentId)
                .build();

        if (parent != null) {
            parent.addLogicalRecord(logicalRecord);
        } else {
            log.info("parent for {} not found", parentId);
        }

        return logicalRecord;
    }
}
