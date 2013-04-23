package com.spartansoftwareinc;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * Detail view showing ITS metadata on the selected LQI in SegmentAttributeView.
 */
public class LanguageQualityIssueView extends JScrollPane {
    private JLabel dataCategoryLabel, segmentLabel;
    private JLabel typeLabel, commentLabel, severityLabel,
            profileLabel, enabledLabel;
    private JLabel segment, type, severity, profile, enabled;
    private JTextArea comment;

    public LanguageQualityIssueView() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gridBag = new GridBagConstraints();
        gridBag.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBag.insets = new Insets(0,10,5,10); // Pad text
        gridBag.gridwidth = 1;

        dataCategoryLabel = new JLabel ();
        gridBag.gridx = 0;
        gridBag.gridy = 0;
        mainPanel.add(dataCategoryLabel, gridBag);

        segmentLabel = new JLabel ();
        gridBag.gridx = 0;
        gridBag.gridy = 1;
        mainPanel.add(segmentLabel, gridBag);

        segment = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 1;
        mainPanel.add(segment, gridBag);

        typeLabel = new JLabel();
        gridBag.gridx = 0;
        gridBag.gridy = 2;
        mainPanel.add(typeLabel, gridBag);

        type = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 2;
        mainPanel.add(type, gridBag);
        
        commentLabel = new JLabel();
        gridBag.gridx = 0;
        gridBag.gridy = 3;
        mainPanel.add(commentLabel, gridBag);
        
        comment = new JTextArea();
        comment.setLineWrap(true);
        comment.setWrapStyleWord(true);
        comment.setVisible(false);
        gridBag.gridx = 1;
        gridBag.gridy = 3;
        mainPanel.add(comment, gridBag);
        
        severityLabel = new JLabel();
        gridBag.gridx = 0;
        gridBag.gridy = 4;
        mainPanel.add(severityLabel, gridBag);
        
        severity = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 4;
        mainPanel.add(severity, gridBag);
        
        profileLabel = new JLabel();
        gridBag.gridx = 0;
        gridBag.gridy = 5;
        mainPanel.add(profileLabel, gridBag);
        
        profile = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 5;
        mainPanel.add(profile, gridBag);
        
        enabledLabel = new JLabel();
        gridBag.gridx = 0;
        gridBag.gridy = 6;
        mainPanel.add(enabledLabel, gridBag);
        
        enabled = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 6;
        mainPanel.add(enabled, gridBag);

        Dimension prefSize = new Dimension(500, 200);
        setPreferredSize(prefSize);
        //Padding
        setBorder(new EmptyBorder(10,10,10,10));
        setViewportView(mainPanel);
    }

    public void setMetadata(Segment selectedSegment, ITSMetadata data) {
        LanguageQualityIssue lqi = (LanguageQualityIssue) data;
        dataCategoryLabel.setText("Language Quality Issue");
        segmentLabel.setText("Segment #");
        segment.setText(selectedSegment.getSegmentNumber()+"");
        typeLabel.setText("Type");
        type.setText(lqi.getType());
        commentLabel.setText("Comment");
        comment.setText(lqi.getComment());
        comment.setVisible(true);
        severityLabel.setText("Severity");
        severity.setText(lqi.getSeverity()+"");
        profileLabel.setText("Profile Reference");
        profile.setText(lqi.getProfileReference() != null ?
                lqi.getProfileReference().toString() : "");
        enabledLabel.setText("Enabled");
        enabled.setText(lqi.isEnabled() ? "yes" : "no");
    }

    public void clearDisplay() {
        dataCategoryLabel.setText("");
        segmentLabel.setText("");
        segment.setText("");
        typeLabel.setText("");
        type.setText("");
        commentLabel.setText("");
        comment.setText("");
        comment.setVisible(false);
        severityLabel.setText("");
        severity.setText("");
        profileLabel.setText("");
        profile.setText("");
        enabledLabel.setText("");
        enabled.setText("");
    }
}