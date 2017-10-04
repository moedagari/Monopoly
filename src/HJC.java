
import java.util.ArrayList;


public class HJC implements Bot {
	
	// The public API of YourTeamName must not change
	// You cannot change any other classes
	// YourTeamName may not alter the state of the board or the player objects
	// It may only inspect the state of the board and the player objects
  //build houses in three , build seven steps ahead of opponents, best areas are orange, red, pink
  
	ColourGroup d = null;
	BoardAPI board;
	PlayerAPI player;
	DiceAPI dice;
	boolean rollDone;
	boolean doubles;
	int doubleCount = 0;
	int jailCount = 0;
	boolean MortgageNeeded = false;
	boolean redeemCheck = true;
	
	HJC (BoardAPI board, PlayerAPI player, DiceAPI dice) {
		this.board = board;
		this.player = player;
		this.dice = dice;
		rollDone = false;
		doubles = false;
		return;
	}
	
	public String getName () {
		return "HJC";
	}
	
	public String getCommand () {
		doubles = false;
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
		if(dice.isDouble() && doubleCount<3){//only add doubles if the player has less than 3 double counts, preventing the bot from rolling again while in jail
			doubles = true;
			doubleCount++;
		}
		if(!player.isInJail()){//reset the jail turn counter
			jailCount =0;
		}
		if(player.isInJail()){//if the player is in jail set doubles to false so he doesnt roll again
			doubles = true;
			rollDone = false;
			if(player.hasGetOutOfJailCard()){//if bot has a get out of jail card , use it
				return "card";
			}
			else if(player.getBalance() > 50){//or else if he has a decent amount of money pay the bail
				return "pay";
			}
			else if(player.getBalance() <= 50){
				return checkToMortgage();
			}
			else if(!rollDone){
				return"roll";
			}
			else{//end your turn and increase the jailcount by one if youre still in jail
				jailCount++;
				return "done";
			}
		}

	else if(!player.isInJail()){	
		Square square = board.getSquare(player.getPosition());
		
		if (square instanceof Property && ((Property) square).isOwned() && !((Property) square).getOwner().equals(player) ) {
			if(player.getBalance()< 0 && !MortgageNeeded){
				MortgageNeeded = true;
				return "balance";
			}
		}	
		
		if ((rollDone || doubles) && square instanceof Property && !(((Property) square).isOwned())){
			Property property = (Property) board.getSquare(player.getPosition());
			if(player.getBalance() >= property.getPrice()){
				doubleCount--; 
				return "buy";
			}
			else if(rollDone == false || doubles){
				rollDone = true;
				return "roll";
			}
			else{
				rollDone = false;
				doubleCount = 0;
				return "done";
			}
		}
		
		if(player.getBalance() > 200 && redeemCheck){ //check if you have any properties to redeem/unmortgage
			ArrayList<Property> propertyList = player.getProperties();
			ArrayList<Property> MortgagedProperties = new ArrayList<Property>() ;
			
			for(Property p : propertyList){
				if (p.isMortgaged()){
					MortgagedProperties.add(p);
				}
			}
			if(!MortgagedProperties.isEmpty()){
				rollDone = false;
				return checkToRedeem(MortgagedProperties);
			}
			
		}
		
		if(player.getBalance() < 0 || MortgageNeeded){
			rollDone = false;
			MortgageNeeded = true;
			return checkToMortgage();
		}
		
		
		else if (rollDone == false || (doubles && player.getBalance() > 0)){
			rollDone = true;
			return "roll";
		}
		else if(!doubles){
			rollDone = false;
			doubleCount = 0;
			return checkToBuild();
		}
		else{
			return "done";
		}
	}
	else{
		return "done";
	}
	
	}
  
	private String checkToRedeem(ArrayList<Property> mortgagedProperties){
		Property temp = mortgagedProperties.get(0);
		for(Property p : mortgagedProperties){ //redeem most expensive property you can afford to 
			if((p.getMortgageRemptionPrice() <= player.getBalance()) && (p.getMortgageRemptionPrice()>temp.getMortgageValue()) ){
				temp = p;
			}
		}
		if((temp.getMortgageRemptionPrice() <= player.getBalance())){
			return "redeem " + temp.getShortName();
		}
		else{ //can't afford to redeem any at this time
			redeemCheck = false;
			return "balance";
		}
			
	}
	
	private String checkToMortgage() {
		ArrayList<Property> propertyList = player.getProperties();
		ArrayList<Property> unMortgagedProperties = new ArrayList<Property>() ;
		if (propertyList.isEmpty()){ //if you have no properties to mortgage you must go bankrupt
			return "bankrupt";
		}
		else{
			for(Property p : propertyList){
				if (!p.isMortgaged()){
					if ((p instanceof Site) && (((Site) p).getNumBuildings() > 0)){
						checkToDemolish((Site) p); //check if there are buildings on it that you need to demolish first
					}
					else{
						unMortgagedProperties.add(p); //otherwise add to unmortgaged properties arraylist
					}
				}
			}
			if(unMortgagedProperties.isEmpty()){
				return "bankrupt"; //if all your properties are already mortgaged, declare bankruptcy
			}
			else{
				Property temp = unMortgagedProperties.get(0);
				for(Property p : unMortgagedProperties){ 
					if((p.getMortgageValue() >= player.getBalance()) && (p.getMortgageValue()<temp.getMortgageValue()) ){
						temp = p;
					}
				}
			
				MortgageNeeded = false;	
				return ("mortgage " + temp.getShortName());
			}
	
		}
		
	}

	private String checkToBuild() {
		ArrayList<Property> propertyList = player.getProperties();
		for(Property p : propertyList){
			if (p instanceof Site) {
				Site site = (Site) p;
				if(player.getBalance() > (3*site.getBuildingPrice()) && site.canBuild(1)){// if you have more than the price + 70 just to leave some dollars on the side
					if (player.isGroupOwner(site)) {
						if (!site.isMortgaged()) {
							return "build " + p.getShortName() + " 1"; 
						}
					}
				}
			}
		}
		return "done";
	}

    private String checkToDemolish(Site t) {
		if(t.getNumBuildings()>=1){
			return "demolish " + t.getShortName() + " 1";

		}
		if(rollDone){
		rollDone = false;
		return "roll";
		}
		else{
			return "done";
		}
	}
    
    
	public String getDecision () {
    if(player.getBalance()<=10){
    	return "chance";
    }
    	return "pay";
	}
	
}