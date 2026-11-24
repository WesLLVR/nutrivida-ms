import { useEffect, useState } from "react";
import TablaFichas from "../componentes/organismos/TablaFichas";
import TablaVentas from "../componentes/organismos/TablaVentas";
import MensajeError from "../componentes/atomos/MensajeError";
import { listarMisFichas } from "../api/fichas";
import type { Ficha } from "../api/fichas";
import { listarMisCompras } from "../api/ventas";
import type { Boleta } from "../datos/boletas";
import { formatearFecha } from "../datos/servicios";

function MiProgreso() {
    const [fichas, setFichas] = useState<Ficha[]>([]);
    const [compras, setCompras] = useState<Boleta[]>([]);
    const [error, setError] = useState("");

    useEffect(() => {
        listarMisFichas().then(lista => setFichas(lista)).catch(err => setError(err.message));
        listarMisCompras().then(lista => setCompras(lista)).catch(err => setError(err.message));
    }, []);

    const ultima = fichas.length > 0 ? fichas[0] : null;

    return (
        <div className="container">

            <h1>Mi progreso</h1>

            <MensajeError mensaje={error} />

            {ultima !== null && (
                <div className="card p-3 mb-4">
                    <h2 className="h5">Plan alimenticio vigente</h2>
                    <p className="text-muted mb-2">
                        Indicado por Nut. {ultima.nutricionista} el {formatearFecha(ultima.fecha)} - IMC actual: {ultima.imc}
                    </p>
                    <p className="mb-0">{ultima.planAlimenticio}</p>
                </div>
            )}

            <h2 className="h5">Historial de mediciones</h2>
            <TablaFichas fichas={fichas} mostrarPaciente={false} />

            <h2 className="h5 mt-4">Mis compras</h2>
            <TablaVentas ventas={compras} mostrarCliente={false} />

        </div>
    );
}

export default MiProgreso;
