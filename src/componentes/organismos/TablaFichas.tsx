import Boton from "../atomos/Boton";
import { formatearFecha } from "../../datos/servicios";
import type { Ficha } from "../../api/fichas";

interface TablaFichasProps {
    fichas: Ficha[];
    mostrarPaciente: boolean;
    alEditar?: (ficha: Ficha) => void;
    alEliminar?: (id: number) => void;
}

function TablaFichas({ fichas, mostrarPaciente, alEditar, alEliminar }: TablaFichasProps) {
    if (fichas.length === 0) {
        return <p>Todavía no hay mediciones registradas.</p>;
    }

    const conAcciones = alEditar !== undefined || alEliminar !== undefined;

    return (
        <div className="table-responsive">
            <table className="table table-bordered align-middle">
                <thead>
                    <tr>
                        <th>Fecha</th>
                        {mostrarPaciente && <th>Paciente</th>}
                        <th>Peso</th>
                        <th>Talla</th>
                        <th>IMC</th>
                        <th>Cintura</th>
                        <th>Observaciones</th>
                        {conAcciones && <th>Acciones</th>}
                    </tr>
                </thead>
                <tbody>
                    {fichas.map(ficha => (
                        <tr key={ficha.id}>
                            <td>{formatearFecha(ficha.fecha)}</td>
                            {mostrarPaciente && <td>{ficha.paciente}</td>}
                            <td>{ficha.peso} kg</td>
                            <td>{ficha.talla} m</td>
                            <td>{ficha.imc}</td>
                            <td>{ficha.cintura === null ? "-" : ficha.cintura + " cm"}</td>
                            <td>{ficha.observaciones}</td>
                            {conAcciones && (
                                <td>
                                    {alEditar && <Boton texto="Editar" color="outline-primary" alHacerClick={() => alEditar(ficha)} />}
                                    {alEliminar && <Boton texto="Eliminar" color="outline-danger" alHacerClick={() => alEliminar(ficha.id)} />}
                                </td>
                            )}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default TablaFichas;
