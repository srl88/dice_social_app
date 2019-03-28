package com.example.mobileliarsdice.Game;

import java.util.ArrayList;

public class Player {
    // Attributes
    private String name;

    // Constructor
    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "name: " + name;
    }

    public String computeHand(ArrayList<Cup> cups, int index, int numberOfPlayers, ArrayList<String> bids) {
        int[] dieCounts = new int[7];

        for(int i=0;i<cups.get(index).getDiceNumber();i++) {
            dieCounts[cups.get(index).getCup().get(i).getFace()]++;
        }

        for(int i=2; i<7; i++)
            dieCounts[i] += dieCounts[1];

        if(bids.size() > 0)
        {
            String[] lastBid = bids.get(bids.size() - 1).split(" ");
            int bidCount = Integer.valueOf(lastBid[0]);
            int bidDie = Integer.valueOf(lastBid[1]);

            // Check if I hold a better bid
            boolean betterBid = false;
            int myBidCount;
            int myHighestCount = 0;

            myHighestCount = dieCounts[bidDie];

            if((myHighestCount > bidCount) || (myHighestCount == bidCount)) {
                betterBid = true;
                myBidCount = myHighestCount;
            }

            if(betterBid == false) {
                int myDiceNeeded = bidCount - myHighestCount;
                int unknownDice = 0;
                int diceInPlay =0;

                for (int i = 0; i < cups.size(); i++) {
                    diceInPlay += cups.get(i).getDiceNumber();
                }
                unknownDice = diceInPlay - cups.get(index).getDiceNumber();

                int previousBidder = index - 1;

                if (previousBidder < 0) {
                    previousBidder = numberOfPlayers -1;
                }

                int bidderDiceNeeded = bidCount - dieCounts[bidDie] - (int)(cups.get(previousBidder).getDiceNumber()/3 +1);
                int bidderUnknown = diceInPlay - cups.get(previousBidder).getDiceNumber() - cups.get(index).getDiceNumber();
                int nextBidder = index + 1;

                if(nextBidder == numberOfPlayers) {
                    nextBidder = 0;
                }

                int nbDiceNeeded = myDiceNeeded - (int)(cups.get(nextBidder).getDiceNumber()/3 +1);
                int nbUnknown = diceInPlay - cups.get(nextBidder).getDiceNumber();

                double myChances = 1 - cumulBinomialProbability(unknownDice, myDiceNeeded -1);
                double bidderChances;

                if(bidderDiceNeeded > 0)
                    bidderChances = 1- cumulBinomialProbability(bidderUnknown, bidderDiceNeeded -1);

                else bidderChances = 1.0;

                double nbChances;
                if(nbDiceNeeded > 0)
                    nbChances = 1- cumulBinomialProbability(nbUnknown, nbDiceNeeded -1 );

                else nbChances = 1.0;

                if(((myChances < .5) && (nbChances <.5)) || (bidderChances < .2))
                    return "Liar!";
            }

            return (bidCount+1) + " " + bidDie;
        }

        return 2 + " " + 2;

    }

    private double cumulBinomialProbability(int n, int k) {
        double sum = 0;
        for(int i = 0; i <=k; i++)
            sum += binomialProbability(n, i);
        return sum;
    }

    private double binomialProbability(int n, int k) {
        double nfact = 1;
        double dfact = 1;
        int greater;
        int lesser;
        if((n-k) > k) {
            greater = n - k;
            lesser = k;
        }
        else {
            greater = k;
            lesser = n-k;
        }
        for(int i = greater+1; i <= n; i++)
            nfact = nfact * i;
        for(int i = 2; i <= lesser; i++)
            dfact = dfact * i;
        return (nfact/dfact)*(Math.pow((1.0/3), k))*Math.pow(2.0/3, (n-k));
    }

}