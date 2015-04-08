package com.vistatec.ocelot.tm.okapi;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.vistatec.ocelot.config.OcelotConfigService;
import com.vistatec.ocelot.config.ConfigTransferService;
import com.vistatec.ocelot.config.xml.RootConfig;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SimpleSegment;
import com.vistatec.ocelot.tm.TmMatch;
import com.vistatec.ocelot.tm.TmPenalizer;
import com.vistatec.ocelot.tm.TmTmxWriter;

public class TestOkapiTmService {
    private final Mockery mockery = new Mockery();
    private final ConfigTransferService cfgXService = mockery.mock(ConfigTransferService.class);
    private OkapiTmService tmService;
    private File testTm;

    @Before
    public void before() throws URISyntaxException, IOException, ConfigTransferService.TransferException {
        File testTmIndices = OkapiTmTestHelpers.getTestOkapiTmDir();
        OkapiTmTestHelpers.deleteDirectory(testTmIndices);
        testTmIndices.mkdirs();

        this.testTm = new File(TestOkapiTmService.class.getResource("simple_tm.tmx").toURI());
    }

    @Test
    public void testFuzzy() throws ConfigTransferService.TransferException, URISyntaxException, IOException {
        this.tmService = new OkapiTmServiceBuilder()
                .fuzzyThreshold(1)
                .maxResults(5)
                .build();
        this.tmService.importTmx("simple_tm", testTm);

        List<TmMatch> appleOrangeResults = tmService.getFuzzyTermMatches("apple orange");
        assertEquals(2, appleOrangeResults.size());
        assertEquals("apple orange pear", appleOrangeResults.get(0).getSource().getDisplayText());
        assertEquals("simple_tm", appleOrangeResults.get(0).getTmOrigin());
        assertEquals("orange apple pear", appleOrangeResults.get(1).getSource().getDisplayText());
        assertEquals("simple_tm", appleOrangeResults.get(1).getTmOrigin());

        List<TmMatch> orangeAppleResults = tmService.getFuzzyTermMatches("orange apple");
        assertEquals(2, appleOrangeResults.size());
        assertEquals("orange apple pear", orangeAppleResults.get(0).getSource().getDisplayText());
        assertEquals("simple_tm", orangeAppleResults.get(0).getTmOrigin());
        assertEquals("apple orange pear", orangeAppleResults.get(1).getSource().getDisplayText());
        assertEquals("simple_tm", orangeAppleResults.get(1).getTmOrigin());

        List<TmMatch> watermelonResults = tmService.getFuzzyTermMatches("watermelon");
        assertEquals(1, watermelonResults.size());
        assertEquals("watermelon pineapple", watermelonResults.get(0).getSource().getDisplayText());
        assertEquals("simple_tm", watermelonResults.get(0).getTmOrigin());
    }

    @Test
    public void testConcordance() throws ConfigTransferService.TransferException, URISyntaxException, IOException {
        this.tmService = new OkapiTmServiceBuilder()
                .fuzzyThreshold(1)
                .maxResults(5)
                .build();
        this.tmService.importTmx("simple_tm", testTm);

        List<TmMatch> results = tmService.getConcordanceMatches("apple");
        assertEquals(4, results.size());
    }

    @AfterClass
    public static void cleanup() throws URISyntaxException {
        OkapiTmTestHelpers.deleteDirectory(OkapiTmTestHelpers.getTestOkapiTmDir());
    }

    private class OkapiTmServiceBuilder {
        private int fuzzyThreshold, maxResults;

        public OkapiTmServiceBuilder fuzzyThreshold(int threshold) {
            this.fuzzyThreshold = threshold;
            return this;
        }

        public OkapiTmServiceBuilder maxResults(int max) {
            this.maxResults = max;
            return this;
        }

        public OkapiTmService build() throws ConfigTransferService.TransferException, URISyntaxException, IOException {
            TmTmxWriter tmxWriter = mockery.mock(TmTmxWriter.class);
            final TmPenalizer penalizer = mockery.mock(TmPenalizer.class);

            final RootConfig config = new RootConfig();
            config.getTmManagement().setFuzzyThreshold(fuzzyThreshold);
            config.getTmManagement().setMaxResults(maxResults);
            mockery.checking(new Expectations() {
                {
                    allowing(cfgXService).parse();
                        will(returnValue(config));
                    allowing(cfgXService).save(with(any(RootConfig.class)));
                    allowing(penalizer).applyPenalties(with(any(List.class)));
                        will(new OkapiTmTestHelpers.ReturnFirstArgument());
                }
            });
            OcelotConfigService cfgService = new OcelotConfigService(cfgXService);
            return new OkapiTmService(
                    new OkapiTmManager(OkapiTmTestHelpers.getTestOkapiTmDir(), cfgService, tmxWriter),
                    penalizer,
                    cfgService);
        }
    }
}
