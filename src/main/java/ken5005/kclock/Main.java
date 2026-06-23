package ken5005.kclock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Main {

    private static final int    WINDOW_WIDTH  = 290;
    private static final int    WINDOW_HEIGHT = 45;
    private static final int    FONT_SIZE     = 27;
    private static final Color  TEXT_COLOR    = Color.WHITE;
    private static final Color  BG_COLOR      = Color.BLACK;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss M/d(E)", Locale.JAPANESE);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("kClock");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);
            frame.setAlwaysOnTop(true);

            JLabel label = new JLabel("", SwingConstants.CENTER);
            label.setFont(new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE));
            label.setForeground(TEXT_COLOR);

            label.setText(LocalDateTime.now().format(FMT));

            new Timer(1000, e -> label.setText(LocalDateTime.now().format(FMT))).start();

            JPopupMenu menu = new JPopupMenu();
            JMenuItem exit = new JMenuItem("Exit");
            exit.addActionListener(e -> System.exit(0));
            menu.add(exit);

            final Point dragOffset = new Point();
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    } else {
                        dragOffset.setLocation(e.getX(), e.getY());
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
            label.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    frame.setLocation(e.getXOnScreen() - dragOffset.x,
                            e.getYOnScreen() - dragOffset.y);
                }
            });

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            panel.setBackground(BG_COLOR);
            panel.add(label, BorderLayout.CENTER);

            frame.setContentPane(panel);
            frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
