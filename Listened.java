import java.util.ArrayList;
import java.util.List;

public class Listened {
    //list of sounds
    List<Sound> sounds;
    int limit;
    public String toString(){
        String a = "----\n";
        for (Sound s: sounds){
            a += s.toString() + "\n";
        
        }
        return a;
    }
    //constructor 
    public Listened (int l){
        sounds = new ArrayList<Sound>();
        limit =l;
    }
    public void newsound(Sound s){
            sounds.add(s);
            if (sounds.size()>limit){
                sounds.remove(0);
            }
        }
    

    public Sound getlast(){
        if (sounds.size()==0){
            return null;
        
        }
        else {
           return sounds.get(sounds.size()-1);
        }
        //returns the last sound
    }
}
