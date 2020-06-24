package no.ssb.dapla.dataset.doc.template;

import no.ssb.avro.convert.core.SchemaBuddy;

import java.util.List;

public abstract class SchemaTraverse<T> {

    private T root;

    protected T traverse(SchemaBuddy schema, T parent) {
        if (schema.isArrayType()) {
            List<SchemaBuddy> children = schema.getChildren();
            if (children.size() != 1) {
                throw new IllegalStateException("Avro Array can only have 1 child: was:" + schema.toString(true) + "‰n");
            }
            traverse(children.get(0), parent);
            return root;
        }

        if (schema.isBranch()) {
            T childStruct = processStruct(schema, parent);
            if (schema.isRoot()) {
                root = childStruct;
            }
            for (SchemaBuddy childSchema : schema.getChildren()) {
                traverse(childSchema, childStruct);
            }
        } else {
            processField(schema, parent);
        }
        return root;
    }

    protected abstract T processStruct(SchemaBuddy schemaBuddy, T parent);

    protected abstract void processField(SchemaBuddy schemaBuddy, T parent);
}
