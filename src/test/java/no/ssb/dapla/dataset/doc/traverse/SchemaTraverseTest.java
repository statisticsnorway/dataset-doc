package no.ssb.dapla.dataset.doc.traverse;

import no.ssb.avro.convert.core.SchemaBuddy;
import no.ssb.dapla.dataset.doc.template.TestUtils;
import org.apache.avro.Schema;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.StringJoiner;


class SchemaTraverseTest {

    public static class Field implements TraverseField<Field> {

        private final List<Field> children = new ArrayList<>();
        private Field parent;
        private String name;

        public Field() {

        }

        public Field(String name, Field parent) {
            this.name = name;
            this.parent = parent;
        }

        @Override
        public void addChild(Field child) {
            children.add(child);
        }

        @Override
        public String getName() {
            return name;
        }

        public List<String> getParents() {
            Field currentParent = parent;
            List<String> parentList = new ArrayList<>();
            while (currentParent != null) {
                parentList.add(currentParent.name);
                currentParent = currentParent.parent;
            }
            return parentList;
        }

        public String getPath() {
            StringJoiner joiner = new StringJoiner(".");
            for (ListIterator<String> iter = getParents().listIterator(getParents().size()); iter.hasPrevious(); ) {
                joiner.add(iter.previous());
            }
            return joiner.add(name).toString();
        }

        String getIntendString() {
            int size = getParents().size();
            if (size == 0) return "";
            if (size == 1) return " |-- ";
            return String.join("", Collections.nCopies(size - 1, " |   ")) + " |-- ";
        }

        public String toString(boolean recursive) {
            StringBuilder sb = new StringBuilder();

            if (recursive) {
                sb.append(String.format("%s%s%n", getIntendString(), name));
                for (Field child : children) {
                    sb.append(child.toString(true));
                }
            } else {
                sb.append(name);
            }
            return sb.toString();
        }

    }
    static class MySchemaTraverse extends SchemaTraverse<Field> {

        @Override
        protected Field createChild(SchemaBuddy schemaBuddy, Field parent) {
            return new Field(schemaBuddy.getName(), parent);
        }

        @Override
        protected void processField(SchemaBuddy schemaBuddy, Field parent) {
            parent.addChild(new Field(schemaBuddy.getName(), parent));
        }
    }

    @Test
    void traverse() {
        Schema inputSchemaSkatt =  TestUtils.loadSchema("testdata/skatt-v0.68.avsc");

        MySchemaTraverse mySchemaTraverse = new MySchemaTraverse();
        Field traverse = mySchemaTraverse.traverse(SchemaBuddy.parse(inputSchemaSkatt));
        System.out.println(traverse.toString(true));

    }
}