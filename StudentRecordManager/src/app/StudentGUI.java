package app;

import model.Student;
import service.StudentManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Dark "glass" themed window for the Student Record Management System.
 */
public class StudentGUI extends JFrame {
    // ---------- Theme ----------
    static final Color CORAL = new Color(255, 111, 125);
    static final Color CORAL2 = new Color(255, 77, 126);
    static final Color MUTED = new Color(0xB4B4BE);
    static final Color FIELD_BG = new Color(0x1E1E22);
    static final String FONT_FAMILY = pickFont();

    static String pickFont() {
        String[] wanted = {"Segoe UI", "Poppins", "Montserrat", "Helvetica Neue", "Arial"};
        String[] available = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String w : wanted) {
            for (String a : available) {
                if (a.equalsIgnoreCase(w)) {
                    return a;
                }
            }
        }
        return Font.SANS_SERIF;
    }

    static Font font(int style, int size) {
        return new Font(FONT_FAMILY, style, size);
    }

    // ---------- Data ----------
    private StudentManager manager = new StudentManager();
    private int nextId = 1;
    private Student editing = null;
    private String chosenPhoto = "";

    // ---------- Components ----------
    private BackgroundPanel bg = new BackgroundPanel();
    private CardLayout cardLayout = new CardLayout();
    private JPanel content = new JPanel(cardLayout);
    private JLabel titleLabel = new JLabel();
    private JLabel subtitleLabel = new JLabel();
    private JPanel toolsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    private JTextField searchField = new JTextField(14);
    private ArrayList<NavItem> navItems = new ArrayList<>();

    private JLabel totalValue = new JLabel("0");
    private JLabel avgValue = new JLabel("0.0");
    private JLabel topperValue = new JLabel("-");
    private JLabel gradeAValue = new JLabel("0");

    private JPanel cardsGrid = new JPanel(new GridLayout(0, 2, 18, 18));
    private DefaultTableModel tableModel;

    private JTextField enrollField = new JTextField();
    private JTextField admissionField = new JTextField();
    private JTextField nameField = new JTextField();
    private JTextField programField = new JTextField("B.Tech CSE");
    private JComboBox<String> sectionBox = new JComboBox<>(new String[]{"A", "B", "C", "D", "E", "F"});
    private JTextField[] markFields = new JTextField[Student.SUBJECTS];
    private JLabel photoPreview = new JLabel();

    public StudentGUI() {
        setTitle("Student Record Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(bg);

        bg.add(buildSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.add(buildTopBar(), BorderLayout.NORTH);

        content.setOpaque(false);
        content.add(buildDashboard(), "dash");
        content.add(buildCardsPage(), "cards");
        content.add(buildForm(), "form");
        content.add(buildTablePage(), "table");
        main.add(content, BorderLayout.CENTER);
        bg.add(main, BorderLayout.CENTER);

        clearForm();
        refreshAll();
        showPage("dash");

        setSize(1180, 740);
        setLocationRelativeTo(null);
    }

    // ================= Custom look components =================

    /** Black background with two glowing pink spheres. */
    static class BackgroundPanel extends JPanel {
        BackgroundPanel() {
            super(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(0x0B0B0D));
            g.fillRect(0, 0, getWidth(), getHeight());
            sphere(g, 40, getHeight() * 0.38f, 250);
            sphere(g, getWidth() - 30, getHeight() * 0.88f, 180);
            g.dispose();
        }

        private void sphere(Graphics2D g, float cx, float cy, float r) {
            float[] fractions = {0f, 0.55f, 0.96f, 1f};
            Color[] colors = {
                    new Color(255, 128, 132),
                    new Color(238, 84, 116),
                    new Color(120, 30, 80),
                    new Color(120, 30, 80, 0)
            };
            g.setPaint(new RadialGradientPaint(new Point2D.Float(cx, cy), r, fractions, colors));
            g.fill(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));
        }
    }

    /** Frosted dark rounded panel. */
    static class GlassPanel extends JPanel {
        private final int arc;

        GlassPanel(LayoutManager layout, int arc) {
            super(layout);
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g.setPaint(new GradientPaint(0, 0, new Color(44, 44, 48, 218), 0, h, new Color(16, 16, 18, 228)));
            g.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);
            g.setColor(new Color(255, 255, 255, 48));
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(0, 0, w - 2, h - 2, arc, arc);
            g.dispose();
            super.paintComponent(g0);
        }
    }

    /** Translucent dark strip for the side navigation. */
    static class SidePanel extends JPanel {
        SidePanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setColor(new Color(10, 10, 12, 175));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(255, 255, 255, 35));
            g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
            g.dispose();
        }
    }

    /** Rounded buttons: filled gradient, outline, or plain text. */
    static class PillButton extends JButton {
        static final int FILLED = 0;
        static final int OUTLINE = 1;
        static final int TEXT = 2;
        private final int style;
        private boolean hover = false;

        PillButton(String text, int style, Color textColor) {
            super(text);
            this.style = style;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setForeground(textColor);
            setFont(font(Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(style == TEXT ? new EmptyBorder(6, 4, 6, 4) : new EmptyBorder(9, 20, 9, 20));
            final Color base = textColor;
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    if (PillButton.this.style == TEXT) {
                        setForeground(base.brighter());
                    }
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    setForeground(base);
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            if (style == FILLED) {
                Color a = hover ? CORAL.brighter() : CORAL;
                Color b = hover ? CORAL2.brighter() : CORAL2;
                g.setPaint(new GradientPaint(0, 0, a, w, h, b));
                g.fillRoundRect(0, 0, w, h, h, h);
            } else if (style == OUTLINE) {
                if (hover) {
                    g.setColor(new Color(255, 255, 255, 30));
                    g.fillRoundRect(0, 0, w, h, h, h);
                }
                g.setColor(new Color(255, 255, 255, 210));
                g.setStroke(new BasicStroke(1.3f));
                g.drawRoundRect(1, 1, w - 3, h - 3, h, h);
            }
            g.dispose();
            super.paintComponent(g0);
        }
    }

    /** Side tab: gradient pill when active. */
    static class NavItem extends JLabel {
        private boolean active = false;
        private boolean hover = false;

        NavItem(String text) {
            super(text);
            setOpaque(false);
            setForeground(new Color(0xC9C9D1));
            setFont(font(Font.PLAIN, 15));
            setBorder(new EmptyBorder(13, 34, 13, 12));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        void setActive(boolean a) {
            active = a;
            setForeground(a ? Color.WHITE : new Color(0xC9C9D1));
            setFont(font(a ? Font.BOLD : Font.PLAIN, 15));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            if (active) {
                g.setPaint(new GradientPaint(0, 0, CORAL, w, 0, CORAL2));
                g.fillRoundRect(14, 3, w - 28, h - 6, h, h);
            } else if (hover) {
                g.setColor(new Color(255, 255, 255, 22));
                g.fillRoundRect(14, 3, w - 28, h - 6, h, h);
            }
            g.dispose();
            super.paintComponent(g0);
        }
    }

    /** Slim coral scrollbar. */
    static class SlimScrollUI extends BasicScrollBarUI {
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return emptyButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return emptyButton();
        }

        private JButton emptyButton() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            return b;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            // transparent track
        }

        @Override
        protected void paintThumb(Graphics g0, JComponent c, Rectangle r) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(255, 111, 125, 170));
            g.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 8, 8);
            g.dispose();
        }
    }

    // ================= Layout builders =================

    private JPanel buildSidebar() {
        SidePanel side = new SidePanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setPreferredSize(new Dimension(225, 0));

        JLabel brand = new JLabel("<html>Student<br><font color='#FF6F7D'>Records</font></html>");
        brand.setForeground(Color.WHITE);
        brand.setFont(font(Font.BOLD, 26));
        brand.setBorder(new EmptyBorder(30, 30, 30, 10));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(brand);

        side.add(navItem("Dashboard", "dash"));
        side.add(navItem("Students", "cards"));
        side.add(navItem("Add Student", "form"));
        side.add(navItem("Records", "table"));

        side.add(Box.createVerticalGlue());
        JLabel foot = new JLabel("Java PBL Project");
        foot.setForeground(MUTED);
        foot.setFont(font(Font.PLAIN, 12));
        foot.setBorder(new EmptyBorder(10, 30, 22, 10));
        foot.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(foot);
        return side;
    }

    private NavItem navItem(String text, String page) {
        NavItem item = new NavItem(text);
        item.putClientProperty("page", page);
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (page.equals("form")) {
                    startAdd();
                }
                showPage(page);
            }
        });
        navItems.add(item);
        return item;
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(22, 28, 10, 28));

        titleLabel.setFont(font(Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        subtitleLabel.setFont(font(Font.PLAIN, 13));
        subtitleLabel.setForeground(MUTED);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titles.add(titleLabel);
        titles.add(Box.createVerticalStrut(2));
        titles.add(subtitleLabel);
        bar.add(titles, BorderLayout.WEST);

        toolsPanel.setOpaque(false);
        styleField(searchField);
        PillButton searchBtn = new PillButton("Search", PillButton.FILLED, Color.WHITE);
        PillButton rankBtn = new PillButton("Rank List", PillButton.OUTLINE, Color.WHITE);
        PillButton resetBtn = new PillButton("Show All", PillButton.TEXT, Color.WHITE);
        toolsPanel.add(searchField);
        toolsPanel.add(searchBtn);
        toolsPanel.add(rankBtn);
        toolsPanel.add(resetBtn);
        bar.add(toolsPanel, BorderLayout.EAST);

        searchBtn.addActionListener(e -> doSearch());
        searchField.addActionListener(e -> doSearch());
        rankBtn.addActionListener(e -> {
            manager.sortByAverage();
            refreshAll();
        });
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            refreshAll();
        });
        return bar;
    }

    private JPanel buildDashboard() {
        JPanel page = new JPanel(new BorderLayout(0, 22));
        page.setOpaque(false);
        page.setBorder(new EmptyBorder(10, 28, 28, 28));

        JPanel stats = new JPanel(new GridLayout(1, 4, 18, 0));
        stats.setOpaque(false);
        stats.add(statCard("Total Students", totalValue));
        stats.add(statCard("Class Average", avgValue));
        stats.add(statCard("Topper", topperValue));
        stats.add(statCard("Grade A Students", gradeAValue));
        topperValue.setFont(font(Font.BOLD, 20));
        page.add(stats, BorderLayout.NORTH);

        GlassPanel welcome = new GlassPanel(new BorderLayout(0, 16), 32);
        welcome.setBorder(new EmptyBorder(28, 30, 28, 30));
        JLabel text = new JLabel("<html><div style='width:560px'>"
                + "<h1>Welcome <font color='#FF6F7D'>back</font></h1>"
                + "<p>Use the tabs on the left to move around:</p>"
                + "<ul>"
                + "<li><b>Students</b> - profile cards with photo and details</li>"
                + "<li><b>Add Student</b> - enrollment no., admission no., program, section, marks and photo</li>"
                + "<li><b>Records</b> - full table with totals, averages and grades</li>"
                + "</ul>"
                + "<p>Search and Rank List are at the top of the Students and Records tabs.</p>"
                + "</div></html>");
        text.setForeground(Color.WHITE);
        text.setFont(font(Font.PLAIN, 14));
        welcome.add(text, BorderLayout.NORTH);

        PillButton demoBtn = new PillButton("Load Demo Data", PillButton.FILLED, Color.WHITE);
        demoBtn.addActionListener(e -> loadDemoData());
        JPanel demoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        demoRow.setOpaque(false);
        demoRow.add(demoBtn);
        PillButton importBtn = new PillButton("Import from CSV", PillButton.OUTLINE, Color.WHITE);
        importBtn.addActionListener(e -> importCsv());
        demoRow.add(Box.createHorizontalStrut(12));
        demoRow.add(importBtn);
        welcome.add(demoRow, BorderLayout.CENTER);
        page.add(welcome, BorderLayout.CENTER);
        return page;
    }

    private JPanel statCard(String title, JLabel valueLabel) {
        GlassPanel p = new GlassPanel(new BorderLayout(0, 10), 28);
        p.setBorder(new EmptyBorder(20, 22, 20, 22));
        JLabel t = new JLabel(title);
        t.setForeground(MUTED);
        t.setFont(font(Font.PLAIN, 13));
        valueLabel.setFont(font(Font.BOLD, 30));
        valueLabel.setForeground(Color.WHITE);
        p.add(t, BorderLayout.NORTH);
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }

    private JScrollPane transparentScroll(Component view) {
        JScrollPane scroll = new JScrollPane(view);
        scroll.setBorder(null);
        scroll.setViewportBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        scroll.getVerticalScrollBar().setUI(new SlimScrollUI());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scroll.getVerticalScrollBar().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        return scroll;
    }

    private JScrollPane buildCardsPage() {
        JPanel holder = new JPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.setBorder(new EmptyBorder(10, 28, 28, 22));
        cardsGrid.setOpaque(false);
        holder.add(cardsGrid, BorderLayout.NORTH);
        return transparentScroll(holder);
    }

    private JPanel buildTablePage() {
        String[] cols = {"Enrollment No.", "Admission No.", "Name", "Program", "Section",
                "M1", "M2", "M3", "Total", "Average", "Grade"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(font(Font.PLAIN, 13));
        table.setFillsViewportHeight(true);
        table.setBackground(new Color(0x1A1A1E));

        DefaultTableCellRenderer cell = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, false, row, col);
                setOpaque(true);
                setBackground(sel ? new Color(0x5A2A3A)
                        : (row % 2 == 0 ? new Color(0x1A1A1E) : new Color(0x222227)));
                setForeground(col == 10 ? CORAL : Color.WHITE);
                setFont(font(col == 2 || col == 10 ? Font.BOLD : Font.PLAIN, 13));
                setBorder(new EmptyBorder(0, 8, 0, 4));
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, cell);
        table.setDefaultRenderer(Number.class, cell);

        DefaultTableCellRenderer head = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, false, false, row, col);
                setOpaque(true);
                setBackground(new Color(0x2C2C32));
                setForeground(CORAL);
                setFont(font(Font.BOLD, 12));
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(new EmptyBorder(10, 8, 10, 4));
                return this;
            }
        };
        table.getTableHeader().setDefaultRenderer(head);
        table.getTableHeader().setReorderingAllowed(false);
        int[] widths = {135, 140, 185, 115, 80, 55, 55, 55, 65, 85, 65};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setColumnHeaderView(table.getTableHeader());
        scroll.getColumnHeader().setBackground(new Color(0x2C2C32));
        JPanel corner = new JPanel();
        corner.setBackground(new Color(0x2C2C32));
        scroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(0x1A1A1E));
        scroll.getVerticalScrollBar().setUI(new SlimScrollUI());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scroll.getVerticalScrollBar().setBackground(new Color(0x1A1A1E));

        GlassPanel glass = new GlassPanel(new BorderLayout(), 28);
        glass.setBorder(new EmptyBorder(14, 14, 14, 14));
        glass.add(scroll, BorderLayout.CENTER);

        JPanel page = new JPanel(new BorderLayout());
        page.setOpaque(false);
        page.setBorder(new EmptyBorder(10, 28, 28, 28));
        page.add(glass, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildForm() {
        JPanel page = new JPanel(new BorderLayout());
        page.setOpaque(false);
        page.setBorder(new EmptyBorder(10, 28, 28, 28));

        GlassPanel box = new GlassPanel(new BorderLayout(30, 18), 32);
        box.setBorder(new EmptyBorder(26, 30, 26, 30));

        JLabel heading = new JLabel("Student details");
        heading.setFont(font(Font.BOLD, 18));
        heading.setForeground(Color.WHITE);
        box.add(heading, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(0, 2, 14, 14));
        fields.setOpaque(false);
        addRow(fields, "Enrollment No.", enrollField);
        addRow(fields, "Admission No.", admissionField);
        addRow(fields, "Name", nameField);
        addRow(fields, "Program", programField);
        addRow(fields, "Section", sectionBox);
        for (int i = 0; i < markFields.length; i++) {
            markFields[i] = new JTextField();
            addRow(fields, "Marks - Subject " + (i + 1) + " (0-100)", markFields[i]);
        }
        styleField(enrollField);
        styleField(admissionField);
        styleField(nameField);
        styleField(programField);
        for (JTextField f : markFields) {
            styleField(f);
        }
        sectionBox.setFont(font(Font.PLAIN, 14));
        sectionBox.setBackground(FIELD_BG);
        sectionBox.setForeground(Color.WHITE);

        JPanel fieldsWrap = new JPanel(new BorderLayout());
        fieldsWrap.setOpaque(false);
        fieldsWrap.add(fields, BorderLayout.NORTH);
        box.add(fieldsWrap, BorderLayout.CENTER);

        // Photo area
        JPanel photoPanel = new JPanel(new BorderLayout(0, 14));
        photoPanel.setOpaque(false);
        photoPreview.setHorizontalAlignment(SwingConstants.CENTER);
        photoPanel.add(photoPreview, BorderLayout.NORTH);
        PillButton chooseBtn = new PillButton("Choose Photo", PillButton.OUTLINE, Color.WHITE);
        PillButton removeBtn = new PillButton("Remove Photo", PillButton.TEXT, MUTED);
        JPanel photoBtns = new JPanel(new GridLayout(2, 1, 0, 6));
        photoBtns.setOpaque(false);
        photoBtns.add(chooseBtn);
        photoBtns.add(removeBtn);
        photoPanel.add(photoBtns, BorderLayout.CENTER);
        JPanel photoWrap = new JPanel(new BorderLayout());
        photoWrap.setOpaque(false);
        photoWrap.setBorder(new EmptyBorder(0, 10, 0, 10));
        photoWrap.add(photoPanel, BorderLayout.NORTH);
        box.add(photoWrap, BorderLayout.EAST);

        // Save / Clear
        PillButton saveBtn = new PillButton("Save Student", PillButton.FILLED, Color.WHITE);
        PillButton clearBtn = new PillButton("Clear", PillButton.OUTLINE, Color.WHITE);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btns.setOpaque(false);
        btns.add(saveBtn);
        btns.add(clearBtn);
        box.add(btns, BorderLayout.SOUTH);

        chooseBtn.addActionListener(e -> choosePhoto());
        removeBtn.addActionListener(e -> {
            chosenPhoto = "";
            photoPreview.setIcon(avatar("", 130));
        });
        saveBtn.addActionListener(e -> saveStudent());
        clearBtn.addActionListener(e -> {
            editing = null;
            clearForm();
            showPage("form");
        });

        page.add(box, BorderLayout.NORTH);
        return page;
    }

    private void addRow(JPanel panel, String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setForeground(MUTED);
        l.setFont(font(Font.PLAIN, 13));
        panel.add(l);
        panel.add(field);
    }

    private void styleField(JTextField f) {
        f.setOpaque(true);
        f.setBackground(FIELD_BG);
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setFont(font(Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 255, 255, 55), 1, true), new EmptyBorder(8, 12, 8, 12)));
    }

    // ================= Student profile card =================

    private JPanel buildCard(Student s) {
        GlassPanel card = new GlassPanel(new BorderLayout(0, 12), 30);
        card.setBorder(new EmptyBorder(20, 22, 14, 22));

        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);
        header.add(new JLabel(avatar(s.getPhotoPath(), 78)), BorderLayout.WEST);

        JLabel name = new JLabel(s.getName());
        name.setFont(font(Font.BOLD, 20));
        name.setForeground(Color.WHITE);
        JLabel prog = new JLabel(s.getCourse() + "  |  Section " + s.getSection());
        prog.setFont(font(Font.PLAIN, 13));
        prog.setForeground(MUTED);
        JPanel names = new JPanel();
        names.setOpaque(false);
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
        name.setAlignmentX(Component.LEFT_ALIGNMENT);
        prog.setAlignmentX(Component.LEFT_ALIGNMENT);
        names.add(Box.createVerticalGlue());
        names.add(name);
        names.add(Box.createVerticalStrut(3));
        names.add(prog);
        names.add(Box.createVerticalGlue());
        header.add(names, BorderLayout.CENTER);
        card.add(header, BorderLayout.NORTH);

        JPanel details = new JPanel(new GridLayout(0, 1, 0, 8));
        details.setOpaque(false);
        details.setBorder(new EmptyBorder(6, 0, 4, 0));
        details.add(detailRow("Enrollment No.", s.getEnrollmentNo(), Color.WHITE));
        details.add(detailRow("Admission No.", s.getAdmissionNo(), Color.WHITE));
        details.add(detailRow("Average", String.format("%.1f   (Grade %c)", s.getAverage(), s.getGrade()), CORAL));
        card.add(details, BorderLayout.CENTER);

        PillButton editBtn = new PillButton("Edit", PillButton.TEXT, Color.WHITE);
        PillButton delBtn = new PillButton("Delete", PillButton.TEXT, CORAL);
        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        actions.add(editBtn, BorderLayout.WEST);
        actions.add(delBtn, BorderLayout.EAST);
        card.add(actions, BorderLayout.SOUTH);

        editBtn.addActionListener(e -> startEdit(s));
        delBtn.addActionListener(e -> deleteStudent(s));
        return card;
    }

    private JPanel detailRow(String label, String value, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setForeground(MUTED);
        l.setFont(font(Font.PLAIN, 13));
        JLabel v = new JLabel(value);
        v.setForeground(valueColor);
        v.setFont(font(Font.BOLD, 14));
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    private String esc(String t) {
        return t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /** Round photo (or a placeholder silhouette) with a coral ring. */
    private ImageIcon avatar(String path, int size) {
        BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setClip(new Ellipse2D.Float(0, 0, size, size));

        boolean drawn = false;
        if (path != null && !path.isEmpty()) {
            File f = new File(path);
            if (f.exists()) {
                ImageIcon raw = new ImageIcon(path);
                int iw = raw.getIconWidth();
                int ih = raw.getIconHeight();
                if (iw > 0 && ih > 0) {
                    double scale = Math.max((double) size / iw, (double) size / ih);
                    int dw = (int) Math.round(iw * scale);
                    int dh = (int) Math.round(ih * scale);
                    g.drawImage(raw.getImage(), (size - dw) / 2, (size - dh) / 2, dw, dh, null);
                    drawn = true;
                }
            }
        }
        if (!drawn) {
            g.setColor(new Color(0x3C3C41));
            g.fillRect(0, 0, size, size);
            g.setColor(new Color(0x9A9AA2));
            int head = size * 2 / 5;
            g.fillOval((size - head) / 2, size / 5, head, head);
            g.fillOval(size / 5, size / 5 + head + size / 12, size * 3 / 5, size);
        }
        g.setClip(null);
        g.setColor(new Color(255, 111, 125, 220));
        g.setStroke(new BasicStroke(2.2f));
        g.drawOval(1, 1, size - 3, size - 3);
        g.dispose();
        return new ImageIcon(out);
    }

    // ================= Dialogs =================

    private boolean showDialog(String title, String message, boolean askConfirm) {
        Window owner = SwingUtilities.getWindowAncestor(bg);
        JDialog d = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        d.setUndecorated(true);
        boolean[] result = {false};

        JPanel box = new JPanel(new BorderLayout(0, 20));
        box.setBackground(new Color(0x17171B));
        box.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CORAL, 2, true), new EmptyBorder(24, 28, 20, 28)));

        JLabel t = new JLabel(title);
        t.setFont(font(Font.BOLD, 18));
        t.setForeground(CORAL);
        JLabel m = new JLabel("<html><div style='width:280px'>" + esc(message) + "</div></html>");
        m.setFont(font(Font.PLAIN, 14));
        m.setForeground(Color.WHITE);
        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(t, BorderLayout.NORTH);
        top.add(m, BorderLayout.CENTER);
        box.add(top, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setOpaque(false);
        if (askConfirm) {
            PillButton no = new PillButton("No", PillButton.OUTLINE, Color.WHITE);
            no.addActionListener(e -> d.dispose());
            btns.add(no);
        }
        PillButton ok = new PillButton(askConfirm ? "Yes" : "OK", PillButton.FILLED, Color.WHITE);
        ok.addActionListener(e -> {
            result[0] = true;
            d.dispose();
        });
        btns.add(ok);
        box.add(btns, BorderLayout.SOUTH);

        d.setContentPane(box);
        d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);
        return result[0];
    }

    private void showError(String msg) {
        showDialog("Oops", msg, false);
    }

    // ================= Navigation and refresh =================

    private void showPage(String page) {
        cardLayout.show(content, page);
        String white;
        String accent;
        String sub;
        switch (page) {
            case "cards":
                white = "Student ";
                accent = "Profiles";
                sub = "Photo, enrollment, admission, program and section";
                break;
            case "form":
                white = editing == null ? "Add " : "Edit ";
                accent = "Student";
                sub = editing == null ? "Fill in the details and save" : "Update the details and save";
                break;
            case "table":
                white = "Marks ";
                accent = "Records";
                sub = "All students with totals, averages and grades";
                break;
            default:
                white = "Class ";
                accent = "Dashboard";
                sub = "Your students at a glance";
                break;
        }
        titleLabel.setText("<html>" + white + "<font color='#FF6F7D'>" + accent + "</font></html>");
        subtitleLabel.setText(sub);
        if (titleLabel.getParent() != null) {
            titleLabel.getParent().revalidate();
        }
        toolsPanel.setVisible(page.equals("cards") || page.equals("table"));
        for (NavItem item : navItems) {
            item.setActive(page.equals(item.getClientProperty("page")));
        }
    }

    private void refreshAll() {
        showList(manager.getAll());
        updateDashboard();
    }

    private void showList(ArrayList<Student> list) {
        cardsGrid.removeAll();
        if (list.isEmpty()) {
            JLabel empty = new JLabel("No students to show. Use 'Add Student' or load demo data from the Dashboard.");
            empty.setForeground(MUTED);
            empty.setFont(font(Font.PLAIN, 14));
            cardsGrid.add(empty);
        }
        tableModel.setRowCount(0);
        for (Student s : list) {
            cardsGrid.add(buildCard(s));
            int[] m = s.getMarks();
            tableModel.addRow(new Object[]{
                    s.getEnrollmentNo(), s.getAdmissionNo(), s.getName(), s.getCourse(), s.getSection(),
                    m[0], m[1], m[2], s.getTotal(), String.format("%.2f", s.getAverage()), s.getGrade()
            });
        }
        cardsGrid.revalidate();
        cardsGrid.repaint();
    }

    private void updateDashboard() {
        ArrayList<Student> all = manager.getAll();
        double sum = 0;
        int gradeA = 0;
        for (Student s : all) {
            sum += s.getAverage();
            if (s.getGrade() == 'A') {
                gradeA++;
            }
        }
        totalValue.setText(String.valueOf(all.size()));
        avgValue.setText(all.isEmpty() ? "0.0" : String.format("%.1f", sum / all.size()));
        Student top = manager.getTopper();
        topperValue.setText(top == null ? "-" : top.getName());
        gradeAValue.setText(String.valueOf(gradeA));
    }

    private void doSearch() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            refreshAll();
        } else {
            showList(manager.searchAny(text));
        }
    }

    // ================= Form actions =================

    private void startAdd() {
        editing = null;
        clearForm();
    }

    private void startEdit(Student s) {
        editing = s;
        enrollField.setText(s.getEnrollmentNo());
        admissionField.setText(s.getAdmissionNo());
        nameField.setText(s.getName());
        programField.setText(s.getCourse());
        sectionBox.setSelectedItem(s.getSection());
        int[] m = s.getMarks();
        for (int i = 0; i < markFields.length; i++) {
            markFields[i].setText(String.valueOf(m[i]));
        }
        chosenPhoto = s.getPhotoPath();
        photoPreview.setIcon(avatar(chosenPhoto, 130));
        showPage("form");
    }

    private void clearForm() {
        enrollField.setText("");
        admissionField.setText("");
        nameField.setText("");
        programField.setText("B.Tech CSE");
        sectionBox.setSelectedIndex(0);
        for (JTextField f : markFields) {
            f.setText("");
        }
        chosenPhoto = "";
        photoPreview.setIcon(avatar("", 130));
    }

    private void choosePhoto() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Images (jpg, jpeg, png, gif)", "jpg", "jpeg", "png", "gif"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            chosenPhoto = fc.getSelectedFile().getAbsolutePath();
            photoPreview.setIcon(avatar(chosenPhoto, 130));
        }
    }

    private int[] readMarks() {
        int[] m = new int[Student.SUBJECTS];
        for (int i = 0; i < m.length; i++) {
            String t = markFields[i].getText().trim();
            if (t.isEmpty()) {
                m[i] = 0;
                continue;
            }
            try {
                m[i] = Integer.parseInt(t);
            } catch (NumberFormatException e) {
                showError("Marks must be numbers.");
                return null;
            }
            if (m[i] < 0 || m[i] > 100) {
                showError("Marks must be between 0 and 100.");
                return null;
            }
        }
        return m;
    }

    private void saveStudent() {
        String enroll = enrollField.getText().trim();
        String admission = admissionField.getText().trim();
        String name = nameField.getText().trim();
        String program = programField.getText().trim();
        String section = (String) sectionBox.getSelectedItem();

        if (name.isEmpty() || enroll.isEmpty() || admission.isEmpty() || program.isEmpty()) {
            showError("Please fill name, enrollment number, admission number and program.");
            return;
        }
        int[] marks = readMarks();
        if (marks == null) {
            return;
        }
        int exceptId = (editing == null) ? -1 : editing.getId();
        if (manager.enrollmentExists(enroll, exceptId)) {
            showError("A student with this enrollment number already exists.");
            return;
        }

        if (editing == null) {
            manager.addStudent(new Student(nextId++, name, program, marks,
                    enroll, admission, section, chosenPhoto));
        } else {
            editing.setName(name);
            editing.setCourse(program);
            editing.setEnrollmentNo(enroll);
            editing.setAdmissionNo(admission);
            editing.setSection(section);
            editing.setPhotoPath(chosenPhoto);
            editing.updateMarks(marks[0], marks[1], marks[2]);
        }
        editing = null;
        clearForm();
        searchField.setText("");
        refreshAll();
        showPage("cards");
    }

    private void deleteStudent(Student s) {
        if (showDialog("Delete student", "Delete " + s.getName() + "?", true)) {
            manager.delete(s.getId());
            refreshAll();
        }
    }

    private void loadDemoData() {
        String[][] demo = {
                {"EN2024001", "AD1001", "Aarav Sharma", "B.Tech CSE", "A", "92", "88", "95"},
                {"EN2024002", "AD1002", "Diya Patel", "B.Tech CSE", "A", "78", "82", "75"},
                {"EN2024003", "AD1003", "Rohan Mehta", "B.Tech CSE", "B", "65", "58", "70"},
                {"EN2024004", "AD1004", "Sneha Reddy", "B.Tech IT", "B", "85", "91", "89"},
                {"EN2024005", "AD1005", "Kabir Singh", "B.Tech CSE", "C", "45", "52", "48"},
                {"EN2024006", "AD1006", "Ananya Iyer", "B.Tech IT", "A", "96", "94", "98"},
                {"EN2024007", "AD1007", "Vihaan Gupta", "B.Tech CSE", "D", "35", "42", "38"},
                {"EN2024008", "AD1008", "Isha Verma", "B.Tech ECE", "B", "72", "68", "75"},
                {"EN2024009", "AD1009", "Arjun Nair", "B.Tech CSE", "C", "55", "61", "58"},
                {"EN2024010", "AD1010", "Meera Joshi", "B.Tech IT", "E", "88", "79", "84"},
                {"EN2024011", "AD1011", "Aditya Rao", "B.Tech ECE", "A", "68", "74", "71"},
                {"EN2024012", "AD1012", "Priya Kulkarni", "B.Tech CSE", "F", "91", "85", "93"},
                {"EN2024013", "AD1013", "Siddharth Das", "B.Tech CSE", "B", "62", "66", "59"},
                {"EN2024014", "AD1014", "Kavya Menon", "B.Tech IT", "C", "77", "83", "80"},
                {"EN2024015", "AD1015", "Harsh Agarwal", "B.Tech ECE", "D", "28", "34", "31"},
                {"EN2024016", "AD1016", "Riya Bansal", "B.Tech CSE", "A", "89", "92", "90"},
                {"EN2024017", "AD1017", "Yash Chopra", "B.Tech IT", "E", "50", "47", "55"},
                {"EN2024018", "AD1018", "Tanvi Deshmukh", "B.Tech CSE", "B", "74", "70", "78"},
                {"EN2024019", "AD1019", "Nikhil Pandey", "B.Tech ECE", "F", "41", "39", "44"},
                {"EN2024020", "AD1020", "Pooja Thakur", "B.Tech CSE", "C", "83", "87", "81"}
        };
        for (String[] d : demo) {
            if (!manager.enrollmentExists(d[0], -1)) {
                int[] marks = {Integer.parseInt(d[5]), Integer.parseInt(d[6]), Integer.parseInt(d[7])};
                manager.addStudent(new Student(nextId++, d[2], d[3], marks, d[0], d[1], d[4], ""));
            }
        }
        refreshAll();
        showPage("cards");
    }

    /** Reads students from a CSV file (header line first). */
    private void importCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("CSV files (*.csv)", "csv"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = fc.getSelectedFile();
        int added = 0;
        int skipped = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // header row
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] p = line.split(",", -1);
                if (p.length < 8) {
                    skipped++;
                    continue;
                }
                for (int i = 0; i < p.length; i++) {
                    p[i] = p[i].trim();
                }
                try {
                    int[] marks = {Integer.parseInt(p[5]), Integer.parseInt(p[6]), Integer.parseInt(p[7])};
                    boolean validMarks = true;
                    for (int m : marks) {
                        if (m < 0 || m > 100) {
                            validMarks = false;
                        }
                    }
                    if (!validMarks || p[0].isEmpty() || p[2].isEmpty()
                            || manager.enrollmentExists(p[0], -1)) {
                        skipped++;
                        continue;
                    }
                    String photo = "";
                    if (p.length > 8 && !p[8].isEmpty()) {
                        File pf = new File(p[8]);
                        if (!pf.isAbsolute()) {
                            pf = new File(file.getParentFile(), p[8]);
                        }
                        photo = pf.getAbsolutePath();
                    }
                    manager.addStudent(new Student(nextId++, p[2], p[3], marks, p[0], p[1], p[4], photo));
                    added++;
                } catch (NumberFormatException e) {
                    skipped++;
                }
            }
        } catch (IOException e) {
            showError("Could not read the file.");
            return;
        }
        refreshAll();
        showPage("cards");
        showDialog("Import finished", added + " students imported"
                + (skipped > 0 ? ", " + skipped + " skipped (duplicate or invalid rows)." : "."), false);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // keep the default look if this fails
        }
        UIManager.put("ComboBox.background", FIELD_BG);
        UIManager.put("ComboBox.foreground", Color.WHITE);
        UIManager.put("ComboBox.selectionBackground", CORAL2);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        SwingUtilities.invokeLater(() -> new StudentGUI().setVisible(true));
    }
}
