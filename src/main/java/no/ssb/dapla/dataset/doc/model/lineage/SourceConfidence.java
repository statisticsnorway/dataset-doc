package no.ssb.dapla.dataset.doc.model.lineage;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SourceConfidence {
    private final Collection<Source> sources;

    public SourceConfidence(Collection<Source> sources) {
        this.sources = sources;
    }

    public float getAverageConfidenceOfSources() {
        Optional<Float> confidence = sources.stream().map(Source::getConfidence).reduce(Float::sum);
        float sum = confidence.orElse(0F);
        return sum != 0F ? sum / sources.size() : 0F;
    }

    public String getFieldType() {
        Set<String> types = sources.stream().map(Source::getType).collect(Collectors.toSet());
        return String.join(",", types);

    }
}
