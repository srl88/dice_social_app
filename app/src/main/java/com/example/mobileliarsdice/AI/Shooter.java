package com.example.mobileliarsdice.AI;

public class Shooter extends CPUPlayer{

    public String toString(){return "Straight Shooter Bot";}

    /**main function for bidding
     *
     * @param me
     * @param numDices
     * @param dice
     * @param bids
     * @return
     */
    @Override
    public String bid(int me, int[] numDices, int[] dice, String[] bids){

        int[] counts = new int[7];
        double[] expected = new double[7];
        int unknown = Controller.diceInPlay() - dice.length;

        //count his dices
        for(int i:dice)
            counts[i]++;
        //count expected
        for(int i=2;i<7;i++)
            expected[i] = counts[i] + unknown / 3d;

        int bidCount = 2;
        int bidDie = 2;

        //get last bids if any
        if(bids.length > 0){
            String[] lastBid = bids[bids.length-1].split(" ");
            bidCount = Integer.valueOf(lastBid[0]);
            bidDie = Integer.valueOf(lastBid[1])+1;
            int possible = Controller.diceInPlay();

            //count possibilites
            for(int i=2;i<7;i++)
                if(i != bidDie)
                    possible -= counts[i];

            //decide if lying
            if(bidCount > possible)
                return "Liar!";

            //liar dice cannot go higher than 6
            if(bidDie > 6){
                bidDie = 2;
                bidCount++;
            }
        }

        double best = Double.MAX_VALUE;
        int bestCount = bidCount;
        int bestDie = bidDie;

        //make a bid
        for(int count=bidCount;count<=Controller.diceInPlay();count++){
            for(int die=bidDie;die<7;die++){
                double score = Math.abs(expected[die]-bidCount);
                if(score < best){
                    best = score;
                    bestCount = count;
                    bestDie = die;
                }
            }
            bidDie = 2;
        }

        //return his choice
        return bestCount + " " + bestDie;
    }
}