package Email;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 *
 * @author Runa Yamada
 */
public class EmailDialog extends JDialog {

    private JTextField txtTo;
    private JTextField txtSubject;
    private JTextArea txtMessage;
    private File attachment;

    private static class RoundedHoverButton extends JButton {
        private final Color normal = new Color(0,155,155);
        private final Color hover  = new Color(0,200,200);

        RoundedHoverButton(String text) {
            super(text);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(160,40));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e){ setBackground(hover); }
                public void mouseExited (java.awt.event.MouseEvent e){ setBackground(normal); }
            });
            setBackground(normal);
        }

        @Override
        protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
            super.paintComponent(g);
        }
    }

    public EmailDialog(Frame parent, String studentName, String studentId, String email, String pdfPath) {
        super(parent, "Send Report via Email", true);

        this.attachment = new File(pdfPath);

        setSize(650, 540);
        getContentPane().setBackground(new Color(230,230,230)); // Grey: Same as loaded table
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parent);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        // TOP 
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(230,230,230));
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15,20,0,20));

        // ---- Send To ----
        JLabel lblTo = new JLabel("Send To (Email):");
        lblTo.setFont(labelFont);
        txtTo = new JTextField(email);
        txtTo.setFont(fieldFont);
        txtTo.setPreferredSize(new Dimension(500,35));
        txtTo.setMaximumSize(new Dimension(Integer.MAX_VALUE,35));

        topPanel.add(lblTo);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(txtTo);
        topPanel.add(Box.createVerticalStrut(15));

        // ---- Subject ----
        JLabel lblSubject = new JLabel("Subject:");
        lblSubject.setFont(labelFont);
        txtSubject = new JTextField("Academic Report for " + studentName);
        txtSubject.setFont(fieldFont);
        txtSubject.setPreferredSize(new Dimension(500,35));
        txtSubject.setMaximumSize(new Dimension(Integer.MAX_VALUE,35));

        topPanel.add(lblSubject);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(txtSubject);
        topPanel.add(Box.createVerticalStrut(15));

        add(topPanel, BorderLayout.NORTH);

        // MESSAGE AREA
        JPanel midPanel = new JPanel(new BorderLayout());
        midPanel.setBackground(new Color(230,230,230));
        midPanel.setBorder(BorderFactory.createEmptyBorder(0,20,0,20));

        JLabel lblMsg = new JLabel("Message");
        lblMsg.setFont(labelFont);

        txtMessage = new JTextArea(
                "Dear " + studentName + ",\n\n"
                        + "Attached is your academic performance report.\n"
                        + "Please review the details and contact us if you need any help.\n\n"
                        + "Regards,\nAcademic Office"
        );
        txtMessage.setFont(fieldFont);
        txtMessage.setLineWrap(true);
        txtMessage.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(txtMessage);
        scroll.setPreferredSize(new Dimension(500,220));

        midPanel.add(lblMsg, BorderLayout.NORTH);
        midPanel.add(scroll, BorderLayout.CENTER);

        add(midPanel, BorderLayout.CENTER);

        // BUTTONS
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));
        btnPanel.setBackground(new Color(230,230,230));

        RoundedHoverButton btnSend = new RoundedHoverButton("Send Email");
        RoundedHoverButton btnCancel = new RoundedHoverButton("Cancel");

        btnSend.addActionListener(e -> onSend());
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnSend);
        btnPanel.add(btnCancel);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private void onSend(){
        String to = txtTo.getText().trim();
        String subject = txtSubject.getText().trim();
        String msg = txtMessage.getText().trim();

        if(to.isEmpty()){
            JOptionPane.showMessageDialog(this, "Recipient email required!");
            return;
        }

        boolean ok = EmailSenderWithAttachment.sendEmailWithAttachment(
                to, subject, msg, attachment.getAbsolutePath()
        );

        if(ok){
            JOptionPane.showMessageDialog(this, "Email sent successfully!");
            dispose();
        }else{
            JOptionPane.showMessageDialog(this, "Failed to send email.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}