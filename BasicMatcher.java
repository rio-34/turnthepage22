import java.util.ArrayList;
import java.util.List;


public class BasicMatcher implements Matcher {
    private int current=-1;
    public double matchpattern(List<Note> melody, int index, List<Sound> listened){
        double correct=0;
        double incorrect=0;
        for (index = 0; index < listened.size(); index++) {
                Sound actual =listened.get(index); 
                Note think = melody.get(index);
                
                if (actual.notename .equals(think.noteName)){
                    correct =correct +1;
                }
                else {
                    incorrect = incorrect+1;
                }
        }
        double num = correct / melody.size();
        double percent = num * 100;
        return percent;
    }
    public double matchpattern2(List<Note> melody, int index, List<Sound> listened){
        boolean done = false;
        double correct =0;
        double incorrect = 0;
        int lindex = 0;
        while (!done){
            Sound actual =listened.get(lindex); 
            Note think = melody.get(index);
            if (actual.notename .equals( think.noteName)){
                index ++;
                lindex ++;
                correct = correct +1;
            }
            else {
                lindex ++;
                incorrect = incorrect + 1;
            }
            done = index >= melody.size() || lindex >= listened.size();
        }
        double per = correct / listened.size();
        double percentage = per * 100;
        return percentage;
    }

    public static void main(String[] args) {
        BasicMatcher m=new BasicMatcher();
        List<Note> melody=new ArrayList<Note>();
        List<Sound> l=new ArrayList<Sound>();
        melody.add(new Note("C4", 4));
        melody.add(new Note("D4", 4));
        melody.add(new Note("E4", 4));
        melody.add(new Note("F4", 4));
        l.add(new Sound("C4", 0, 1));
        m.matchpattern(melody,0,l);
        System.out.println (m.matchpattern2(melody,0,l));
        System.out.println (m.matchpattern(melody,0,l));
        System.out.println(m.locator(melody, l));
    }

    public int locator(List<Note> melody, List<Sound> listened){
        int idnex = 0;
        int highestindex=-1;
        double highestscore=0;
        Sound lastheard=listened.get(listened.size()-1);
        Note expected = melody.get(current+1);
        if (expected.equals( lastheard)){
            current++;
        }
        //for (idnex=0; idnex<melody.size(); idnex++){
          //  double score = matchpattern2(melody, idnex,listened);
            //if (score > highestscore){
              //  highestscore = score;
                //highestindex = idnex;
            //}//

       // if (highestindex!=-1){
         //   System.out.println(listened);
           // System.out.println(highestindex);
            //S//ystem.out.println(highestscore);
        
        return current;
    
    } 
}
