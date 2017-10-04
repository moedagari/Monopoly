import java.util.ArrayList;

public class lagosBoys implements Bot {

	// The public API of YourTeamName must not change
	// You cannot change any other classes
	// YourTeamName may not alter the state of the board or the player objects
	// It may only inspect the state of the board and the player objects

	private BoardAPI board;
	private PlayerAPI player;
	private DiceAPI dice;
	private boolean paid = false, usedCard = false;
	private String command;
	private boolean checkForMonopoly = false;
	private boolean rolled = false;
	private int numberOfTimesGoneRound = 0;
	private int jailCount = 0;
	boolean bankrupt = false;

	ArrayList<ColourGroup> frequentlyLandedOn = new ArrayList<ColourGroup>();//Stores frequently landed on properties

	lagosBoys (BoardAPI board, PlayerAPI player, DiceAPI dice) {
		this.board = board;
		this.player = player;
		this.dice = dice;
		frequentlyLandedOn.add(new ColourGroup("pink"));
		frequentlyLandedOn.add(new ColourGroup("orange"));
		frequentlyLandedOn.add(new ColourGroup("red"));
		frequentlyLandedOn.add(new ColourGroup("yellow"));
		frequentlyLandedOn.add(new ColourGroup("green"));
		return;
	}

	public String getName () {
		return "lagosBoys";
	}

