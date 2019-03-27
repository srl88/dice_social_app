package main.java.com.example.mobileliarsdice.Game;

/**
 * Created by Philibert ADAM
 *
 *
 */

public class CPUPlayer extends Player {
    // Attributes
    private String name;
    private int token;

    // Constructor
    public CPUPlayer(String name, int token) {
        this.name = "cpu";
        this.token = token;
    }

    /**main function for bidding
     *
     * @param cup
     * @param index
     * @return n
     */
    public String computeHand(ArrayList<Cup> cups, int index, int numberOfPlayers, String[] bids) {
        int[] dieCounts = new int[7];

        for(int i=0;i<cups.get(index).getCup.getDiceNumber();i++) {
            dieCounts[cups.get(index).getCup().get(i).getFace()]++;
        }

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
                int myDiceNeeded = bidCount - myHighestCount;
                int unknownDice = 0;
                int diceInPlay =0;

                for (int i = 0; i < cups.size().; i++) {
                    diceInPlay += cups.get(i).getCup.getDiceNumber();
                }
                unknownDice = diceInPlay - cups.get(index).getCup.getDiceNumber();

                if (myHighDie <= bidDie) {
                    myDiceNeeded++;
                }

                int previousBidder = index - 1;

                if (previousBidder < 0) {
                    previousBidder = numberOfPlayers -1;
            }

                int bidderDiceNeeded = bidCount - dieCounts[bidDie] - (int)(cups.get(previousBidder).getCup.getDiceNumber()/3 +1);
                int bidderUnknown = diceInPlay - cups.get(previousBidder).getCup.getDiceNumber() -myDice.length;
                int nextBidder = index + 1;

                if(nextBidder == numberOfPlayers) {
                    nextBidder = 0;
                }

                int nbDiceNeeded = myDiceNeeded - (int)(cups.get(nextBidder).getCup.getDiceNumber()/3 +1);
                int nbUnknown = diceInPlay - cups.get(nextBidder).getCup.getDiceNumber();

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
