package com.example.mobileliarsdice.AI;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public class Controller {

    protected static Map<Class<?>,Long> w=new HashMap<Class<?>,Long>();
    public static long getScore(CPUPlayer p){
        return w.get(p.getClass());
    }

    private static void addPoints(CPUPlayer p,int n){
        Class<?> c=p.getClass();
        w.put(c, w.get(c)+n);
    }

    private Controller(){}
    private static List<List<Integer>> dice=new ArrayList<List<Integer>>();
    private static List<Integer> roll(int n){
        List<Integer> l=new ArrayList<Integer>();
        Random r=new Random((long)(Math.random()*Long.MAX_VALUE));
        for(int i=0;i<n;i++){
            l.add(1+r.nextInt(6));
        }
        return l;
    }

    public static int diceInPlay(){
        int n=0;
        for(List<Integer> l:dice)n+=l.size();
        return n;
    }

    private static List<CPUPlayer> p;
    private static int i;
    private static List<CPUPlayer> p(){
        List<CPUPlayer> p=new ArrayList<CPUPlayer>();
        p.add(new Nobody());
        return p;
    }

    private static List<CPUPlayer> getSubset(List<CPUPlayer> p,int i){
        List<CPUPlayer> q=new ArrayList<CPUPlayer>();
        while(i*p.size()>0){
            int j=(int)(Math.random()*p.size());
            q.add(p.remove(j));
            i--;
        }
        return q;
    }

    private static CPUPlayer currentPlayer(){
        return p.get(i);
    }

    public static void main(String[] a) throws FileNotFoundException{
        for(CPUPlayer k:p()){
            w.put(k.getClass(),0L);
        }

        for(int i=1;i<=1000;i++){System.out.print(i+":");main();}

        p=p();
        PrintStream o=new PrintStream("res.txt");
        for(CPUPlayer pl:p){
            o.println(pl+":"+getScore(pl));
        }
        o.close();
    }
    private static void main(){
        dice.clear();
        int t=0;
        p=getSubset(p(),3+(int)(3*Math.random()));
        System.out.print(p+" -");
        List<String> bids=new ArrayList<String>();

        for(int i=0;i<p.size();i++){
            dice.add(roll(5));
        }

        i=0;
        while(p.size()>1){
            t++;
            i%=p.size();
            addPoints(p.get(i),1);
            int[] d=new int[p.size()];

            for(int j=0;j<d.length;j++){
                d[j]=dice.get(j).size();
            }

            int[] md=new int[dice.get(i).size()];

            for(int j=0;j<md.length;j++){
                md[j]=dice.get(i).get(j);
            }

            String z=currentPlayer().bid(i, d, md, bids.toArray(new String[0]));

            if(z.equals("Liar!")){
                if(bids.size()==0){
                    dice.remove(i);
                    p.remove(i);
                    bids.clear();
                    i--;
                    if(i<0)i=p.size()-1;

                    for(int i=0;i<dice.size();i++){
                        dice.set(i, roll(dice.get(i).size()));
                    }
                    continue;
                }
                boolean bo=testBid(bids.get(bids.size()-1));
                bids.clear();
                if(bo){
                    dice.get(i).remove(dice.get(i).size()-1);
                    i--;
                    if(i<0)i=p.size()-1;
                    dice.get(i).add(1);
                    addPoints(p.get(i),10);
                }

                else{
                    i--;
                    if(i<0)i=p.size()-1;
                    dice.get(i).remove(dice.get(i).size()-1);
                    i++;i%=p.size();
                    dice.get(i).add(1);
                    addPoints(p.get(i),10);
                }

                for(int i=0;i<dice.size();i++){
                    dice.set(i, roll(dice.get(i).size()));
                }

                while(dice.indexOf(new ArrayList<Integer>())!=-1){
                    int i=dice.indexOf(new ArrayList<Integer>());
                    dice.remove(i);
                    p.remove(i);
                }

                continue;

            }
            else{
                boolean bo=isLegal(z)&&(bids.size()==0||isGreaterThan(z,bids.get(bids.size()-1)));
                if(bo){
                    bids.add(z);
                }

                else{
                    dice.remove(i);
                    p.remove(i);
                    bids.clear();
                    i--;
                    if(i<0)i=p.size()-1;
                    for(int i=0;i<dice.size();i++){
                        dice.set(i, roll(dice.get(i).size()));
                    }
                    continue;
                }
            }

            i++;
            i%=p.size();
            if(t>=5000){
                System.out.println("Round timed out.");
                return ;
            }
        }

        System.out.println(p.get(0)+" wins in "+t+" turns.");
        addPoints(p.get(0),1000);
    }

    private static boolean testBid(String string) {
        Scanner r=new Scanner(string);
        int x=r.nextInt(),y=r.nextInt();
        r.close();
        for(List<Integer> l:dice) for(int i:l) if(i==y||i==1)x--;
        return x<=0;
    }

    private static boolean isLegal(String string) {
        try{Scanner r=new Scanner(string);
            int x=r.nextInt(),y=r.nextInt();
            r.close();
            return 0<y&&0<x&&y<7;
        }
        catch(Exception e)
                {return false;}
    }

    public static String[] playerNames(){
        String[] s=new String[p.size()];
        for(int i=0;i<s.length;i++){
            s[i]=p.get(i).toString();
        }
        return s;
    }

    public static int valueOf(String i) {
        String[] j=i.split(" ");
        int y=Integer.parseInt(j[0]);
        int z=Integer.parseInt(j[1]);
        return y*6+z;
    }

    public static boolean isGreaterThan(String string1,String string2) {
        return valueOf(string1)>valueOf(string2);
    }

    public static int numPlayers(){
        return p.size();
    }
}