	public String getCommand () {
		
		try {
			Thread.sleep(1); // Controls the speed of the game
		} 
		catch (InterruptedException exception) {
			exception.printStackTrace();
		}

		ArrayList<Site> playerSites = new ArrayList<Site>();
		ArrayList<Site> sitesWithMonopoly = new ArrayList<Site>();
		ArrayList<Station> playerStations = new ArrayList<Station>();
		ArrayList<Utility> playerUtilities = new ArrayList<Utility>();
		ArrayList<Property> mortgagedProperties = new ArrayList<Property>();


		//Early Stages of the game
		if(numberOfTimesGoneRound<=4){

			//Check if bankrupt and mortgage properties until not bankrupt, utilities, sites and stations are mortgaged in that order
			if(player.getBalance() < 0){

				bankrupt = true;
				for (Property property: player.getProperties()) {


					if(property instanceof Site){
						playerSites.add((Site) property);
					}

					if(property instanceof Station){
						playerStations.add((Station) property);
					}

					if(property instanceof Utility){
						playerUtilities.add((Utility) property);
					}

				}



				for (Utility utility: playerUtilities) {

					if(!utility.isMortgaged()){
						command = "mortgage";
						return command + " " + utility.getShortName();
					}
					if(player.getBalance()>100) break;
				}



				if(player.getBalance() < 0 || player.getBalance()<100){

					for (Site site: playerSites) {

						if(!site.isMortgaged() && !isFrequentlyLandedOn(site)){

							while(site.canDemolish(1) && player.getBalance()<100){
								command = "demolish";
								return command + " " + site.getShortName() + " " + 1; 
							}

							if(player.getBalance()>100) break;

							command = "mortgage";
							return command + " " + site.getShortName();
						}

					}

				}


				if(player.getBalance() < 0 || player.getBalance()<100){

					for (Site site: playerSites) {
						if(!site.isMortgaged()){

							while(site.canDemolish(1) && player.getBalance()<100){
								command = "demolish";
								return command + " " + site.getShortName() + " " + 1; 
							}

							if(player.getBalance()>100) break;

							command = "mortgage";
							return command + " " + site.getShortName();
						}
					}

				}


				if(player.getBalance() < 0){

					for (Station station: playerStations) {

						if(!station.isMortgaged() && player.getBalance()<100){
							command = "mortgage";
							return command + " " + station.getShortName();

						}
						if(player.getBalance()>100) break;
					}

				}



			}


			//use card or pay immediately at early stages
			if(player.isInJail()){
				if(player.hasGetOutOfJailCard()){
					command = "card";
					usedCard = true;
					return command;
				}

				else if(player.getBalance() - 50 > 200){
					command = "pay";
					paid = true;
					return command;
				}

				else{
					if(!rolled){
						command = "roll";
						rolled = true;

						return command;
					}else{

						command = "done";
						rolled = false;
						return command;

					}
				}
			}


			boolean rolledToLeaveJail = !paid && !usedCard ;			
			usedCard = paid = false;



			if(!rolled){

				command = "roll";
				rolled = true;
				return command;

			}

			//Buy properties if the purchase won't leave you almost bankrupt
			if( (board.getSquare(player.getPosition()) instanceof Property) && !board.getProperty(player.getPosition()).isOwned() ){

				if( player.getBalance() - board.getProperty(player.getPosition()).getPrice() > 100){

					command = "buy";
					return command;

				}
			}


			if(player.passedGo()) numberOfTimesGoneRound++;//Keeps track of number of times you went round the board




			if(player.getBalance()<0){
				command = "bankrupt";
				return command;
			}

			if(dice.isDouble() && !rolledToLeaveJail){
				command = "roll";
				return command;
			}

			else if(rolled){
				command = "done";
				rolled = false;
				return command;
			}


		}
		
		
		
		//Late stages of game
		else{

			if(numberOfTimesGoneRound==15) return "quit";
			
			//Check if bankrupt and mortgage properties until not bankrupt, utilities, sites and stations are mortgaged in that order
			if(player.getBalance() < 0){

				bankrupt = true;
				for (Property property: player.getProperties()) {


					if(property instanceof Site){
						playerSites.add((Site) property);
					}

					if(property instanceof Station){
						playerStations.add((Station) property);
					}

					if(property instanceof Utility){
						playerUtilities.add((Utility) property);
					}

				}



				for (Utility utility: playerUtilities) {

					if(!utility.isMortgaged()){
						command = "mortgage";
						return command + " " + utility.getShortName();
					}
					if(player.getBalance()>100) break;
				}



				if(player.getBalance() < 0 || player.getBalance()<100){

					for (Site site: playerSites) {

						if(!site.isMortgaged() && !isFrequentlyLandedOn(site)){

							while(site.canDemolish(1) && player.getBalance()<100){
								command = "demolish";
								return command + " " + site.getShortName() + " " + 1; 
							}

							if(player.getBalance()>100) break;

							command = "mortgage";
							return command + " " + site.getShortName();
						}

					}

				}


				if(player.getBalance() < 0 || player.getBalance()<100){

					for (Site site: playerSites) {
						if(!site.isMortgaged()){

							while(site.canDemolish(1) && player.getBalance()<100){
								command = "demolish";
								return command + " " + site.getShortName() + " " + 1; 
							}

							if(player.getBalance()>100) break;

							command = "mortgage";
							return command + " " + site.getShortName();
						}
					}

				}


				if(player.getBalance() < 0){

					for (Station station: playerStations) {

						if(!station.isMortgaged() && player.getBalance()<100){
							command = "mortgage";
							return command + " " + station.getShortName();

						}
						if(player.getBalance()>100) break;
					}

				}

			}

			//Stay in jail as long as possible
			if(player.isInJail()){

				if(player.hasGetOutOfJailCard()  && jailCount ==1){
					command = "card";
					usedCard = true;
					return command;
				}

				else if(!rolled){
					command = "roll";
					rolled = true;
					jailCount++;

					return command;
				}else{

					command = "done";
					rolled = false;
					return command;

				}

			}

			boolean rolledToLeaveJail = !usedCard ;			
			usedCard = paid = false;

			if(!player.isInJail()){
				jailCount=0;
			}


			if(!rolled){

				command = "roll";
				rolled = true;
				return command;

			}


			if( (board.getSquare(player.getPosition()) instanceof Property) && !board.getProperty(player.getPosition()).isOwned() ){

				if( player.getBalance() - board.getProperty(player.getPosition()).getPrice() > 100){

					command = "buy";
					return command;

				}
			}


			if(player.passedGo()) numberOfTimesGoneRound++;




			//Build on properties with monopoly
			for (Property property: player.getProperties()) {

				if(property instanceof Site){

					if(player.isGroupOwner((Site) property)){
						checkForMonopoly = true;
						sitesWithMonopoly.add((Site) property);
					}

				}
			}

			if(checkForMonopoly){


				int propertiesBuiltOn = 0;
				for (Site site: sitesWithMonopoly) {

					if(isFrequentlyLandedOn(site)){
						while(!site.isMortgaged() && (site.canBuild(1) && (player.getBalance()-site.getBuildingPrice())>500)){
							command = "build";						
							return command + " " + site.getShortName() + " " + 1; 
						}
						propertiesBuiltOn++;
					}

				}

				if(propertiesBuiltOn<sitesWithMonopoly.size() ){
					for (Site site: sitesWithMonopoly) {
						if(!isFrequentlyLandedOn(site)){
							while(!site.isMortgaged() && (site.canBuild(1) && (player.getBalance()-site.getBuildingPrice())>500)){
								command = "build";
								return command + " " + site.getShortName() + " " + 1; 
							}
						}
					}
				}

			}

			boolean mortgagedProperty = false;
			for (Property property: player.getProperties()) {
				if(property.isMortgaged()){

					mortgagedProperties.add(property);
					mortgagedProperty = true;

				}
			}

			//Redeem mortgaged properties
			if(mortgagedProperty){

				for (Property property: mortgagedProperties) {

					if(player.getBalance() - property.getMortgageRemptionPrice()>200){

						command = "redeem";
						return command + " " + property.getShortName();

					}

				}

			}


			for (Property property: player.getProperties()) {

				if(!property.isMortgaged()){
					bankrupt = false;
					break;
				}
			}

			if(bankrupt){
				command = "bankrupt";
				return command;
			}

			if(dice.isDouble() && !rolledToLeaveJail){
				command = "roll";
				return command;
			}

			else if(rolled){
				command = "done";
				rolled = false;
				return command;
			}


		}



		return command;
	}

	
	public boolean isFrequentlyLandedOn(Site site) {

		boolean answer = false;

		for (ColourGroup Colorgroup: frequentlyLandedOn) {

			if(site.getColourGroup().getName().equals(Colorgroup.getName())){
				answer = true;
				return answer;
			}

		}


		return answer;
	}

	//Says chance unless the person has many houses or a hotel
	public String getDecision () {

		String decision = "chance";

		if(player.getNumHousesOwned()>1 || player.getNumHotelsOwned()>0){
			decision = "pay";
		}

		return decision;
	}


}
