package presentation;

import business.AthleteManager;
import business.CompetitionManager;
import business.DelegationManager;
import persistence.AthleteJSONDAO;
import persistence.CompetitionCSVDAO;
import persistence.CompetitionJSONDAO;
import persistence.DelegationJSONDAO;

public class Main {

    public static void main(String[] args) {
        //en el orden en el qu se usa que sino hay problemas cuando se compila y no funciona
        DelegationJSONDAO delegationDAO = new DelegationJSONDAO();
        DelegationManager delegationManager = new DelegationManager(delegationDAO);

        AthleteJSONDAO athleteDAO = new AthleteJSONDAO();
        AthleteManager athleteManager = new AthleteManager(athleteDAO);

        CompetitionJSONDAO competitionDAO = new CompetitionJSONDAO();
        CompetitionCSVDAO competitionCSVDAO = new CompetitionCSVDAO();
        CompetitionManager competitionManager = new CompetitionManager(competitionDAO, competitionCSVDAO);

        View view = new View();
        Controller controller = new Controller(view, delegationManager, athleteManager, competitionManager);

        //si los ficheros estan bien arranca pero si no el programa termina
        if (controller.comprobarFicheros()) {
            controller.run();
        }
    }
}
