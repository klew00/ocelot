package com.vistatec.ocelot.services;

import com.vistatec.ocelot.segment.model.OcelotSegment;

import java.util.List;

import com.vistatec.ocelot.events.LQIAdditionEvent;
import com.vistatec.ocelot.events.LQIEditEvent;
import com.vistatec.ocelot.events.LQIRemoveEvent;
import com.vistatec.ocelot.events.SegmentTargetResetEvent;
import com.vistatec.ocelot.events.SegmentTargetUpdateEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;

/**
 * Service for performing segment related operations.
 */
public interface SegmentService extends OcelotEventQueueListener {
    // TODO: Remove requirement for segment model to be table based
    public OcelotSegment getSegment(int row);

    public int getNumSegments();

    public void setSegments(List<OcelotSegment> segments);

    public void updateSegmentTarget(SegmentTargetUpdateEvent e);
    public void resetSegmentTarget(SegmentTargetResetEvent e);

    public void addLQI(LQIAdditionEvent e);
    public void editLQI(LQIEditEvent e);
    public void removeLQI(LQIRemoveEvent e);

    public void clearAllSegments();
}
