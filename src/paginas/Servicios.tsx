import { useEffect, useState } from "react";
import Buscador from "../componentes/moleculas/Buscador";
import ListaServicios from "../componentes/organismos/ListaServicios";
import MensajeError from "../componentes/atomos/MensajeError";
import { listarServicios } from "../api/catalogo";
import { filtrarServicios, tiposServicio } from "../datos/servicios";
import type { Servicio } from "../datos/servicios";

interface ServiciosProps {
    alAgregar: (servicio: Servicio) => void;
}

function Servicios({ alAgregar }: ServiciosProps) {
    const [servicios, setServicios] = useState<Servicio[]>([]);
    const [texto, setTexto] = useState("");
    const [tipo, setTipo] = useState("");
    const [error, setError] = useState("");
    const [cargando, setCargando] = useState(true);

    useEffect(() => {
        listarServicios()
            .then(lista => setServicios(lista))
            .catch(err => setError(err.message))
            .finally(() => setCargando(false));
    }, []);

    const serviciosFiltrados = filtrarServicios(servicios, texto, tipo);

    return (
        <div className="container">

            <h1>Servicios</h1>

            <Buscador
                texto={texto}
                tipo={tipo}
                tipos={tiposServicio}
                alCambiarTexto={setTexto}
                alCambiarTipo={setTipo}
            />

            <MensajeError mensaje={error} />

            {cargando ? (
                <p>Cargando servicios...</p>
            ) : (
                <ListaServicios servicios={serviciosFiltrados} alAgregar={alAgregar} />
            )}

        </div>
    );
}

export default Servicios;
