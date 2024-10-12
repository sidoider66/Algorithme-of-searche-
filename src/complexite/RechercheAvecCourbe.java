package complexite;

import javax.swing.*;

import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RechercheAvecCourbe extends JFrame {

    public RechercheAvecCourbe(String titre) {
        super(titre);
        setLayout(new BorderLayout());
    }

    // Méthode pour créer le graphique et la table
    public void creerGraphiqueEtTable(List<Integer> tailles, List<Double> tempsSequentiel,
                                      List<Double> tempsDichotomiqueIterative, List<Double> tempsDichotomiqueRecursive) {
        // Créer le graphique
        XYSeries serieSeq = new XYSeries("Recherche Séquentielle");
        XYSeries serieDichoIter = new XYSeries("Recherche Dichotomique Itérative");
        XYSeries serieDichoRec = new XYSeries("Recherche Dichotomique Récursive");

        for (int i = 0; i < tailles.size(); i++) {
            serieSeq.add(tailles.get(i), tempsSequentiel.get(i));
            serieDichoIter.add(tailles.get(i), tempsDichotomiqueIterative.get(i));
            serieDichoRec.add(tailles.get(i), tempsDichotomiqueRecursive.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serieSeq);
        dataset.addSeries(serieDichoIter);
        dataset.addSeries(serieDichoRec);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Comparaison des Algorithmes de Recherche",
                "Taille du Tableau",
                "Temps (secondes)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 400));
        add(chartPanel, BorderLayout.CENTER);

        // Créer le tableau
        JTable table = creerTableResultats(tailles, tempsSequentiel, tempsDichotomiqueIterative, tempsDichotomiqueRecursive);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.SOUTH);

        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Créer le tableau des résultats
    private JTable creerTableResultats(List<Integer> tailles, List<Double> tempsSeq, 
                                       List<Double> tempsDichoIter, List<Double> tempsDichoRec) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Taille du Tableau");
        model.addColumn("Temps Séquentiel (s)");
        model.addColumn("Temps Dichotomique Itératif (s)");
        model.addColumn("Temps Dichotomique Récursif (s)");

        for (int i = 0; i < tailles.size(); i++) {
            model.addRow(new Object[]{
                tailles.get(i), tempsSeq.get(i), tempsDichoIter.get(i), tempsDichoRec.get(i)
            });
        }

        return new JTable(model);
    }

    public static void main(String[] args) {
        Random random = new Random();
        List<Integer> tailles = List.of(1000,2000,3000, 4000,5000,  6000,7000, 8000,9000, 10000); // Sizes of the array
        List<Double> tempsSequentiel = new ArrayList<>();
        List<Double> tempsDichotomiqueIterative = new ArrayList<>();
        List<Double> tempsDichotomiqueRecursive = new ArrayList<>();
        int element = 5000;  // Element to search

        // Measure the time for each algorithm for different sizes of array
        for (int taille : tailles) {
            List<Double> seqTemps = new ArrayList<>();
            List<Double> dichoIterTemps = new ArrayList<>();
            List<Double> dichoRecTemps = new ArrayList<>();

            // Execute each search three times
            for (int j = 0; j < 3; j++) {
                List<Integer> tableau = genererTableauAleatoire(taille, random);
                Collections.sort(tableau);  // Sort for binary search

                seqTemps.add(mesureTempsRecherche(RechercheAvecCourbe::rechercheSequentielle, tableau, element));
                dichoIterTemps.add(mesureTempsRecherche(RechercheAvecCourbe::rechercheDichotomiqueIterative, tableau, element));
                dichoRecTemps.add(mesureTempsRecherche((tab, elem) -> rechercheDichotomiqueRecursive(tab, elem, 0, tab.size() - 1), tableau, element));
            }

            // Calculate average time for each algorithm
            tempsSequentiel.add(moyenne(seqTemps));
            tempsDichotomiqueIterative.add(moyenne(dichoIterTemps));
            tempsDichotomiqueRecursive.add(moyenne(dichoRecTemps));
        }

        // Create and display the chart and table
        new RechercheAvecCourbe("Comparaison des Algorithmes de Recherche")
            .creerGraphiqueEtTable(tailles, tempsSequentiel, tempsDichotomiqueIterative, tempsDichotomiqueRecursive);
    }

    // Generate a random array
    private static List<Integer> genererTableauAleatoire(int taille, Random random) {
        List<Integer> tableau = new ArrayList<>();
        for (int i = 0; i < taille; i++) {
            tableau.add(random.nextInt(taille * 10));  // Random numbers up to taille * 10
        }
        return tableau;
    }

    // Measure execution time of the search algorithms
    public static double mesureTempsRecherche(TempsInterface algo, List<Integer> tableau, int element) {
        long startTime = System.nanoTime();
        algo.recherche(tableau, element);
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000_000.0;  // Convert to seconds
    }

    // Calculate the average of a list of times
    private static double moyenne(List<Double> temps) {
        double sum = 0.0;
        for (double t : temps) {
            sum += t;
        }
        return sum / temps.size();
    }

    // Sequential search
    public static int rechercheSequentielle(List<Integer> liste, int element) {
        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i) == element) {
                return i;
            }
        }
        return -1;
    }

    // Iterative binary search
    public static int rechercheDichotomiqueIterative(List<Integer> liste, int element) {
        int debut = 0;
        int fin = liste.size() - 1;

        while (debut <= fin) {
            int milieu = (debut + fin) / 2;
            if (liste.get(milieu) == element) {
                return milieu;
            } else if (liste.get(milieu) < element) {
                debut = milieu + 1;
            } else {
                fin = milieu - 1;
            }
        }
        return -1;
    }

    // Recursive binary search
    public static int rechercheDichotomiqueRecursive(List<Integer> liste, int element, int debut, int fin) {
        if (debut > fin) {
            return -1;
        }

        int milieu = (debut + fin) / 2;
        if (liste.get(milieu) == element) {
            return milieu;
        } else if (liste.get(milieu) < element) {
            return rechercheDichotomiqueRecursive(liste, element, milieu + 1, fin);
        } else {
            return rechercheDichotomiqueRecursive(liste, element, debut, milieu - 1);
        }
    }

    // Functional interface for search
    @FunctionalInterface
    interface TempsInterface {
        int recherche(List<Integer> tableau, int element);
    }
}
