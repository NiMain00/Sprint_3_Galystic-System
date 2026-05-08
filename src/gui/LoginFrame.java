// src/gui/LoginFrame.java
package gui;

import dao.PenggunaDAO;
import util.LoginAttemptTracker;
import util.AuditLogger;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingWorker;
import model.Pengguna;

public class LoginFrame extends JFrame {

    private static final Color PRIMARY = new Color(126, 4, 3);
    private static final Color PRIMARY_DARK = new Color(90, 3, 2);
    private static final Color PRIMARY_LIGHT = new Color(160, 30, 30);
    private static final Color BG_COLOR = new Color(245, 245, 250);
    private static final Color WHITE = Color.WHITE;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

public LoginFrame() {
        setTitle("Login - Bengkel Lathifah");
        
        // Set custom icon untuk window title bar
        try {
            Image icon = new ImageIcon(getClass().getResource("/assets/LOGO.png")).getImage();
            setIconImage(icon);
        } catch (Exception e) {
            // Jika icon gagal load, gunakan default
            System.out.println("Warning: Gagal load icon - " + e.getMessage());
        }
        
        setSize(900, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // Left Panel - Branding
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY, 0, getHeight(), PRIMARY_DARK);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(80, 40, 80, 40));

        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/WRENCH-NoBackgroud.png"));
        Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel lblIcon = new JLabel(new ImageIcon(img));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);


        JLabel lblTitle = new JLabel("BENGKEL LATHIFAH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Sistem Informasi Manajemen");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(255, 255, 255, 200));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel("<html><center>Penjualan dan Jasa<br>Bengkel Mobil</center></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(255, 255, 255, 180));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(lblIcon);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(lblTitle);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(lblSubtitle);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(lblDesc);
        leftPanel.add(Box.createVerticalGlue());

        // Right Panel - Login Form
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(BG_COLOR);
        rightPanel.setLayout(new GridBagLayout());

        JPanel formCard = new JPanel();
        formCard.setBackground(WHITE);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        formCard.setPreferredSize(new Dimension(340, 350));

        JLabel lblLogin = new JLabel("Masuk ke Sistem");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogin.setForeground(PRIMARY);
        lblLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblWelcome = new JLabel("Silakan masukkan kredensial Anda");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblWelcome.setForeground(Color.GRAY);
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(80, 80, 80));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(80, 80, 80));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLogin = new JButton("MASUK");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(PRIMARY);
        btnLogin.setForeground(WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnLogin.setBackground(PRIMARY_DARK); }
            public void mouseExited(MouseEvent e) { btnLogin.setBackground(PRIMARY); }
        });

        btnLogin.addActionListener(e -> doLogin());
        txtPassword.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        });
        txtUsername.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) txtPassword.requestFocus();
            }
        });

        formCard.add(lblLogin);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(lblWelcome);
        formCard.add(Box.createVerticalStrut(25));
        formCard.add(lblUser);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(txtUsername);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(lblPass);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(txtPassword);
        formCard.add(Box.createVerticalStrut(25));
        formCard.add(btnLogin);

        rightPanel.add(formCard);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        setContentPane(mainPanel);
    }

    /**
     * Perform login using SwingWorker to run database operations off the EDT.
     * This prevents UI freezing during authentication.
     */
    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password harus diisi!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if account is locked before attempting login
        if (LoginAttemptTracker.isLockedOut(username)) {
            int remaining = LoginAttemptTracker.getRemainingLockoutSeconds(username);
            JOptionPane.showMessageDialog(this,
                    "Akun terkunci karena terlalu banyak percobaan login gagal.\nCoba lagi dalam " + remaining + " detik.",
                    "Akun Terkonfirmasi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Disable button during login and show loading cursor
        btnLogin.setEnabled(false);
        btnLogin.setText("Loading...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Use SwingWorker to perform login off the EDT
        new SwingWorker<Pengguna, Void>() {
            
            @Override
            protected Pengguna doInBackground() throws Exception {
                // Run database operation in background thread
                PenggunaDAO dao = new PenggunaDAO();
                return dao.login(username, password);
            }

            @Override
            protected void done() {
                // Re-enable button and reset cursor
                btnLogin.setEnabled(true);
                btnLogin.setText("MASUK");
                setCursor(Cursor.getDefaultCursor());
                
                try {
                    Pengguna pengguna = get();
                    
                    if (pengguna != null) {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Selamat datang, " + pengguna.getNamaLengkap() + "!",
                                "Login Berhasil", JOptionPane.INFORMATION_MESSAGE);
                        new DashboardFrame(pengguna).setVisible(true);
                        LoginFrame.this.dispose();
                    } else {
                        // Show remaining attempts
                        int remaining = LoginAttemptTracker.getRemainingAttempts(username);
                        if (remaining <= 2) {
                            JOptionPane.showMessageDialog(LoginFrame.this,
                                    "Username atau Password salah!\nPercobaan tersisa: " + remaining,
                                    "Login Gagal", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(LoginFrame.this,
                                    "Username atau Password salah!",
                                    "Login Gagal", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Terjadi kesalahan: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
