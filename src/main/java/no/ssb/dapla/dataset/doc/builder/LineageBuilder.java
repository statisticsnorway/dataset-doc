package no.ssb.dapla.dataset.doc.builder;

import no.ssb.dapla.dataset.doc.model.lineage.Dataset;
import no.ssb.dapla.dataset.doc.model.lineage.InstanceVariable;
import no.ssb.dapla.dataset.doc.model.lineage.LogicalRecord;
import no.ssb.dapla.dataset.doc.model.lineage.Source;
import no.ssb.dapla.dataset.doc.template.SchemaToLineageTemplate;
import no.ssb.dapla.dataset.doc.template.SchemaWithPath;
import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public static SchemaToLineageBuilder createSchemaToLineageBuilder() {
        return new SchemaToLineageBuilder();
    }

    public static SourceBuilder crateSourceBuilder() {
        return new SourceBuilder();
    }

    public static class SourceBuilder {
        private String field;
        private String path;
        private String type;
        private long version;
        private float confidence;

        public SourceBuilder field(String field) {
            this.field = field;
            return this;
        }

        public SourceBuilder path(String path) {
            this.path = path;
            return this;
        }

        public SourceBuilder version(long version) {
            this.version = version;
            return this;
        }

        public SourceBuilder confidence(float confidence) {
            this.confidence = confidence;
            return this;
        }

        public SourceBuilder type(String type) {
            this.type = type;
            return this;
        }

        public Source build() {
            Source source = new Source(field, path, version);
            source.setConfidence(confidence);
            source.setType(type);
            return source;
        }
    }

    public static class SchemaToLineageBuilder {
        private final List<SchemaWithPath> schemaWithPaths = new ArrayList<>();
        Schema outputSchema;

        public SchemaToLineageBuilder addInput(SchemaWithPath schemaWithPath) {
            schemaWithPaths.add(schemaWithPath);
            return this;
        }

        public SchemaToLineageBuilder outputSchema(Schema outputSchema) {
            this.outputSchema = outputSchema;
            return this;
        }

        public SchemaToLineageTemplate build() {
            return new SchemaToLineageTemplate(schemaWithPaths, outputSchema);
        }
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

        public InstanceVariableBuilder name(String name) {
            instanceVariable.setName(name);
            return this;
        }

        public InstanceVariableBuilder type(String type) {
            instanceVariable.setType(type);
            return this;
        }

        public InstanceVariableBuilder confidence(Float confidence) {
            instanceVariable.setConfidence(confidence);
            return this;
        }

        public InstanceVariableBuilder addSource(Source source) {
            instanceVariable.addSource(source);
            return this;
        }

        public InstanceVariableBuilder addSources(Collection<Source> sources) {
            instanceVariable.addSources(sources);
            return this;
        }

        public InstanceVariable build() {
            return instanceVariable;
        }
    }
}
