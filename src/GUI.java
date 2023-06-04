import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;

import javax.swing.*;
import javax.tools.Tool;

public class GUI {

    static class MyComponent extends JComponent{
        @Override
        protected void paintComponent(Graphics g){
            Font font = new Font("Calibri", Font.BOLD, 20);
            Graphics2D g2 = (Graphics2D)g;
            g2.setFont(font);
            g2.drawString("Hello wordl!", 20,20);
            Point2D p1 = new Point2D.Float(70,70);
            Point2D p2 = new Point2D.Float(190,190);
            Line2D l2 = new Line2D.Float(p1,p2);
            g2.draw(l2);
            Ellipse2D el = new Ellipse2D.Float(70,70,290,190);
            g2.setPaint(Color.blue);
            g2.fill(el);
            Rectangle2D r = new Rectangle2D.Float(70,70,190,190);
            g2.draw(r);
            try{
                URL url = new URL("https://yandex-images.clstorage.net/hJJ4i9198/2576e0-MhsN/lu-M1i0GMRWxfPhLe_dYGVMRnWtz0ar71Xwr9EB9kW6PGTwG_fyNKke08c464N_h1CxLuMNcWT8IuSHAL3deX9NqSQStguRFpS48Tbn3Tw4BBUr-fVhxMBy28_kLcUhhCFnsU6kuSnoUqOpK5f7c9kqlEjXTw9Ih3rG8XrH7nskSdVWE7Qs4VD82PRP7u9qOlRRQZ_uU-Tse_bG5TfvMp43SIh5lI1d_miVlV6z5kPLM6dU5HciRoZ1xM1t9cZWIUftcD2bB-NF9OPVRPrDSh1aZXqI5Hjw7EHW0M8J7SWuNXOIcI6HS-5sqJR_v-lT9ie6ZcxVBHKcbOH8UtT9ewlG6W49pSjCc9bX227p3nIlclZjrdhp9OV17tb6PfYBqz11mmeSlk7AU5KAZYv5WOkLnUjYaSpunmX71m3hzlEDQuloEawW4Ef0y8pUy-57MXtDXKz-X-b1asfB5C7AGqg5TJpfqKBB3VC3lH2c6H_aCLVv83EQaYVs5-V1-NVoLEvsUASfKfRU8uv3WejtfCFhUnqA5mfg0kbA9NsM0DumEFeIRLG7Qd1EkqJppedWwC-YRcVNBlCYVPDHSfruSwBbxnoPvDrPRs3150HazlARe3FUpNhkzuJzzcTQAfA-vCtkqkelh3HTcpOeXJ_4T-kLpGXLeixOq2XoyGz36FcUceBpNocXwW7p0OZa4NpZPWNKUqvIdc3oQOva3B70LYgqbZNZqLlJ00aLmWCn_E3kFLBh0UAzdqBX1PpMxslyNmbsSTOZBspt0-zTV8DcQTxvfk6u5k7x81bo2Mwy9CWED0ycbLmNQc5gtpJcnPl59A-mVNRbJFe1ZNrObN_vSQpx4nAnmSjBcdP51GvYz2s9ZnB4q_92xtZ5ztLsENQGixFAi2KXsEPsZKWzabL1YN0nl2LgdRJLvE_L0mLk0WIyfcNPA7o_yGn1y8ZA6f5RJHlsbK7adPbTXsnt4Ck");
                Image image = new ImageIcon(url).getImage();
                g2.drawImage(image,200,200,100,100,null);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static void createGUI(){
        UIManager.LookAndFeelInfo[] lookAndFeelInfo = UIManager.getInstalledLookAndFeels();
        for(int i = 0; i < lookAndFeelInfo.length; i++){
            System.out.println(lookAndFeelInfo[i]);
        }

        JFrame frame = new JFrame("Test frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        JLabel label = new JLabel("Test label");
        frame.getContentPane().add(label);
        //String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        //frame.getContentPane().add(new MyComponent());
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);
        JButton btn = new JButton("Ok");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setTitle(((JButton)e.getSource()).getText());
                panel.setBackground(Color.blue);
            }
        });
        panel.add(btn);
        JButton btn1 = new JButton("Metal");
        JButton btn2 = new JButton("Numbus");
        JButton btn3 = new JButton("CDE/Motif");
        JButton btn4 = new JButton("Windows");
        JButton btn5 = new JButton("Windows Classic");
        panel.add(btn1);
        panel.add(btn2);
        panel.add(btn3);
        panel.add(btn4);
        panel.add(btn5);
        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                }catch (Exception exc){
                    exc.printStackTrace();
                }
            }
        });
        btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                }catch (Exception exc){
                    exc.printStackTrace();
                }
            }
        });
        btn3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                }catch (Exception exc){
                    exc.printStackTrace();
                }
            }
        });
        btn4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel("java.swing.plaf.windows.WindowsLookAndFeel");
                }catch (Exception exc){
                    exc.printStackTrace();
                }
            }
        });
        btn5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
                }catch (Exception exc){
                    exc.printStackTrace();
                }
            }
        });

        frame.setPreferredSize(new Dimension(500, 300));
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        frame.setBounds(dimension.width/2 - 250, dimension.height/2 - 150, 500, 300);
        frame.pack();

        frame.setVisible(true);
    }

    static void start(){
        JFrame.setDefaultLookAndFeelDecorated(true);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
            }
        });
    }

}
