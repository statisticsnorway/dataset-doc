package no.ssb.dapla.dataset.doc.model.lineage;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class SourceConfidence {
    private final Collection<Source> sources;

    private final float matchScore;

    public SourceConfidence(Collection<Source> sources) {
        this.sources = sources;

        matchScore = sources.stream().map(Source::getMatchScore).reduce((a, b) -> a * b).orElse(0F);
    }

    public float getAverageConfidenceOfSources() {
        Optional<Float> confidence = sources.stream().map(Source::getConfidence).reduce(Float::sum);
        float sum = confidence.orElse(0F);
        return sum != 0F ? sum / sources.size() : 0F;
    }

    public Collection<String> getTypeCandidates() {
        if (matchScore < 1.0F) {
            return sources.stream().map(Source::getType).collect(Collectors.toSet());
        }
        return Collections.emptyList();
    }

    public String getFieldType() {
        if (matchScore >= 1.0f) {
            return "inherited";
        }
        return "";
    }
}
