// src/gui/StatistikPanel.java
package gui;

import dao.LaporanDAO;
import gui.PenggunaPanel;

import java.awt.event.*;


import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Map;
import javax.swing.*;

public class StatistikPanel extends JPanel {

    private static JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(ThemeManager.getTEXT_SECONDARY());
        return lbl;
    }

    private static JTextField createTextField() {
        JTextField tf = new JTextField(15);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBORDER_COLOR()),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        tf.setBackground(ThemeManager.getCARD_BG());
        tf.setForeground(ThemeManager.getTEXT_PRIMARY());
        return tf;
    }

    private static JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 32));
        return btn;
    }

    private JTextField txtTahun;
    private JComboBox<String> cmbTipe;
    private JComboBox<String> cmbMode;
    private JPanel chartPanel;
    private LaporanDAO dao = new LaporanDAO();
    private DecimalFormat df = new DecimalFormat("#,##0");
    private Map<String, Double> currentData;


    public StatistikPanel() {
        setLayout(new BorderLayout(15, 15));
setBackground(ThemeManager.getBG_COLOR());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
filterPanel.setBackground(ThemeManager.getCARD_BG());
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        filterPanel.add(createLabel("Tahun:"));
        txtTahun = createTextField();
        txtTahun.setColumns(6);
        txtTahun.setText(String.valueOf(LocalDate.now().getYear()));
        filterPanel.add(txtTahun);

        filterPanel.add(createLabel("Tipe:"));
        cmbTipe = new JComboBox<>(new String[]{"Semua", "Campuran", "Barang", "Jasa"});
        cmbTipe.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbTipe.setRenderer(new TransaksiPanel.ComboBoxRenderer());
        filterPanel.add(cmbTipe);

        // Mode pilih (dropdown)
        filterPanel.add(createLabel("Mode:"));
        cmbMode = new JComboBox<>(new String[]{"Bulanan", "Tahunan (12 tahun)"});
        cmbMode.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbMode.setRenderer(new TransaksiPanel.ComboBoxRenderer());
        filterPanel.add(cmbMode);

        JButton btnGenerate = createButton("Generate", DashboardFrame.PRIMARY);
        btnGenerate.addActionListener(e -> generateChart());
        filterPanel.add(btnGenerate);



        // Chart panel
        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (currentData != null) {
                    drawChart((Graphics2D) g);
                }
            }
        };
chartPanel.setBackground(ThemeManager.getCARD_BG());
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        add(filterPanel, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);

        generateChart();

    }


    private void generateChart() {
        try {
            int tahun = Integer.parseInt(txtTahun.getText().trim());
            String tipe = (String) cmbTipe.getSelectedItem();
            String mode = (String) cmbMode.getSelectedItem();

            boolean tahunan = mode != null && mode.startsWith("Tahunan");

            if (tahunan) {
                currentData = dao.getStatistikTahunan(tahun, tipe);
            } else {
                currentData = dao.getStatistikBulanan(tahun, tipe);
            }
            chartPanel.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Tahun harus berupa angka!");
        }
    }




    private boolean lastTahunan = false;


    private void drawChart(Graphics2D g2d) {

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = chartPanel.getWidth();
        int h = chartPanel.getHeight();
        int padding = 80;
        int chartW = w - padding * 2;
        int chartH = h - padding * 2 - 40;

        // Title
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2d.setColor(DashboardFrame.PRIMARY);

        // Mode ditentukan oleh tombol terakhir (hardcode title sesuai isi chart)
        // Jika ingin benar-benar dinamis, simpan flag tahunan ke field.
        // Untuk saat ini, pastikan generateChart dipanggil sesuai tombol.
        String mode = (currentData != null && !currentData.containsKey("Jan") ? "Tahunan" : "Bulanan");


        String title;
        if (mode != null && mode.startsWith("Tahunan")) {
            title = "Statistik Pendapatan (12 Tahun) hingga " + txtTahun.getText() + " - " + cmbTipe.getSelectedItem();
        } else {
            title = "Statistik Pendapatan Tahun " + txtTahun.getText() + " - " + cmbTipe.getSelectedItem();
        }

        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, (w - fm.stringWidth(title)) / 2, 35);

        // Find max value
        double maxVal = 1;
        for (Double val : currentData.values()) {
            if (val > maxVal) maxVal = val;
        }

        // Draw axes
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawLine(padding, padding, padding, padding + chartH);
        g2d.drawLine(padding, padding + chartH, padding + chartW, padding + chartH);

        // Draw grid lines and Y labels
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2d.setColor(new Color(220, 220, 220));
        for (int i = 0; i <= 5; i++) {
            int y = padding + chartH - (chartH * i / 5);
            g2d.drawLine(padding, y, padding + chartW, y);
            g2d.setColor(new Color(120, 120, 120));
            String label = "Rp " + df.format(maxVal * i / 5);
            g2d.drawString(label, 5, y + 4);
            g2d.setColor(new Color(220, 220, 220));
        }

        // Draw bars
        int barCount = currentData.size();
        int barWidth = Math.max(20, (chartW - 20) / barCount - 8);
        int x = padding + 10;

        int idx = 0;
        for (Map.Entry<String, Double> entry : currentData.entrySet()) {
            double val = entry.getValue();
            int barH = (int) ((val / maxVal) * chartH);
            if (barH < 1 && val > 0) barH = 3;

            // Gradient bar
            Color barColor = new Color(126, 4, 3);
            Color barLight = new Color(180, 40, 40);
            GradientPaint gp = new GradientPaint(x, padding + chartH - barH, barLight, x, padding + chartH, barColor);
            g2d.setPaint(gp);
            g2d.fillRoundRect(x, padding + chartH - barH, barWidth, barH, 5, 5);

            // Bar border
            g2d.setColor(new Color(100, 3, 2));
            g2d.drawRoundRect(x, padding + chartH - barH, barWidth, barH, 5, 5);

            // Month label
            g2d.setColor(new Color(80, 80, 80));
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
            fm = g2d.getFontMetrics();
            int labelX = x + (barWidth - fm.stringWidth(entry.getKey())) / 2;
            g2d.drawString(entry.getKey(), labelX, padding + chartH + 18);

            // Value label on top
            if (val > 0) {
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                String valStr = df.format(val);
                fm = g2d.getFontMetrics();
                int valX = x + (barWidth - fm.stringWidth(valStr)) / 2;
                g2d.setColor(DashboardFrame.PRIMARY);
                g2d.drawString(valStr, valX, padding + chartH - barH - 5);
            }

            x += barWidth + 8;
            idx++;
        }

        // Total info
        double total = 0;
        for (Double v : currentData.values()) total += v;
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2d.setColor(DashboardFrame.PRIMARY);
        String totalStr = "Total Pendapatan: Rp " + df.format(total);
        fm = g2d.getFontMetrics();
        g2d.drawString(totalStr, (w - fm.stringWidth(totalStr)) / 2, h - 15);
    }
}