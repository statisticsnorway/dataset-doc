package no.ssb.dapla.dataset.doc.template;

import java.util.List;

public class ValidateResult {
    private final List<String> missingTemplateFields;
    private final List<String> missingSchemaFields;

    public ValidateResult(List<String> missingTemplateFields, List<String> missingSchemaFields) {
        this.missingTemplateFields = missingTemplateFields;
        this.missingSchemaFields = missingSchemaFields;
    }

    public boolean isOk() {
        return missingSchemaFields.isEmpty() && missingTemplateFields.isEmpty();
    }

    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        if (missingTemplateFields.size() > 0) {
            sb.append("New fields added in spark schema ").append(missingTemplateFields).append(" is not in doc template\n");
        }
        if (missingSchemaFields.size() > 0) {
            sb.append("Fields document in template is no longer in spark schema ").append(missingSchemaFields).append("\n");
        }
        return sb.toString();
    }

}
