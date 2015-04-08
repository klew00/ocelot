package com.vistatec.ocelot.tm;

import java.io.IOException;
import java.util.List;

/**
 * Service for utilizing TMs.
 */
public interface TmService {
    public List<TmMatch> getFuzzyTermMatches(String segment) throws IOException;

    public List<TmMatch> getConcordanceMatches(String segment) throws IOException;
}
