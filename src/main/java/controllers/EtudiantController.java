package controllers;
import models.Data;
import models.Etudiant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import java.awt.Color;

public class EtudiantController {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String BASE_URL = "http://localhost:8080/api/etudiants"; // Modifie si nécessaire
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel min_moye;
    private JLabel max_moye;
    private JPanel chart_panel;

    public EtudiantController(
            JTable table,
            DefaultTableModel tableModel,
            JLabel min_moye,
            JLabel max_moye,
            JPanel ChartPanel
        ) {

        this.table = table;
        this.tableModel = tableModel;
        this.min_moye = min_moye;
        this.max_moye = max_moye;
        this.chart_panel = ChartPanel;

        loadEtudiantsIntoTable();
    }

    public static class MoyenneResponse {
        public float moyenneMinimale;
        public float moyenneMaximale;
        public Integer code;
        // Ajoute un constructeur par défaut si nécessaire
        public MoyenneResponse() {}
    }

    public void loadEtudiantsIntoTable() {
//        Format de reponse attendue
//        [
//            {
//                "numero": "ET001",
//                    "nom": "Rakoto",
//                    "moyenne": 12.5
//            },
//            {
//                "numero": "ET002",
//                    "nom": "Rabe",
//                    "moyenne": 8.0
//            },
//            {
//                "numero": "ET003",
//                    "nom": "Randria",
//                    "moyenne": 4.2
//            }
//        ]

        try {
            table.removeAll();
            System.out.println("Voici l'url de base : " + this.BASE_URL);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            Data data = mapper.readValue(response.body(), new TypeReference<>() {});

            DefaultTableModel model = new DefaultTableModel(new String[]{"Numéro", "Nom", "Moyenne", "Observation"}, 0);
            for (Etudiant e : data.getEtudiants()) {
                model.addRow(new Object[]{e.getNumEt(), e.getNom(), e.getMoyenne(), e.getObservation()});
            }
            table.setModel(model);
            loadMoyenne(min_moye, max_moye);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Inside error stack : ");
            System.out.println(e);
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Erreur de chargement : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadMoyenne(JLabel min_moye, JLabel max_moye) {
        // Format de réponse attendue :
        // {
        //     "min": 0.0,
        //     "max": 0.0
        // }

        try {
            min_moye.setText("0");
            max_moye.setText("0");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/moyenneClasse"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());

            MoyenneResponse moyenneResponse = mapper.readValue(response.body(), MoyenneResponse.class);

            min_moye.setText(String.valueOf(moyenneResponse.moyenneMinimale));
            max_moye.setText(String.valueOf(moyenneResponse.moyenneMaximale));

            // Charger l'histogramme
            addHistogramToPanel(moyenneResponse.moyenneMinimale, moyenneResponse.moyenneMaximale);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Inside error stack : ");
            System.out.println(e);
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Erreur de chargement : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void addEtudiant(Etudiant etudiant) {
        try {
            String json = mapper.writeValueAsString(etudiant);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            loadEtudiantsIntoTable();
            JOptionPane.showMessageDialog(null, "Étudiant ajouté avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur ajout : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateEtudiant(Etudiant etudiant) {
        try {
            String json = mapper.writeValueAsString(etudiant);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/"+ etudiant.getNumEt() ))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            loadEtudiantsIntoTable();
            JOptionPane.showMessageDialog(null, "Étudiant mis à jour", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur update : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteEtudiant(String numero) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/"+ numero ))
                    .DELETE()
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            loadEtudiantsIntoTable();
            JOptionPane.showMessageDialog(null, "Étudiant supprimé", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur suppression : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ajoute un histogramme dans un JPanel existant à partir des valeurs min et max.
     * @param min la moyenne minimale
     * @param max la moyenne maximale
     */
    public void addHistogramToPanel(float min, float max) {
        // Nettoyer le panel
        chart_panel.removeAll();
        chart_panel.setLayout(new BorderLayout());

        // Créer le dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(min, "Moyenne", "Minimale");
        dataset.addValue(max, "Moyenne", "Maximale");

        // Créer le graphique
        JFreeChart chart = ChartFactory.createBarChart(
                "Moyenne des étudiants", // Titre
                "Type",                 // Axe X
                "Valeur",               // Axe Y
                dataset
        );


        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        renderer.setSeriesPaint(0, new Color(0, 128, 255)); // Bleu
        renderer.setMaximumBarWidth(0.1); // Valeur entre 0.0 (très fin) et 1.0 (épaisseur max)

        // Créer et ajouter le ChartPanel
        ChartPanel chartPanel = new ChartPanel(chart);
        chart_panel.add(chartPanel, BorderLayout.CENTER);
        chart_panel.validate();
    }
}
