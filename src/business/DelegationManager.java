package business;

import persistence.DelegationJSONDAO;
import persistence.PersistenceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * la logica de las delegaciones: ordena, busca y cambia medallas
 */
public class DelegationManager {

    //solo guardamos el dao, las listas se piden al fichero cada vez que hacen falta
    private DelegationJSONDAO dao;

    /**
     * guarda el dao con el que leera y escribira las delegaciones
     * @param dao el acceso al fichero de delegaciones
     */
    public DelegationManager(DelegationJSONDAO dao) {
        this.dao = dao;
    }

    /**
     * pregunta al dao si delegations.json esta bien
     * @return true si el programa puede arrancar
     */
    public boolean comprobarFichero() {
        return dao.existeFichero();
    }

    /**
     * da todas las delegaciones ordenadas por nombre, de la a a la z
     * @return la lista ordenada
     * @throws BusinessException si falla el fichero
     */
    public List<Delegation> listarDelegaciones() throws BusinessException {
        //pedimos las delegaciones al fichero
        List<Delegation> delegations = cargar();
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
     * da las delegaciones como un medallero: primero las que tienen mas oros, luego platas,
     * luego bronces y, si siguen empatadas, por nombre
     * @return la lista ordenada
     * @throws BusinessException si falla el fichero
     */
    public List<Delegation> obtenerMedallero() throws BusinessException {
        List<Delegation> delegations = cargar();
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
     * busca una delegacion a partir de su codigo
     * @param codigo codigo de la delegacion (DLG-XXX)
     * @return la delegacion
     * @throws BusinessException si no existe (NotFoundException) o si falla el fichero
     */
    public Delegation buscarPorCodigo(String codigo) throws BusinessException {
        try {
            Delegation delegation = dao.buscarPorCodigo(codigo);
            if (delegation == null) {
                throw new NotFoundException("The delegation \"" + codigo + "\" does not exist.");
            }
            return delegation;
        } catch (PersistenceException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * quita las medallas de un podio. si alguna no se puede quitar, no se quita ninguna
     * @param podio medalla y codigo de la delegacion que la tenia
     * @return true si se ha restado todo, false si no se ha podido
     * @throws BusinessException si falla el fichero
     */
    public boolean restarMedallas(Map<Medal, String> podio) throws BusinessException {
        List<Delegation> delegations = cargar();

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
        guardar(delegations);
        return true;
    }

    /**
     * anade cada medalla del podio a su delegacion y guarda los cambios
     * @param podio medalla y codigo de la delegacion que la gana
     * @throws BusinessException si falla el fichero
     */
    public void sumarMedallas(Map<Medal, String> podio) throws BusinessException {
        try {
            //solo se cargan las delegaciones del podio, no todas
            for (Map.Entry<Medal, String> entry : podio.entrySet()) {
                Delegation delegation = dao.buscarPorCodigo(entry.getValue());
                //si el codigo no existe simplemente se ignora
                if (delegation != null) {
                    delegation.addMedal(entry.getKey());
                    dao.actualizarDelegacion(delegation);
                }
            }
        } catch (PersistenceException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * deja a cero las medallas de todas las delegaciones
     * @throws BusinessException si falla el fichero
     */
    public void reiniciarMedallas() throws BusinessException {
        List<Delegation> delegations = cargar();
        for (Delegation delegation : delegations) {
            delegation.resetMedals();
        }
        guardar(delegations);
    }

    //pide la lista al dao y pasa el error de persistencia a error de negocio
    private List<Delegation> cargar() throws BusinessException {
        try {
            return dao.cargarDelegaciones();
        } catch (PersistenceException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    //lo mismo pero para guardar
    private void guardar(List<Delegation> delegations) throws BusinessException {
        try {
            dao.guardarDelegaciones(delegations);
        } catch (PersistenceException e) {
            throw new BusinessException(e.getMessage());
        }
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
