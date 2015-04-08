package com.vistatec.ocelot.tm.okapi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.inject.Inject;
import com.vistatec.ocelot.config.ConfigService;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.tm.TmMatch;
import com.vistatec.ocelot.tm.TmPenalizer;
import com.vistatec.ocelot.tm.TmService;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.tm.pensieve.common.TmHit;

/**
 * Use Okapi Pensieve to search the Lucene index.
 */
public class OkapiTmService implements TmService {
    private final OkapiTmManager manager;
    private final TmPenalizer penalizer;
    private final ConfigService cfgService;

    @Inject
    public OkapiTmService(OkapiTmManager manager, TmPenalizer penalizer, ConfigService cfgService) {
        this.manager = manager;
        this.penalizer = penalizer;
        this.cfgService = cfgService;
    }

    public void importTmx(String tmName, File tmx) throws IOException {
        manager.importTmx(tmName, tmx);
    }

    @Override
    public List<TmMatch> getFuzzyTermMatches(String segment) throws IOException {
        Iterator<OkapiTmManager.TmPair> tmPairs = manager.getSeekers();
        int pensieveThreshold = new Double(cfgService.getFuzzyThreshold()).intValue();

        List<TmMatch> matches = new ArrayList<>();
        while (tmPairs.hasNext()) {
            OkapiTmManager.TmPair tmPair = tmPairs.next();
            List<TmHit> results = tmPair.getSeeker().searchFuzzy(
                    new TextFragment(segment),
                    pensieveThreshold, cfgService.getMaxResults(), null);

            matches.addAll(convertOkapiTmHit(tmPair.getTmOrigin(), results));
        }
        return penalizer.applyPenalties(matches);
    }

    @Override
    public List<TmMatch> getConcordanceMatches(String segment) throws IOException {
        Iterator<OkapiTmManager.TmPair> tmPairs = manager.getSeekers();
        int pensieveThreshold = new Double(cfgService.getFuzzyThreshold()).intValue();

        List<TmMatch> matches = new ArrayList<>();
        while(tmPairs.hasNext()) {
            OkapiTmManager.TmPair tmPair = tmPairs.next();
            List<TmHit> results = tmPair.getSeeker().searchSimpleConcordance(
                    segment, pensieveThreshold, cfgService.getMaxResults(), null);

            matches.addAll(convertOkapiTmHit(tmPair.getTmOrigin(), results));
        }
        return penalizer.applyPenalties(matches);
    }

    public List<TmMatch> convertOkapiTmHit(String tmOrigin, List<TmHit> leverageResults) {
        List<TmMatch> matches = new ArrayList<>();
        for (TmHit hit : leverageResults) {
            matches.add(new PensieveTmMatch(tmOrigin, hit));
        }
        return matches;
    }
}
