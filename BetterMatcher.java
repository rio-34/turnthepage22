//Written by Selen Serdar.
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BetterMatcher implements Matcher {
    private int current = -1;

    public double matchpattern(List<Note> melody, int index, List<Sound> listened) {
        double correct = 0;
        double incorrect = 0;
        for (index = 0; index < listened.size(); index++) {
            Sound actual = listened.get(index);
            Note think = melody.get(index);

            if (actual.notename.equals(think.noteName)) {
                correct = correct + 1;
            } else {
                incorrect = incorrect + 1;
            }
        }
        double num = correct / melody.size();
        double percent = num * 100;
        return percent;
    }

    public double matchpattern2(List<Note> melody, int index, List<Sound> listened) {
        boolean done = false;
        double correct = 0;
        double incorrect = 0;
        int lindex = 0;
        while (!done) {
            Sound actual = listened.get(lindex);
            Note think = melody.get(index);
            if (actual.notename.equals(think.noteName)) {
                index++;
                lindex++;
                correct = correct + 1;
            } else {
                lindex++;
                incorrect = incorrect + 1;
            }
            done = index >= melody.size() || lindex >= listened.size();
        }
        double per = correct / listened.size();
        double percentage = per * 100;
        return percentage;
    }
//scan scans whole set of notes to find most lkely place. 
    public int scan (List<Note> melody, List<Sound> listened){
        int highestindex = -1;
        double highestscore = 0;
        int pos=current;
        if(pos<0) {
            pos=0;
        }
        for (int index = pos ;index<melody.size(); index++){
            double score = matchpattern2(melody, index, listened);
            if (score > highestscore) {
                highestscore = score;
                highestindex = index;
            }
        }
        for (int index = 0; index<pos && index<melody.size(); index++){
            double score = matchpattern2(melody, index, listened);
            if (score > highestscore) {
                highestscore = score;
                highestindex = index;
            }
        }
        return highestindex;
    }

    public int locator(List<Note> melody, List<Sound> listened) {

        Sound lastheard = listened.get(listened.size() - 1);
        if (current+1>= melody.size()){
            
            current = scan (melody, listened);
            return current;
        }
        Note expected = melody.get(current + 1);
        if (expected.equals(lastheard)) {
            current++;
        } else {

           //did not hear expected note. scan
           int highestindex = scan(melody, listened);
            if (highestindex!=-1){
                current= highestindex+listened.size();

            }
        }
        return current;

    }
}
