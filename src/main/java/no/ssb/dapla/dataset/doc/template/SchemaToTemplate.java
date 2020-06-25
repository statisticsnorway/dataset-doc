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
import java.util.List;

public class SchemaToTemplate extends SchemaTraverse<LogicalRecord> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Schema schema;
    private final List<String> instanceVariableFilter = new ArrayList<>();
    private final List<String> logicalRecordFilter = new ArrayList<>();

    /**
     * @param schema
     * @param path
     * @deprecated (Path is no longer necessary)
     */
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
        if (!instanceVariableFilter.isEmpty()) {
            throw new IllegalStateException("InstanceVariableFilter already contains " + ignoreFields + ". use addInstanceVariableFilter to add");
        }
        return addInstanceVariableFilter(ignoreFields);
    }

    public SchemaToTemplate addInstanceVariableFilter(String... ignoreFields) {
        instanceVariableFilter.addAll(Arrays.asList(ignoreFields));
        return this;
    }

    public SchemaToTemplate withLogicalRecordFilterFilter(String... ignoreFields) {
        if (!logicalRecordFilter.isEmpty()) {
            throw new IllegalStateException("LogicalRecord already contains " + ignoreFields + ". use addLogicalRecordFilter to add");
        }
        return addLogicalRecordFilterFilter(ignoreFields);
    }

    public SchemaToTemplate addLogicalRecordFilterFilter(String... ignoreFields) {
        logicalRecordFilter.addAll(Arrays.asList(ignoreFields));
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

    public String generateSimpleTemplateAsJsonString() {
        try {
            Dataset dataset = generateTemplate();
            return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
                    .writer(getFilterProvider())
                    .writeValueAsString(dataset);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Dataset generateTemplate() {
        SchemaBuddy schemaBuddy = SchemaBuddy.parse(schema);

        LogicalRecord root = traverse(schemaBuddy);
        return SimpleBuilder.createDatasetBuilder()
                .root(root)
                .build();
    }

    @Override
    protected LogicalRecord processStruct(SchemaBuddy schemaBuddy, LogicalRecord parent) {
        LogicalRecord childLogicalRecord = getLogicalRecord(schemaBuddy.getName());
        if (parent != null) {
            parent.addLogicalRecord(childLogicalRecord);
        }
        return childLogicalRecord;
    }

    @Override
    protected void processField(SchemaBuddy schemaBuddy, LogicalRecord parent) {
        String description = (String) schemaBuddy.getProp("description");
        parent.addInstanceVariable(getInstanceVariable(schemaBuddy.getName(), description));
    }

    private FilterProvider getFilterProvider() {
        return new SimpleFilterProvider()
                .addFilter("LogicalRecord_MinimumFilter", SimpleBeanPropertyFilter.serializeAllExcept(logicalRecordFilter.toArray(new String[0])))
                .addFilter("InstanceVariable_MinimumFilter", SimpleBeanPropertyFilter.serializeAllExcept(instanceVariableFilter.toArray(new String[0])));
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

    private LogicalRecord getLogicalRecord(String name) {
        return SimpleBuilder.createLogicalRecordBuilder()
                .name(name)
                .unitType("UnitType_DUMMY")
                .build();
    }
}
