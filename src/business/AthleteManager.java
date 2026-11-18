package business;

import persistence.AthleteJSONDAO;

import java.time.LocalDate;
import java.util.List;

public class AthleteManager {

    private AthleteJSONDAO dao;

    public AthleteManager(AthleteJSONDAO dao) {
        this.dao = dao;
    }

    public LocalDate validarFechaNacimiento(String texto) {
        // TODO
        return null;
    }

    public Profile perfilAtleta(int opcion) {
        // TODO
        return null;
    }

    public Athlete guardarAtleta(String nombre, LocalDate fechaNacimiento, String codigoDelegacion, Profile perfil) {
        // TODO
        return null;
    }

    public List<Athlete> buscarAtletas(String texto) {
        // TODO
        return null;
    }

    public List<Athlete> atletasDeDelegacion(String codigoDelegacion) {
        // TODO
        return null;
    }

    public void borrarAtleta(String idAtleta) {
        // TODO
    }

    public Athlete buscarPorId(String idAtleta) {
        // TODO
        return null;
    }
}
