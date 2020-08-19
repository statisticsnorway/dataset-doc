package no.ssb.dapla.dataset.doc.model.simple;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonFilter("InstanceVariable_MinimumFilter")
public class Instance {

    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private Info population;

    @JsonProperty
    private Info dataStructureComponentRole;

    @JsonProperty
    private Info dataStructureComponentType;

    @JsonProperty
    private boolean identifierComponentIsComposite;

    @JsonProperty
    private boolean identifierComponentIsUnique;

    @JsonProperty
    private Info representedVariable;

    @JsonProperty
    private Info sentinelValueDomain;

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

    public Info getPopulation() {
        return population;
    }

    public void setPopulation(Info population) {
        this.population = population;
    }

    public Info getDataStructureComponentRole() {
        return dataStructureComponentRole;
    }

    public void setDataStructureComponentRole(Info dataStructureComponentRole) {
        this.dataStructureComponentRole = dataStructureComponentRole;
    }

    public Info getDataStructureComponentType() {
        return dataStructureComponentType;
    }

    public void setDataStructureComponentType(Info dataStructureComponentType) {
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

    public Info getRepresentedVariable() {
        return representedVariable;
    }

    public void setRepresentedVariable(Info representedVariable) {
        this.representedVariable = representedVariable;
    }

    public Info getSentinelValueDomain() {
        return sentinelValueDomain;
    }

    public void setSentinelValueDomain(Info sentinelValueDomain) {
        this.sentinelValueDomain = sentinelValueDomain;
    }
}
