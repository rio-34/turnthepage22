
//Written by Selen Serdar.
public class Sound {
    String notename;
	double timeStamp;
	double duration;
    
    public Sound (){
        notename="";
    }

    public Sound (String n, double t, double d){
        notename=n;
        timeStamp=t;
        duration=d;
    }

    public String toString(){
        return notename+" "+duration;
        
    }

    public static void main (String []args){
        Sound s = new Sound("c4",10,20);
        System.out.println(s);
    }   
}
