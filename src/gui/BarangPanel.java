// src/gui/BarangPanel.java
package gui;

import dao.KategoriBarangDAO;
import dao.ProdukDAO;
import static gui.PenggunaPanel.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.Barang;
import model.KategoriBarang;
import model.Pengguna;

public class BarangPanel extends JPanel {

private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNama, txtHarga, txtDeskripsi;
    private JComboBox<KategoriBarang> cmbKategori;
    private ProdukDAO dao = new ProdukDAO();
    private int selectedId = -1;
    private Pengguna currentUser;
    private java.util.List<Barang> allBarang;
    private DecimalFormat currencyFormat = new DecimalFormat("Rp #,##0");

    public BarangPanel(Pengguna currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(0, 0));
        setBackground(ThemeManager.getBG_COLOR());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ThemeManager.addPropertyChangeListener(this::onThemeChanged);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ThemeManager.getCARD_BG());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBORDER_COLOR()),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        formPanel.setPreferredSize(new Dimension(500, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;

        JLabel lbl = new JLabel("Form Barang");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(ThemeManager.getPRIMARY());
        formPanel.add(lbl, gbc);

        gbc.gridwidth = 1; gbc.gridy++;

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(createLabel("Nama:"), gbc); gbc.gridx = 1;
        txtNama = createTextField(); formPanel.add(txtNama, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(createLabel("Harga:"), gbc); gbc.gridx = 1;
        txtHarga = createTextField(); formPanel.add(txtHarga, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(createLabel("Kategori:"), gbc); gbc.gridx = 1;
        cmbKategori = new JComboBox<>();
        cmbKategori.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbKategori.setRenderer(new TransaksiPanel.ComboBoxRenderer());
        loadKategori();
        formPanel.add(cmbKategori, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(createLabel("Deskripsi:"), gbc); gbc.gridx = 1;
        txtDeskripsi = createTextField(); formPanel.add(txtDeskripsi, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        bp.setOpaque(false);

        JButton btnSave = createButton("Simpan", DashboardFrame.PRIMARY); // ThemeManager.getPRIMARY()
        JButton btnUpdate = createButton("Update", new Color(52, 152, 219));
        JButton btnDelete = createButton("Hapus", new Color(231, 76, 60));
        JButton btnClear = createButton("Bersihkan", Color.GRAY);

        btnSave.addActionListener(e -> save());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnClear.addActionListener(e -> clear());

        bp.add(btnSave); bp.add(btnUpdate); bp.add(btnDelete); bp.add(btnClear);
        formPanel.add(bp, gbc);

tableModel = new DefaultTableModel(new String[]{"ID", "Kode", "Nama", "Harga", "Kategori", "Deskripsi"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        // Sembunyikan kolom ID (index 0)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Terapkan ke semua kolom
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Custom renderer for column 3 (Harga) - display as "Rp X"
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Number) {
                    value = currencyFormat.format(((Number) value).doubleValue());
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        currencyRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);

        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setForeground(ThemeManager.getTABLE_TEXT());          // warna teks
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 1, ThemeManager.getTABLE_HEADER_BORDER())); 
                label.setOpaque(true);

                return label;
            }
        });
        
        styleTable(table);
        table.setRowSorter(new TableRowSorter<>(tableModel));
        ((TableRowSorter)table.getRowSorter()).setComparator(3, new NumberComparator()); // Harga column
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    selectedId = (int) tableModel.getValueAt(row, 0);
                    txtNama.setText((String) tableModel.getValueAt(row, 1));
                    txtHarga.setText(String.valueOf(tableModel.getValueAt(row, 2)));
                    String kat = (String) tableModel.getValueAt(row, 3);
                    for (int i = 0; i < cmbKategori.getItemCount(); i++) {
                        if (cmbKategori.getItemAt(i).toString().equals(kat)) {
                            cmbKategori.setSelectedIndex(i); break;
                        }
                    }
                    txtDeskripsi.setText((String) tableModel.getValueAt(row, 5));
                }
            }
        });

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        JLabel lblSearch = createLabel("Cari: ");
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTable(searchField.getText().toLowerCase());
            }
        });
        JButton btnClearSearch = createButton("Clear", Color.GRAY);
        btnClearSearch.addActionListener(e -> {
            searchField.setText("");
            filterTable("");
        });
        searchPanel.add(lblSearch);
        searchPanel.add(searchField);
        searchPanel.add(btnClearSearch);

        JPanel tp = new JPanel(new BorderLayout());
        tp.setBackground(ThemeManager.getCARD_BG());
        tp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBORDER_COLOR()),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        tp.add(searchPanel, BorderLayout.NORTH);
        tp.add(new JScrollPane(table), BorderLayout.CENTER);

        add(formPanel, BorderLayout.WEST);
        add(tp, BorderLayout.CENTER);
        load();
    }

    private void loadKategori() {
        cmbKategori.removeAllItems();
        for (KategoriBarang k : new KategoriBarangDAO().getAll()) {
            cmbKategori.addItem(k);
        }
    }

    private void load() {
        allBarang = dao.getAllBarang();
        tableModel.setRowCount(0);
        for (Barang b : allBarang) {
            tableModel.addRow(new Object[]{b.getId(), b.getKode(), b.getNama(), b.getHarga(), b.getNamaKategori(), b.getDeskripsi()});
        }
        filterTable("");
    }

    private void filterTable(String query) {
        tableModel.setRowCount(0);
        for (Barang b : allBarang) {
            if (b.getKode().toLowerCase().contains(query) ||
                b.getNama().toLowerCase().contains(query) ||
                b.getNamaKategori().toLowerCase().contains(query) ||
                (b.getDeskripsi() != null && b.getDeskripsi().toLowerCase().contains(query))) {
                tableModel.addRow(new Object[]{b.getId(), b.getKode(), b.getNama(), b.getHarga(), b.getNamaKategori(), b.getDeskripsi()});
            }
        }
    }

