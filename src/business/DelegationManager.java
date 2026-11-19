package business;

import persistence.DelegationJSONDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DelegationManager {

    //solo guardamos el dao, las listas se piden al fichero cada vez que hacen falta
    private DelegationJSONDAO dao;

    public DelegationManager(DelegationJSONDAO dao) {
        this.dao = dao;
    }

    /**
     * Comprueba que el fichero de delegaciones existe y es correcto.
     * @return true si el programa puede arrancar
     */
    public boolean comprobarFichero() {
        //se lo preguntamos al dao, que es quien conoce el fichero
        return dao.existeFichero();
    }

    /**
     * Devuelve todas las delegaciones ordenadas alfabéticamente por nombre.
     * @return lista de delegaciones
     */
    public List<Delegation> listarDelegaciones() {
        //pedimos las delegaciones al fichero
        List<Delegation> delegations = dao.cargarDelegaciones();
        //esta lista ira guardando las delegaciones ya ordenadas
        List<Delegation> sorted = new ArrayList<>();
        for (Delegation delegation : delegations) {
            //buscamos su posicion
            int position = 0;
            while (position < sorted.size()
                    && sorted.get(position).getName().compareToIgnoreCase(delegation.getName()) <= 0) {
                position++;
            }
            //la metemos en ese hueco
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
        //mismo metodo de ordenar que arriba, pero aqui el orden lo decide goesBefore
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
        //cargamos del fichero y buscamos con el metodo privado
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

        //primero se comprueba todo, para no dejar el podio a medias
        for (Map.Entry<Medal, String> entry : podio.entrySet()) {
            Delegation delegation = findByCode(delegations, entry.getValue());
            //si no existe la delegacion o no tiene esa medalla, paramos sin tocar nada
            if (delegation == null || !hasMedal(delegation, entry.getKey())) {
                return false;
            }
        }

        //si todo estaba bien, ahora si restamos cada medalla
        for (Map.Entry<Medal, String> entry : podio.entrySet()) {
            findByCode(delegations, entry.getValue()).removeMedal(entry.getKey());
        }
        //guardamos la lista entera ya modificada
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
            //si el codigo no existe simplemente se ignora
            if (delegation != null) {
                delegation.addMedal(entry.getKey());
            }
        }
        //al terminar guardamos los cambios en el fichero
        dao.guardarDelegaciones(delegations);
    }

    /**
     * Pone a cero las medallas de todas las delegaciones.
     */
    public void reiniciarMedallas() {
        List<Delegation> delegations = dao.cargarDelegaciones();
        //ponemos a cero cada delegacion y luego guardamos todo de una vez
        for (Delegation delegation : delegations) {
            delegation.resetMedals();
        }
        dao.guardarDelegaciones(delegations);
    }

    //recorre la lista y devuelve la que tenga ese codigo (null si no esta)
    private Delegation findByCode(List<Delegation> delegations, String code) {
        for (Delegation delegation : delegations) {
            if (delegation.getCode().equals(code)) {
                return delegation;
            }
        }
        return null;
    }

    //mira si la delegacion tiene al menos una medalla de ese tipo
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

    //decide si a va antes que b en el medallero
    private boolean goesBefore(Delegation a, Delegation b) {
        //si los oros son distintos, va primero quien tiene mas
        if (a.getGoldMedals() != b.getGoldMedals()) {
            return a.getGoldMedals() > b.getGoldMedals();
        }
        //empate en oros: miramos las platas
        if (a.getSilverMedals() != b.getSilverMedals()) {
            return a.getSilverMedals() > b.getSilverMedals();
        }
        //empate tambien en platas: miramos los bronces
        if (a.getBronzeMedals() != b.getBronzeMedals()) {
            return a.getBronzeMedals() > b.getBronzeMedals();
        }
        //si empatan en todo, desempata el orden alfabetico del nombre
        return a.getName().compareToIgnoreCase(b.getName()) < 0;
    }
}
