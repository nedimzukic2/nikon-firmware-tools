package com.nikonhacker.gui.component;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSelectionPanel extends JPanel implements ActionListener {
    String label;
    JLabel jlabel;
    JButton button;
    JTextField textField;
    boolean directoryMode;
    private List<DependentField> dependentFields;
    private String dialogTitle;

    public FileSelectionPanel(String label, JTextField textField, boolean directoryMode) {
        super();
        init(label, textField, directoryMode, new ArrayList<DependentField>(), dialogTitle);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        jlabel.setEnabled(enabled);
        button.setEnabled(enabled);
        textField.setEnabled(enabled);
    }

    /**
     *
     * @param label
     * @param textField
     * @param directoryMode
     * @param dependentFields : a list of text fields that will be filled based on this one. Each field is associated with a suffix to customize secondary field filename. If it contains a dot, it replaces the extension, otherwise it replaces the full filename
     */
    public FileSelectionPanel(String label, JTextField textField, boolean directoryMode, List<DependentField> dependentFields) {
        super();
        init(label, textField, directoryMode, dependentFields, null);
    }

    /**
     *
     * @param label
     * @param textField
     * @param directoryMode
     * @param dependentFields : a list of text fields that will be filled based on this one. Each field is associated with a suffix to customize secondary field filename. If it contains a dot, it replaces the extension, otherwise it replaces the full filename
     *   
     */
    public FileSelectionPanel(String label, JTextField textField, boolean directoryMode, List<DependentField> dependentFields, String dialogTitle) {
        super();
        init(label, textField, directoryMode, dependentFields, dialogTitle);
    }

    private void init(String label, JTextField textField, boolean directoryMode, List<DependentField> dependentFields, String dialogTitle) {
        this.label = label;
        this.textField = textField;
        this.directoryMode = directoryMode;
        this.dependentFields = dependentFields;
        this.dialogTitle = dialogTitle;

        this.setLayout(new FlowLayout(FlowLayout.RIGHT));

        if (StringUtils.isNotBlank(label)) {
            jlabel = new JLabel(label);
            this.add(jlabel);
        }

        textField.setPreferredSize(new Dimension(400, (int) textField.getPreferredSize().getHeight()));
        this.add(textField);

        button = new JButton("...");
        this.add(button);

        button.addActionListener(this);
        //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {
        final JFileChooser fc = new JFileChooser();

        fc.setDialogTitle(StringUtils.isNotBlank(dialogTitle)?dialogTitle:(StringUtils.isNotBlank(label)?("Select " + label):"Select file"));
        fc.setCurrentDirectory(new java.io.File("."));

        if (directoryMode) {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setAcceptAllFileFilterUsed(false);
        }
        else {
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setAcceptAllFileFilterUsed(true);
        }

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            textField.setText(fc.getSelectedFile().getPath());
            for (DependentField dependentField : dependentFields) {
                if (StringUtils.isBlank(dependentField.field.getText())) {
                    // We have to fill the cascading target based on just made selection and given target
                    String text = textField.getText();
                    if (directoryMode) {
                        dependentField.field.setText(text + File.separatorChar + dependentField.suffix);
                    }
                    else {
                        if (dependentField.suffix.contains(".")) {
                            // replace filename
                            dependentField.field.setText(StringUtils.substringBeforeLast(text, File.separator) + File.separator + dependentField.suffix);
                        }
                        else {
                            // only replace extension
                            dependentField.field.setText(StringUtils.substringBeforeLast(text, ".") + "." + dependentField.suffix);
                        }
                    }
                }
            }
        }
    }

    public class DependentField {
        JTextField field;
        String suffix;

        public DependentField(JTextField field, String suffix) {
            this.field = field;
            this.suffix = suffix;
        }
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }
}