private void save() {
        // Auto-generate kode if empty (new entry)
        if (txtNama.getText().trim().isEmpty() || txtHarga.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan Harga harus diisi!"); return;
        }
        try {
            Barang b = new Barang();
            b.setNama(txtNama.getText().trim());
            b.setHarga(Double.parseDouble(txtHarga.getText().trim()));
            b.setKategoriId(((KategoriBarang) cmbKategori.getSelectedItem()).getId());
            b.setDeskripsi(txtDeskripsi.getText().trim());
            if (dao.insertBarang(b)) { JOptionPane.showMessageDialog(this, "Berhasil!"); load(); clear(); }
            else JOptionPane.showMessageDialog(this, "Gagal!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka!");
        }
    }

    private void update() {
        if (selectedId == -1) { JOptionPane.showMessageDialog(this, "Pilih data!"); return; }
        try {
            Barang b = new Barang();
            b.setId(selectedId);
            b.setNama(txtNama.getText().trim());
            b.setHarga(Double.parseDouble(txtHarga.getText().trim()));
            b.setKategoriId(((KategoriBarang) cmbKategori.getSelectedItem()).getId());
            b.setDeskripsi(txtDeskripsi.getText().trim());
            if (dao.updateBarang(b)) { JOptionPane.showMessageDialog(this, "Berhasil!"); load(); clear(); }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka!");
        }
    }

    private void delete() {
        if (selectedId == -1) { JOptionPane.showMessageDialog(this, "Pilih data!"); return; }
        if (!"Admin".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "Akses ditolak! Hanya Admin yang dapat menghapus data.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Yakin?") == JOptionPane.YES_OPTION) {
            if (dao.deleteBarang(selectedId)) { JOptionPane.showMessageDialog(this, "Berhasil!"); load(); clear(); }
        }
    }

private void clear() {
        // Auto-generate new kode when clearing form for new entry
        txtNama.setText(""); txtHarga.setText(""); txtDeskripsi.setText("");
        if (cmbKategori.getItemCount() > 0) cmbKategori.setSelectedIndex(0);
        selectedId = -1; table.clearSelection();
    }
    
    private void onThemeChanged(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> {
            setBackground(ThemeManager.getBG_COLOR());
            revalidate();
            repaint();
        });
    }
}
