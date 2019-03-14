package com.example.mobileliarsdice.AI;

public class Badnomial extends CPUPlayer{

    public String toString() {return "Badnomial Bot";}

    /**main function for bidding
     *
     * @param myId
     * @param diceEachPlayerHas
     * @param myDice
     * @param bids
     * @return
     */
    @Override
    public String bid(int myId, int[] diceEachPlayerHas, int[] myDice, String[] bids) {

        int[] dieCounts = new int[7];

        for(int i:myDice)
            dieCounts[i]++;
        for(int i=2; i<7; i++)
            dieCounts[i] += dieCounts[1];

        if(bids.length > 0)
        {
            String[] lastBid = bids[bids.length - 1].split(" ");
            int bidCount = Integer.valueOf(lastBid[0]);
            int bidDie = Integer.valueOf(lastBid[1]);

            // Check if I hold a better bid
            boolean betterBid = false;
            int myBidDie;
            int myBidCount;
            int myHighestCount = 0;
            int myHighDie = bidDie +1;

            for(int i = 2; i < 7; i++) {
                if(dieCounts[i] >= myHighestCount) {
                    myHighestCount = dieCounts[i];
                    myHighDie = i;
                }
            }
            if((myHighestCount > bidCount) || ((myHighestCount == bidCount) && (myHighDie > bidDie))) {
                betterBid = true;
                myBidDie = myHighDie;
                myBidCount = myHighestCount;
            }

            if(betterBid == false) {
                int unknownDice = Controller.diceInPlay() - myDice.length;
                int myDiceNeeded = bidCount - myHighestCount;

                if(myHighDie <= bidDie)
                    myDiceNeeded++;

                int previousBidder = myId - 1;

                if(previousBidder < 0)
                    previousBidder = Controller.numPlayers() -1;

                int bidderDiceNeeded = bidCount - dieCounts[bidDie] - (int)(diceEachPlayerHas[previousBidder]/3 +1);
                int bidderUnknown = Controller.diceInPlay() - diceEachPlayerHas[previousBidder] -myDice.length;
                int nextBidder = myId + 1;

                if(nextBidder == Controller.numPlayers())
                    nextBidder = 0;

                int nbDiceNeeded = myDiceNeeded - (int)(diceEachPlayerHas[nextBidder]/3 +1);
                int nbUnknown = Controller.diceInPlay() - diceEachPlayerHas[nextBidder];
                //float myChances = (unknownDice/3 - myDiceNeeded)/((float)unknownDice/9);
                //float bidderChances = (bidderUnknown/3 - bidderDiceNeeded)/((float)bidderUnknown/9);
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

            return (bidCount+1) + " " + myHighDie;
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