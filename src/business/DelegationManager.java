package business;

import persistence.DelegationJSONDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DelegationManager {

    private DelegationJSONDAO dao;

    public DelegationManager(DelegationJSONDAO dao) {
        this.dao = dao;
    }

    /**
     * Comprueba que el fichero de delegaciones existe y es correcto.
     * @return true si el programa puede arrancar
     */
    public boolean comprobarFichero() {
        return dao.existeFichero();
    }

    /**
     * Devuelve todas las delegaciones ordenadas alfabéticamente por nombre.
     * @return lista de delegaciones
     */
    public List<Delegation> listarDelegaciones() {
        List<Delegation> delegations = dao.cargarDelegaciones();
        List<Delegation> sorted = new ArrayList<>();
        for (Delegation delegation : delegations) {
            int position = 0;
            while (position < sorted.size()
                    && sorted.get(position).getName().compareToIgnoreCase(delegation.getName()) <= 0) {
                position++;
            }
            sorted.add(position, delegation);
        }
        return sorted;
    }

    /**
     * Devuelve las delegaciones ordenadas como un medallero:
     * más oros, luego más platas, luego más bronces y, si empatan, por nombre.
     * @return lista de delegaciones ordenada
     */
    public List<Delegation> obtenerMedallero() {
        List<Delegation> delegations = dao.cargarDelegaciones();
        List<Delegation> sorted = new ArrayList<>();
        for (Delegation delegation : delegations) {
            int position = 0;
            while (position < sorted.size() && !goesBefore(delegation, sorted.get(position))) {
                position++;
            }
            sorted.add(position, delegation);
        }
        return sorted;
    }

    /**
     * Busca una delegación por su código.
     * @param codigo código de la delegación (DLG-XXX)
     * @return la delegación, o null si no existe
     */
    public Delegation buscarPorCodigo(String codigo) {
        return findByCode(dao.cargarDelegaciones(), codigo);
    }

    /**
     * Resta las medallas de un podio. Si alguna delegación no existe o no tiene
     * esa medalla, no cambia nada.
     * @param podio medalla y código de la delegación que la tiene
     * @return true si se ha restado todo, false si no se ha podido
     */
    public boolean restarMedallas(Map<Medal, String> podio) {
        List<Delegation> delegations = dao.cargarDelegaciones();

        // Primero se comprueba todo, para no dejar el podio a medias
        for (Map.Entry<Medal, String> entry : podio.entrySet()) {
            Delegation delegation = findByCode(delegations, entry.getValue());
            if (delegation == null || !hasMedal(delegation, entry.getKey())) {
                return false;
            }
        }

        for (Map.Entry<Medal, String> entry : podio.entrySet()) {
            findByCode(delegations, entry.getValue()).removeMedal(entry.getKey());
        }
        dao.guardarDelegaciones(delegations);
        return true;
    }

    /**
     * Suma las medallas de un podio a las delegaciones indicadas.
     * @param podio medalla y código de la delegación que la gana
     */
    public void sumarMedallas(Map<Medal, String> podio) {
        List<Delegation> delegations = dao.cargarDelegaciones();
        for (Map.Entry<Medal, String> entry : podio.entrySet()) {
            Delegation delegation = findByCode(delegations, entry.getValue());
            if (delegation != null) {
                delegation.addMedal(entry.getKey());
            }
        }
        dao.guardarDelegaciones(delegations);
    }

    /**
     * Pone a cero las medallas de todas las delegaciones.
     */
    public void reiniciarMedallas() {
        List<Delegation> delegations = dao.cargarDelegaciones();
        for (Delegation delegation : delegations) {
            delegation.resetMedals();
        }
        dao.guardarDelegaciones(delegations);
    }

    private Delegation findByCode(List<Delegation> delegations, String code) {
        for (Delegation delegation : delegations) {
            if (delegation.getCode().equals(code)) {
                return delegation;
            }
        }
        return null;
    }

    private boolean hasMedal(Delegation delegation, Medal medal) {
        switch (medal) {
            case GOLD:
                return delegation.getGoldMedals() > 0;
            case SILVER:
                return delegation.getSilverMedals() > 0;
            default:
                return delegation.getBronzeMedals() > 0;
        }
    }

    // Decide si a va antes que b en el medallero
    private boolean goesBefore(Delegation a, Delegation b) {
        if (a.getGoldMedals() != b.getGoldMedals()) {
            return a.getGoldMedals() > b.getGoldMedals();
        }
        if (a.getSilverMedals() != b.getSilverMedals()) {
            return a.getSilverMedals() > b.getSilverMedals();
        }
        if (a.getBronzeMedals() != b.getBronzeMedals()) {
            return a.getBronzeMedals() > b.getBronzeMedals();
        }
        return a.getName().compareToIgnoreCase(b.getName()) < 0;
    }
}
