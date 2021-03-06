/*
 * Copyright (C) 2015, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
package com.vistatec.ocelot.services;

import com.vistatec.ocelot.segment.model.OcelotSegment;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.vistatec.ocelot.events.ItsDocStatsUpdateLqiEvent;
import com.vistatec.ocelot.events.ItsDocStatsAddedProvEvent;
import com.vistatec.ocelot.events.ItsDocStatsClearEvent;
import com.vistatec.ocelot.events.ItsDocStatsRecalculateEvent;
import com.vistatec.ocelot.events.ItsDocStatsRemovedLqiEvent;
import com.vistatec.ocelot.events.LQIAdditionEvent;
import com.vistatec.ocelot.events.LQIEditEvent;
import com.vistatec.ocelot.events.LQIModificationEvent;
import com.vistatec.ocelot.events.LQIRemoveEvent;
import com.vistatec.ocelot.events.ProvenanceAddEvent;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.events.SegmentTargetResetEvent;
import com.vistatec.ocelot.events.SegmentTargetUpdateEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.segment.model.SegmentVariant;

/**
 * Service for performing segment related operations.
 * FIXME: Should not contain the app segment data model.
 */
public class SegmentServiceImpl implements SegmentService {
    // TODO: remove segments (data) from service implementation
    private List<OcelotSegment> segments = new ArrayList<>(100);
    private final OcelotEventQueue eventQueue;

    @Inject
    public SegmentServiceImpl(OcelotEventQueue eventQueue) {
        this.eventQueue = eventQueue;
    }

    @Override
    public OcelotSegment getSegment(int row) {
        return segments.get(row);
    }

    @Override
    public int getNumSegments() {
        return segments.size();
    }

    @Override
    public void setSegments(List<OcelotSegment> segments) {
        this.segments = segments;
        eventQueue.post(new ItsDocStatsRecalculateEvent(segments));
    }

    @Subscribe
    @Override
    public void updateSegmentTarget(SegmentTargetUpdateEvent e) {
        OcelotSegment seg = e.getSegment();
        SegmentVariant updatedTarget = e.getUpdatedTarget();
        boolean updatedSeg = seg.updateTarget(updatedTarget);
        if (updatedSeg) {
            eventQueue.post(new SegmentEditEvent(seg));
        }
    }

    @Subscribe
    @Override
    public void resetSegmentTarget(SegmentTargetResetEvent e) {
        OcelotSegment seg = e.getSegment();
        if (seg.hasOriginalTarget() && !seg.getTargetDiff().isEmpty()) {
            if (seg.resetTarget()) {
                eventQueue.post(new SegmentEditEvent(seg));
            }
        }
    }

    @Subscribe
    @Override
    public void addLQI(LQIAdditionEvent e) {
        OcelotSegment seg = e.getSegment();
        LanguageQualityIssue lqi = e.getLQI();
        seg.addLQI(lqi);
        eventQueue.post(new ItsDocStatsUpdateLqiEvent(lqi));
        eventQueue.post(new SegmentEditEvent(seg));
        eventQueue.post(new LQIModificationEvent(lqi, seg));
    }

    @Subscribe
    @Override
    public void editLQI(LQIEditEvent e) {
        LanguageQualityIssue editedLQI = e.getLQI();

        OcelotSegment seg = e.getSegment();
        LanguageQualityIssue segmentLQI = e.getSegmentLQI();
        segmentLQI.setType(editedLQI.getType());
        segmentLQI.setComment(editedLQI.getComment());
        segmentLQI.setSeverity(editedLQI.getSeverity());
        segmentLQI.setProfileReference(editedLQI.getProfileReference());
        segmentLQI.setEnabled(editedLQI.isEnabled());

        eventQueue.post(new ItsDocStatsUpdateLqiEvent(segmentLQI));
        eventQueue.post(new SegmentEditEvent(seg));
        eventQueue.post(new LQIModificationEvent(segmentLQI, seg));
    }

    @Subscribe
    @Override
    public void removeLQI(LQIRemoveEvent e) {
        OcelotSegment seg = e.getSegment();
        LanguageQualityIssue lqi = e.getLQI();
        seg.removeLQI(lqi);
        eventQueue.post(new ItsDocStatsRemovedLqiEvent(segments));
        eventQueue.post(new SegmentEditEvent(seg));
        eventQueue.post(new LQIModificationEvent(lqi, seg));
    }

    @Subscribe
    public void addProvenance(ProvenanceAddEvent e) {
        Provenance prov = e.getProvenance();
        OcelotSegment seg = e.getSegment();
        seg.addProvenance(prov);
        if (e.isOcelotProv) {
            seg.setOcelotProvenance(true);
        }
        eventQueue.post(new ItsDocStatsAddedProvEvent(prov));
    }

    @Override
    public void clearAllSegments() {
        this.segments.clear();
        eventQueue.post(new ItsDocStatsClearEvent());
    }
}
