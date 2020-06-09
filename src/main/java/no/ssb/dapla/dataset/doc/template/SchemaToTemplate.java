package no.ssb.dapla.dataset.doc.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class SchemaToTemplate {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Schema schema;
    private final HashMap<String, LogicalRecord> pathToLogicalRecord = new HashMap<>();
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

    public ObjectNode generateTemplate() {
        ObjectNode root = new ObjectMapper().createObjectNode();
        ObjectNode dataset = root.putObject("dataset");
        dataset.put("name", "-person-");
        SchemaBuddy schemaBuddy = SchemaBuddy.parse(schema);
        ArrayNode ivs = dataset.putArray("instanceVariables");
        ArrayNode lrs = dataset.putArray("logicalRecords");

        traverse(schemaBuddy, dataset, 0, ivs, lrs);
        return root;
    }

    public void traverse(SchemaBuddy schemaBuddy, ObjectNode node, int level, ArrayNode instanceVariables, ArrayNode logicalRecords) {
        if (schemaBuddy.isArrayType()) {
            List<SchemaBuddy> children = schemaBuddy.getChildren();
            if (children.size() != 1) {
                throw new IllegalStateException("Avro Array can only have 1 child: was:" + schemaBuddy.toString(true) + "‰n");
            }
            traverse(children.get(0), node, level, instanceVariables, logicalRecords);
            return;
        }
        System.out.println(getIntendString(level) + schemaBuddy.getName());

        if (schemaBuddy.isBranch()) {
            ArrayNode ivs = node.putArray("instanceVariables");
            ArrayNode lrs = node.putArray("logicalRecords");
            for (SchemaBuddy child : schemaBuddy.getChildren()) {
                ObjectNode jsonNode = logicalRecords.addObject();
                traverse(child, jsonNode, level + 1, ivs, lrs);
            }

//            ArrayNode instanceVariables = node.putArray("InstanceVariables");
//            for (SchemaBuddy child : schemaBuddy.getSimpleTypeChildren()) {
//                System.out.println(getIntendString(level + 1) + child.getName());
//                instanceVariables.add(getInstanceVariableAsJsonNode(child.getName(), child.getName()));
//            }
//            List<SchemaBuddy> complexTypeChildren = schemaBuddy.getComplexTypeChildren();
//            if (complexTypeChildren.isEmpty()) return;
//
//            ArrayNode logicalRecord = node.putArray("LogicalRecords");
//            for (SchemaBuddy child : complexTypeChildren) {
//                logicalRecord.add(getLogicalRecordAsJsonNode(child.getName(), child.getName()));
//                traverse(child, node, level + 1);
//            }
        } else {
            System.out.println("We have InstanceVariable:" + schemaBuddy.getName());
//            instanceVariables.addObject().put("name", schemaBuddy.getName());
            instanceVariables.add(getInstanceVariableAsJsonNode(schemaBuddy.getName(), schemaBuddy.getName()));
//            ObjectNode instanceVariable = node.putObject("InstanceVariables");
//            instanceVariable
        }
    }

    public Dataset generateSimpleTemplate() {
        AtomicReference<LogicalRecord> logicalRecordRoot = new AtomicReference<>();
        SchemaBuddy.parse(schema, schemaWrapper -> {
            if (schemaWrapper.isBranch()) {
                String path = schemaWrapper.getPath();
                log.info("Type :{}", schemaWrapper.getType());
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
                .addFilter("LogicalRecord_MinimumFilter", SimpleBeanPropertyFilter.serializeAllExcept(logicalRecordFilter.toArray(new String[0])))
                .addFilter("InstanceVariable_MinimumFilter", SimpleBeanPropertyFilter.serializeAllExcept(instanceVariableFilter.toArray(new String[0])));
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

    private JsonNode getInstanceVariableAsJsonNode(String name, String description) {
        InstanceVariable instanceVariable = getInstanceVariable(name, description);
        return getJsonNode(instanceVariable);
    }

    private JsonNode getJsonNode(Object pojo) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = objectMapper
                    .writer(getFilterProvider()) // Adding filter returns ObjectWriter, so need to go through String
                    .writeValueAsString(pojo);
            return objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
//            log.info("parent for {} not found", parentId);
        }

        return logicalRecord;
    }

    private JsonNode getLogicalRecordAsJsonNode(String name, String description) {
        LogicalRecord logicalRecord = getLogicalRecord(name, description);
        return getJsonNode(logicalRecord);
    }

    String getIntendString(int level) {
        if (level == 0) return "";
        if (level == 1) return " |-- ";
        return String.join("", Collections.nCopies(level, " |   ")) + " |-- ";
    }
}
