package no.ssb.dapla.dataset.doc.model.simple;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonFilter("InstanceVariable_MinimumFilter")
@JsonPropertyOrder({"name", "description", "identifierComponentIsComposite", "identifierComponentIsUnique", "dataStructureComponentRole", "dataStructureComponentType"})
public class Instance {

    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private boolean identifierComponentIsComposite;

    @JsonProperty
    private boolean identifierComponentIsUnique;

    @JsonProperty
    private EnumInfo dataStructureComponentRole;

    @JsonProperty
    private EnumInfo dataStructureComponentType;

    @JsonProperty
    private TypeInfo population;

    @JsonProperty
    private TypeInfo representedVariable;

    @JsonProperty
    private TypeInfo sentinelValueDomain;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeInfo getPopulation() {
        return population;
    }

    public void setPopulation(TypeInfo population) {
        this.population = population;
    }

    public EnumInfo getDataStructureComponentRole() {
        return dataStructureComponentRole;
    }

    public void setDataStructureComponentRole(EnumInfo dataStructureComponentRole) {
        this.dataStructureComponentRole = dataStructureComponentRole;
    }

    public EnumInfo getDataStructureComponentType() {
        return dataStructureComponentType;
    }

    public void setDataStructureComponentType(EnumInfo dataStructureComponentType) {
        this.dataStructureComponentType = dataStructureComponentType;
    }

    public boolean getIdentifierComponentIsComposite() {
        return identifierComponentIsComposite;
    }

    public void setIdentifierComponentIsComposite(boolean identifierComponentIsComposite) {
        this.identifierComponentIsComposite = identifierComponentIsComposite;
    }

    public boolean getIdentifierComponentIsUnique() {
        return identifierComponentIsUnique;
    }

    public void setIdentifierComponentIsUnique(boolean identifierComponentIsUnique) {
        this.identifierComponentIsUnique = identifierComponentIsUnique;
    }

    public TypeInfo getRepresentedVariable() {
        return representedVariable;
    }

    public void setRepresentedVariable(TypeInfo representedVariable) {
        this.representedVariable = representedVariable;
    }

    public TypeInfo getSentinelValueDomain() {
        return sentinelValueDomain;
    }

    public void setSentinelValueDomain(TypeInfo sentinelValueDomain) {
        this.sentinelValueDomain = sentinelValueDomain;
    }
}
