package no.ssb.dapla.dataset.doc.model.simple;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFilter("InstanceVariable_MinimumFilter")
public class InstanceVariable {

    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private String population;

    @JsonProperty
    private String dataStructureComponentRole;

    @JsonProperty
    private String dataStructureComponentType;

    @JsonProperty
    private boolean identifierComponentIsComposite;

    @JsonProperty
    private boolean identifierComponentIsUnique;

    @JsonProperty
    private String representedVariable;

    @JsonProperty
    private String sentinelValueDomain;

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

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }


    public String getDataStructureComponentRole() {
        return dataStructureComponentRole;
    }

    public void setDataStructureComponentRole(String dataStructureComponentRole) {
        this.dataStructureComponentRole = dataStructureComponentRole;
    }

    public String getDataStructureComponentType() {
        return dataStructureComponentType;
    }

    public void setDataStructureComponentType(String dataStructureComponentType) {
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

    public String getRepresentedVariable() {
        return representedVariable;
    }

    public void setRepresentedVariable(String representedVariable) {
        this.representedVariable = representedVariable;
    }

    public String getSentinelValueDomain() {
        return sentinelValueDomain;
    }

    public void setSentinelValueDomain(String sentinelValueDomain) {
        this.sentinelValueDomain = sentinelValueDomain;
    }
}
