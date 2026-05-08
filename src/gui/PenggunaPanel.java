// src/gui/PenggunaPanel.java
package gui;

import dao.PenggunaDAO;
import model.Pengguna;
import java.awt.*;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.table.TableRowSorter;
import model.Pengguna;
import gui.ThemeManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class PenggunaPanel extends JPanel {

private static final Color PRIMARY = ThemeManager.getPRIMARY();
// private static final Color BG = DashboardFrame.BG_COLOR; // Now use ThemeManager.getBG_COLOR()
// Removed static CARD - use ThemeManager.getCARD_BG() directly

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtUsername, txtNamaLengkap, txtPassword;
    private JComboBox<String> cmbRole;
    private PenggunaDAO dao = new PenggunaDAO();
    private int selectedId = -1;

private Pengguna currentUser;

    public PenggunaPanel(Pengguna currentUser) {
        this.currentUser = currentUser;
        ThemeManager.addPropertyChangeListener(this::onThemeChanged);

        setLayout(new BorderLayout(0, 0));
setBackground(ThemeManager.getBG_COLOR());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
formPanel.setBackground(ThemeManager.getCARD_BG());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBORDER_COLOR()),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setPreferredSize(new Dimension(500, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;

        JLabel lblForm = new JLabel("Form Pengguna");
        lblForm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblForm.setForeground(ThemeManager.getPRIMARY());

        formPanel.add(lblForm, gbc);

        gbc.gridwidth = 1; gbc.gridy++;
        formPanel.add(createLabel("Username:"), gbc);
        gbc.gridx = 1;
        txtUsername = createTextField();
        formPanel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(createLabel("Password:"), gbc);
        gbc.gridx = 1;
        txtPassword = createTextField();
        formPanel.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(createLabel("Nama Lengkap:"), gbc);
        gbc.gridx = 1;
        txtNamaLengkap = createTextField();
        formPanel.add(txtNamaLengkap, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(createLabel("Role:"), gbc);
        gbc.gridx = 1;
        cmbRole = new JComboBox<>(new String[]{"Admin", "User"});
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbRole.setRenderer(new TransaksiPanel.ComboBoxRenderer());
        formPanel.add(cmbRole, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnPanel.setOpaque(false);

        JButton btnSave = createButton("Simpan", ThemeManager.getPRIMARY());




        JButton btnUpdate = createButton("Update", new Color(52, 152, 219));
        JButton btnDelete = createButton("Hapus", new Color(231, 76, 60));
        JButton btnClear = createButton("Bersihkan", Color.GRAY);

        btnSave.addActionListener(e -> saveData());
        btnUpdate.addActionListener(e -> updateData());
        btnDelete.addActionListener(e -> deleteData());
        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnSave);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        formPanel.add(btnPanel, gbc);

        // Table
        String[] cols = {"ID", "Username", "Nama Lengkap", "Role", "Dibuat"};
tableModel = new DefaultTableModel(cols, 0) {
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
        
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                
                label.setForeground(ThemeManager.getTEXT_PRIMARY());          // warna teks

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
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    selectedId = (int) tableModel.getValueAt(row, 0);
                    txtUsername.setText((String) tableModel.getValueAt(row, 1));
                    txtNamaLengkap.setText((String) tableModel.getValueAt(row, 2));
                    cmbRole.setSelectedItem(tableModel.getValueAt(row, 3));
                    txtPassword.setText("");
                }
            }
        });

        JPanel tablePanel = new JPanel(new BorderLayout());
tablePanel.setBackground(ThemeManager.getCARD_BG());
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBORDER_COLOR()),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        tablePanel.add(new JScrollPane(table));

        add(formPanel, BorderLayout.WEST);
        add(tablePanel, BorderLayout.CENTER);

        loadData();
    }
    
    private void onThemeChanged(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> {
            SwingUtilities.updateComponentTreeUI(this);
            revalidate();
            repaint();
        });
    }


    private void loadData() {
        tableModel.setRowCount(0);
        for (Pengguna p : dao.getAll()) {
            tableModel.addRow(new Object[]{p.getId(), p.getUsername(), p.getNamaLengkap(), p.getRole(), p.getCreatedAt()});
        }
    }

    private void saveData() {
        if (txtUsername.getText().trim().isEmpty() || txtPassword.getText().trim().isEmpty() || txtNamaLengkap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!"); return;
        }
        Pengguna p = new Pengguna();
        p.setUsername(txtUsername.getText().trim());
        p.setPassword(txtPassword.getText().trim());
        p.setNamaLengkap(txtNamaLengkap.getText().trim());
        p.setRole((String) cmbRole.getSelectedItem());
        if (dao.insert(p)) {
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
            loadData(); clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data!");
        }
    }

    private void updateData() {
        if (selectedId == -1) { JOptionPane.showMessageDialog(this, "Pilih data terlebih dahulu!"); return; }
        Pengguna p = new Pengguna();
        p.setId(selectedId);
        p.setUsername(txtUsername.getText().trim());
        p.setPassword(txtPassword.getText().trim().isEmpty() ? "unchanged" : txtPassword.getText().trim());
        p.setNamaLengkap(txtNamaLengkap.getText().trim());
        p.setRole((String) cmbRole.getSelectedItem());
        if (dao.update(p)) {
            JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
            loadData(); clearForm();
        }
    }

    private void deleteData() {
        if (selectedId == -1) { JOptionPane.showMessageDialog(this, "Pilih data terlebih dahulu!"); return; }
        if (!"Admin".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "Akses ditolak! Hanya Admin yang dapat menghapus pengguna.");
            return;
        }
        int opt = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            if (dao.delete(selectedId)) {
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                loadData(); clearForm();
            }
        }
    }

    private void clearForm() {
        txtUsername.setText(""); txtPassword.setText(""); txtNamaLengkap.setText("");
        cmbRole.setSelectedIndex(0); selectedId = -1; table.clearSelection();
    }

    static JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(ThemeManager.getTEXT_SECONDARY());
        return lbl;
    }


    static JTextField createTextField() {
        JTextField tf = new JTextField(15);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBORDER_COLOR()),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        tf.setBackground(ThemeManager.getCARD_BG());
        tf.setForeground(ThemeManager.getTEXT_PRIMARY());
        return tf;
    }


    static JButton createButton(String text, Color bg) {
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

    static void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setGridColor(ThemeManager.getBORDER_COLOR());
        table.setSelectionBackground(new Color(ThemeManager.getSIDEBAR_ACTIVE().getRed(), ThemeManager.getSIDEBAR_ACTIVE().getGreen(), ThemeManager.getSIDEBAR_ACTIVE().getBlue(), 64));
        table.setSelectionForeground(ThemeManager.getTEXT_PRIMARY());
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(ThemeManager.getSIDEBAR_BG());
        table.getTableHeader().setForeground(ThemeManager.getTEXT_PRIMARY());
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
    }

}
