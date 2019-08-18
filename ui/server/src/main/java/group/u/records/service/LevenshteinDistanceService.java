package group.u.records.service;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.springframework.stereotype.Service;

@Service
public class LevenshteinDistanceService {
    public boolean areStringsSufficientlySimilar(String a, String b) {
        return FuzzySearch.ratio(a, b) >= 90;
    }
}
