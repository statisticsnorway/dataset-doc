package no.ssb.dapla.dataset.doc.model.lineage;

import no.ssb.avro.convert.core.SchemaBuddy;
import no.ssb.dapla.dataset.doc.traverse.ParentAware;
import no.ssb.dapla.dataset.doc.traverse.PathTraverse;
import no.ssb.dapla.dataset.doc.traverse.SchemaTraverse;
import no.ssb.dapla.dataset.doc.traverse.TraverseField;
import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FieldFinder extends SchemaTraverse<FieldFinder.Record> {

    static class Record implements TraverseField<FieldFinder.Record>, ParentAware {
        final Record parent;
        final String name;
        private final List<Record> children = new ArrayList<>();
        private final List<Field> fields = new ArrayList<>();

        public Record(Record parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        @Override
        public ParentAware getParent() {
            return parent;
        }

        @Override
        public void addChild(Record child) {
            children.add(child);
        }

        @Override
        public String getName() {
            return name;
        }

        public List<Record> getChildren() {
            return children;
        }

        public String getPath() {
            PathTraverse<Record> pathTraverse = new PathTraverse<>(this);
            return pathTraverse.getPath("spark_schema");
        }

        public void addField(Field field) {
            fields.add(field);
        }

        public List<Field> find(String name) {
            return fields.stream()
                    .filter(i -> i.isMatch(name))
                    .collect(Collectors.toList());
        }

    }

    static class Field {
        final String name;

        public String getPath() {
            return path;
        }

        final String path;

        public boolean isMatch(String name) {
            return name.equals(this.name);
        }

//        public boolean checkPartialMatchAndSetConfidence(String name) {
//            if (confidence != null) {
//                System.out.println("Confidence should not be set, was:" + confidence);
////            throw new IllegalStateException("Confidence should not be set, was:" + confidence);
//            }
//            float existingConfidence = confidence != null ? confidence : 1.0F;
//            if (this.name.equals(name)) {
//                setConfidence(existingConfidence);
//                return true;
//            }
//            if (this.name.contains(name)) {
//                setConfidence(existingConfidence * (name.length() / (float) this.name.length()));
//                return true;
//            }
//            if (name.contains(this.name)) {
//                setConfidence(existingConfidence * (this.name.length() / (float) name.length()));
//                return true;
//            }
//            return false;
//        }


        public Field(String name, String path) {
            this.name = name;
            this.path = path;
        }
    }

    final SchemaBuddy schemaBuddy;
    final Record root;

    public FieldFinder(Schema schema) {
        this.schemaBuddy = SchemaBuddy.parse(schema);
        root = traverse(schemaBuddy);
    }

    public List<String> getPaths(String field) {
        return find(field).stream().map(Field::getPath).collect(Collectors.toList());
    }

    public List<Field> find(String field) {
        List<Field> result = new ArrayList<>();
        search(field, root, result);
        return result;
    }

    private void search(String name, Record parent, List<Field> result) {
        result.addAll(parent.find(name));
        for (Record child : parent.getChildren()) {
            search(name, child, result);
        }
    }

    @Override
    protected FieldFinder.Record createChild(SchemaBuddy schemaBuddy, FieldFinder.Record parent) {
        return new FieldFinder.Record(parent, schemaBuddy.getName());
    }

    @Override
    protected void processField(SchemaBuddy schemaBuddy, Record parent) {
        // TODO: Find a better to deal with "spark_schema" root
        String path = parent.getPath().equals("spark_schema") ? "" : parent.getPath() + ".";
        Field field = new Field(schemaBuddy.getName(), path + schemaBuddy.getName());
        parent.addField(field);
    }
}
