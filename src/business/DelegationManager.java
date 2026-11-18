package business;

import persistence.DelegationJSONDAO;

import java.util.List;
import java.util.Map;

public class DelegationManager {

    private DelegationJSONDAO dao;

    public DelegationManager(DelegationJSONDAO dao) {
        this.dao = dao;
    }

    public boolean comprobarFichero() {
        // TODO
        return false;
    }

    public List<Delegation> listarDelegaciones() {
        // TODO
        return null;
    }

    public List<Delegation> obtenerMedallero() {
        // TODO
        return null;
    }

    public Delegation buscarPorCodigo(String codigo) {
        // TODO
        return null;
    }

    public boolean restarMedallas(Map<Medal, String> podio) {
        // TODO
        return false;
    }

    public void sumarMedallas(Map<Medal, String> podio) {
        // TODO
    }

    public void reiniciarMedallas() {
        // TODO
    }
}
