//Written by Selen Serdar.
import java.util.List;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Page {
    int number;
    int from;
    int to;
    JPanel panel;
    JLabel plable;
    JPanel notepanel;
    JPanel numbers;

    public Page (){
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        notepanel = new JPanel ();
        notepanel.setLayout(new FlowLayout());
        numbers = new JPanel();
        numbers.setLayout(new FlowLayout());
        panel.add(notepanel);
        panel.add(numbers);

    }

    public static Page findPage(List<Page> pages, int noteindex){
        for (Page page : pages){
            if (page.from<=noteindex && page.to>noteindex){
                return page;
            }
        } 
        return null;
    }
}
