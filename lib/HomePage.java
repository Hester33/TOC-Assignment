package lib;

//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.FlowLayout;
//import java.awt.GridLayout;
import java.awt.*;

//import javax.swing.BorderFactory;
//import javax.swing.BoxLayout;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.SwingConstants;
import javax.swing.*;

import lib.model.Member;;

// Main page of the GUI
public class HomePage {
    JPanel panel = new JPanel();
    JPanel panel_header = new JPanel();
    JPanel panel_members = new JPanel();

    HomePage() {
        this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
        this.panel_header.setLayout(new GridLayout(3, 1));
        this.panel_members.setLayout(new BoxLayout(this.panel_members, BoxLayout.Y_AXIS));

        // HEADER

        JLabel label_title = new JLabel("TIC2151 Theory of Computation", SwingConstants.CENTER);
        JLabel label_ttNo = new JLabel("Tutorial Section: TT1L", SwingConstants.CENTER);
        JLabel label_groupNo = new JLabel("Group number: G10", SwingConstants.CENTER);

        this.panel_header.add(label_title);
        this.panel_header.add(label_ttNo);
        this.panel_header.add(label_groupNo);
        this.panel.add(this.panel_header);

        // MEMBERS
        // TODO: add member photos
        Member[] members = { new Member("1181203140", "Ong Wen Xuan", "lib/assets/images/hester.jpg", "25%"),
                new Member("1181203056", "Elton Wong Chun Meng", "lib/assets/images/java.png", "25%"),
                new Member("1181203212", "Reynard Kok Jin Yik", "lib/assets/images/rey.jpg", "25%"), 
                new Member("1191102550", "Ng Zhi Shuen", "lib/assets/images/java.png", "25%"), };

        for (int i = 0; i < members.length; i++) {
            JPanel panel_member = new JPanel();
            JPanel member_image = new JPanel();
            JPanel member_label = new JPanel();
            JPanel panel_memberInfo = new JPanel();
            panel_member.setLayout(new GridLayout(1, 3));
            member_image.setLayout(new FlowLayout());
            member_label.setLayout(new GridLayout(3, 1));
            panel_memberInfo.setLayout(new GridLayout(3, 1));

            ImagePanel image = new ImagePanel(members[i].image);
            image.setPreferredSize(new Dimension(80, 80));
            member_image.add(image);

            JLabel idLabel = new JLabel("Student ID:   ");
            JLabel id = new JLabel(members[i].id);
            JLabel nameLabel = new JLabel("Name:   ");
            JLabel name = new JLabel(members[i].name);
            JLabel participationLabel = new JLabel("Participation:   ");
            JLabel participation = new JLabel(members[i].participation);
            member_label.add(idLabel);
            panel_memberInfo.add(id);
            member_label.add(nameLabel);
            panel_memberInfo.add(name);
            member_label.add(participationLabel);
            panel_memberInfo.add(participation);
            panel_member.add(member_image);
            panel_member.add(member_label);
            panel_member.add(panel_memberInfo);
            panel_member.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            this.panel_members.add(panel_member);
        }
        this.panel_header.setBorder(BorderFactory.createRaisedBevelBorder());
        this.panel_header.setPreferredSize(new Dimension(100, 70));
        
        this.panel.add(this.panel_members);
        this.panel_members.setPreferredSize(new Dimension(200, 70));
    }
}
