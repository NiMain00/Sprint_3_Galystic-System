package gui;

import dao.ProdukDAO;
import static gui.PenggunaPanel.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.Jasa;
import model.Pengguna;

public class JasaPanel extends JPanel {

private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtKode, txtNama, txtHarga, txtDeskripsi;
    private ProdukDAO dao = new ProdukDAO();
    private int selectedId = -1;
    private Pengguna currentUser;
    private List<Jasa> allJasa;
    private DecimalFormat currencyFormat = new DecimalFormat("Rp #,##0");

    public JasaPanel(Pengguna currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(0, 0));
        setBackground(ThemeManager.getBG_COLOR());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

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

        JLabel lbl = new JLabel("Form Jasa");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(ThemeManager.getPRIMARY());
        formPanel.add(lbl, gbc);



        // Nama
gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 1;
formPanel.add(createLabel("Nama:"), gbc);
gbc.gridx = 1;
txtNama = createTextField();
formPanel.add(txtNama, gbc);

// Harga
gbc.gridx = 0; gbc.gridy++;
formPanel.add(createLabel("Harga:"), gbc);
gbc.gridx = 1;
txtHarga = createTextField();
formPanel.add(txtHarga, gbc);

// Deskripsi
gbc.gridx = 0; gbc.gridy++;
formPanel.add(createLabel("Deskripsi:"), gbc);
gbc.gridx = 1;
txtDeskripsi = createTextField();
formPanel.add(txtDeskripsi, gbc);


        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        bp.setOpaque(false);

        JButton btnSave = createButton("Simpan", ThemeManager.getPRIMARY());
        JButton btnUpdate = createButton("Update", new Color(52, 152, 219));
        JButton btnDelete = createButton("Hapus", new Color(231, 76, 60));
        JButton btnClear = createButton("Bersihkan", Color.GRAY);

        btnSave.addActionListener(e -> save());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnClear.addActionListener(e -> clear());

        bp.add(btnSave); bp.add(btnUpdate); bp.add(btnDelete); bp.add(btnClear);
        formPanel.add(bp, gbc);

tableModel = new DefaultTableModel(new String[]{"ID", "Kode", "Nama", "Harga", "Deskripsi"}, 0) {
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

        label.setForeground(ThemeManager.getTEXT_PRIMARY());
        label.setBackground(ThemeManager.getCARD_BG()); // 🔥 WAJIB
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 1, ThemeManager.getBORDER_COLOR()));
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
                    txtKode.setText((String) tableModel.getValueAt(row, 1));
                    txtNama.setText((String) tableModel.getValueAt(row, 2));
                    txtHarga.setText(String.valueOf(tableModel.getValueAt(row, 3)));
                    txtDeskripsi.setText((String) tableModel.getValueAt(row, 4));
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
                JasaPanel.this.filterTable(searchField.getText().toLowerCase());
            }
        });
        JButton btnClearSearch = createButton("Clear", Color.GRAY);
        btnClearSearch.addActionListener(e -> {
            searchField.setText("");
            JasaPanel.this.filterTable("");
        });
        searchPanel.add(lblSearch);
        searchPanel.add(searchField);
        searchPanel.add(btnClearSearch);

        JPanel tp = new JPanel(new BorderLayout());
        tp.setBackground(ThemeManager.getCARD_BG());
        tp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        tp.add(searchPanel, BorderLayout.NORTH);
        tp.add(new JScrollPane(table), BorderLayout.CENTER);

        add(formPanel, BorderLayout.WEST);
        add(tp, BorderLayout.CENTER);
        load();
    }

    private void load() {
        allJasa = dao.getAllJasa();
        tableModel.setRowCount(0);
        for (Jasa j : allJasa) {
            tableModel.addRow(new Object[]{j.getId(), j.getKode(), j.getNama(), j.getHarga(), j.getDeskripsi()});
        }
        filterTable("");
    }

    private void filterTable(String query) {
        tableModel.setRowCount(0);
        for (Jasa j : allJasa) {
            if (j.getNama().toLowerCase().contains(query) ||
                j.getKode().toLowerCase().contains(query) ||
                (j.getDeskripsi() != null && j.getDeskripsi().toLowerCase().contains(query))) {
                tableModel.addRow(new Object[]{j.getId(), j.getKode(), j.getNama(), j.getHarga(), j.getDeskripsi()});
            }
        }
    }

private void save() {
        // Auto-generate kode if empty (new entry)
        if (txtKode.getText().trim().isEmpty()) {
            txtKode.setText(dao.generateNextKodeJasa());
        }
        if (txtNama.getText().trim().isEmpty() || txtHarga.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama, Harga harus diisi!"); return;
        }
        try {
            Jasa j = new Jasa();
            j.setKode(txtKode.getText().trim());
            j.setNama(txtNama.getText().trim());
            j.setHarga(Double.parseDouble(txtHarga.getText().trim()));
            j.setDeskripsi(txtDeskripsi.getText().trim());
            if (dao.insertJasa(j)) { JOptionPane.showMessageDialog(this, "Berhasil!"); load(); clear(); }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga harus angka!");
        }
    }

    private void update() {
        if (selectedId == -1) { JOptionPane.showMessageDialog(this, "Pilih data!"); return; }
        try {
            Jasa j = new Jasa();
            j.setId(selectedId);
            j.setKode(txtKode.getText().trim());
            j.setNama(txtNama.getText().trim());
            j.setHarga(Double.parseDouble(txtHarga.getText().trim()));
            j.setDeskripsi(txtDeskripsi.getText().trim());
            if (dao.updateJasa(j)) { JOptionPane.showMessageDialog(this, "Berhasil!"); load(); clear(); }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga harus angka!");
        }
    }

    private void delete() {
        if (selectedId == -1) { JOptionPane.showMessageDialog(this, "Pilih data!"); return; }
        if (!"Admin".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "Akses ditolak! Hanya Admin yang dapat menghapus data.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Yakin?") == JOptionPane.YES_OPTION) {
            if (dao.deleteJasa(selectedId)) { JOptionPane.showMessageDialog(this, "Berhasil!"); load(); clear(); }
        }
    }

private void clear() {
        // Auto-generate new kode when clearing form for new entry
        txtKode.setText(dao.generateNextKodeJasa());
        txtNama.setText(""); txtHarga.setText(""); txtDeskripsi.setText("");
        selectedId = -1; table.clearSelection();
    }
}

