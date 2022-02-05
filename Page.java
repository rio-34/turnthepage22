import java.util.List;
import java.awt.FlowLayout;

import javax.swing.JPanel;

public class Page {
    int number;
    int from;
    int to;
    JPanel panel;

    public Page (){
        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setOpaque(true);
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
