package business;

import persistence.CompetitionCSVDAO;
import persistence.CompetitionJSONDAO;

import java.util.List;

public class CompetitionManager {

    private CompetitionJSONDAO dao;
    private CompetitionCSVDAO csvDao;

    public CompetitionManager(CompetitionJSONDAO dao, CompetitionCSVDAO csvDao) {
        this.dao = dao;
        this.csvDao = csvDao;
    }

    public boolean hayCompeticiones() {
        // TODO
        return false;
    }

    public List<Competition> listarCompeticiones() {
        // TODO
        return null;
    }

    public List<String[]> leerCSV(String nombreFichero) {
        // TODO
        return null;
    }

    public List<String> validarCompeticion(String[] campos, List<Athlete> atletas) {
        // TODO
        return null;
    }

    public Competition guardarCompeticion(String[] campos) {
        // TODO
        return null;
    }

    public Results borrarCompeticion(String idCompeticion) {
        // TODO
        return null;
    }

    public List<Results> quitarAtletaDeCompeticiones(String idAtleta) {
        // TODO
        return null;
    }

    public void reiniciarResultados() {
        // TODO
    }

    public List<Performance> simularCompeticion(Competition competicion, List<Athlete> participantes) {
        // TODO
        return null;
    }
}
