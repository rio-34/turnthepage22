import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

public class Note {
    String noteName;
    int ilength;
    JLabel label;

    public Note(){

    }
    public Note(String n, int i){
        noteName =n;
        ilength=i;
    }
    public String toString(){
        return ilength+noteName;
    }
    public boolean equals (Sound s){
        return Notes.getFreq(s.notename)==Notes.getFreq(noteName);
    }


    public static List<Note> parse (Reader input, List<Page> pages) throws IOException {
        String line;
        List<Note> list = new ArrayList<Note>();
        BufferedReader reader = new BufferedReader(input);
        Page page = new Page();
        do {
           line=reader.readLine();
           if (line!=null){
            String[] result = line.split("\\s");
            if (result.length==2){
                Note note = new Note();
                note.ilength = Integer.parseInt(result[0]);
                note.noteName=result[1];
                list.add(note);
                page.to++;
            } 
            else{
                pages.add(page);
                page = new Page();
                page.number = pages.size();
                page.from= list.size();
                page.to = page.from;
            }
           }
        }  while (line != null);
        if (page.to-page.from>0){
            pages.add(page);
        }
        return list;
    }
}