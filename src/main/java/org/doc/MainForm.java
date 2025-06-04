package org.doc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import controllers.EtudiantController;
import models.Etudiant;

public class MainForm extends JFrame{
    private JPanel mainContainerPanel;
    private JTabbedPane tabbedPane1;
    private JPanel etudiantTab;
    private JPanel patientTab;

    private JTable etudiantTable;
    private JTextField inputNom;
    private JTextField inputMoyenne;


    private JLabel indexEtudiant;

    private JButton submitEtudiant;
    private JButton annulerEtudiantSubmit;
    private JButton rafraichirEtudiantButton;
    private JButton annulerEtudiantActionButton;
    private JButton updateEtudiant;
    private JButton deleteEtudiant;

    private JLabel min_moy;
    private JLabel max_moye;
    private JPanel chart_pannel;

    private EtudiantController etudiantController;

    // Global vars
    private Boolean IS_CREATE = true;
    private Integer GLOBAL_INDEX = -1;


    public MainForm () {
        DefaultTableModel etudiantTableModel = new DefaultTableModel();
        etudiantController = new EtudiantController(etudiantTable, etudiantTableModel, min_moy, max_moye, chart_pannel);
        setContentPane(mainContainerPanel);
        setTitle("Gestion des notes des etudiants.");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(1200, 720);

        indexEtudiant.setText("Aucun");


        //*****************************
        //  LISTENER
        //****************************
        etudiantTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int selectedRow = etudiantTable.getSelectedRow();
                    if (selectedRow != -1) {
                        GLOBAL_INDEX = selectedRow;
                        indexEtudiant.setText(etudiantTable.getValueAt (selectedRow,0).toString());
                        updateEtudiant.setEnabled(true);
                        deleteEtudiant.setEnabled(true);
                    } else {
                        indexEtudiant.setText("Aucun");
                    }
                }
            }
        });


        //*****************************
        //  BUTTON ACTIONS
        //****************************
        submitEtudiant.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                try {
                    String nom = inputNom.getText();
                    String moyenneStr = inputMoyenne.getText();
                    float moyenne = Float.parseFloat(moyenneStr);

                    if (moyenne < 0 || moyenne > 20) {
                        JOptionPane.showMessageDialog(null, "La moyenne doit être comprise entre 0 et 20.", "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                        inputMoyenne.setText("0");
                        return;
                    } else if (nom.equals("")) {
                        JOptionPane.showMessageDialog(null, "Veuillez insérer votre  nom ", "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // moyenne est un float
                    if (IS_CREATE ) {
                        Etudiant etudiant = new Etudiant(null, nom, moyenne);
                        etudiantController.addEtudiant(etudiant);
                    } else {
                        Integer matricule = Integer.parseInt(etudiantTable.getValueAt(GLOBAL_INDEX, 0).toString());
                        Etudiant etudiant = new Etudiant(matricule, nom, moyenne);
                        etudiantController.updateEtudiant(etudiant);
                    }
                    clearInputs();
                    etudiantController.loadEtudiantsIntoTable();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Veuillez entrer une moyenne valide (nombre décimal).", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    inputMoyenne.setText("0");
                }
            }
        });

        annulerEtudiantSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                clearInputs();
            }
        });

        rafraichirEtudiantButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                etudiantController.loadEtudiantsIntoTable();
            }
        });

        annulerEtudiantActionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                clearActions();
                clearInputs();
            }
        });

        updateEtudiant.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                IS_CREATE = false;
                inputNom.setText(etudiantTable.getValueAt(GLOBAL_INDEX, 1).toString());
                inputMoyenne.setText(etudiantTable.getValueAt(GLOBAL_INDEX, 2).toString());
            }
        });

        deleteEtudiant.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                IS_CREATE = true;
                String matricule = etudiantTable.getValueAt(GLOBAL_INDEX, 0).toString();
                // Create the confirmation dialog
                int response = JOptionPane.showConfirmDialog(null,
                        "Êtes-vous sûr de vouloir supprimer la note de l'etudiant avec le numero " + matricule + "?",
                        "Confirmation de suppression",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                // If the user clicks "Yes", proceed with the deletion
                if (response == JOptionPane.YES_OPTION) {
                    etudiantController.deleteEtudiant(matricule);
                    clearActions();
                } else {
                    clearActions();
                }
            }
        });

        //*****************************
        //  SET THE UI VISIBLE
        //****************************
        setVisible(true);
    }

    private void clearInputs () {
        IS_CREATE = true;
        inputNom.setText("");
        inputMoyenne.setText("");

    }

    private void clearActions(){
        GLOBAL_INDEX = -1;
        indexEtudiant.setText("Aucun");
        updateEtudiant.setEnabled(false);
        deleteEtudiant.setEnabled(false);
    }
}