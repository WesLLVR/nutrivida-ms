import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import Boton from "../componentes/atomos/Boton";
import BotonEnlace from "../componentes/atomos/BotonEnlace";
import { buscarServicioPorCodigo } from "../api/catalogo";
import { formatearPrecio } from "../datos/servicios";
import type { Servicio } from "../datos/servicios";

interface DetalleProps {
    alAgregar: (servicio: Servicio) => void;
}

function DetalleServicio({ alAgregar }: DetalleProps) {
    const { codigo } = useParams();

    const [servicio, setServicio] = useState<Servicio | null>(null);
    const [noExiste, setNoExiste] = useState(false);

    useEffect(() => {
        buscarServicioPorCodigo(String(codigo))
            .then(encontrado => setServicio(encontrado))
            .catch(() => setNoExiste(true));
    }, [codigo]);

    if (noExiste) {
        return (
            <div className="container">
                <p>No encontramos ese servicio.</p>
                <BotonEnlace ruta="/servicios" texto="Volver a servicios" color="secondary" />
            </div>
        );
    }

    if (servicio === null) {
        return (
            <div className="container">
                <p>Cargando servicio...</p>
            </div>
        );
    }

    return (
        <div className="container">
            <div className="row g-4 align-items-center">

                <div className="col-12 col-md-6">
                    <img src={servicio.imagen} className="img-detalle rounded" alt={servicio.nombre} />
                </div>

                <div className="col-12 col-md-6">
                    <h1 className="h3">{servicio.nombre}</h1>
                    <p className="text-muted">{servicio.tipo.nombre}</p>
                    <p>{servicio.descripcion}</p>

                    <ul>
                        <li>Código: {servicio.codigo}</li>
                        <li>Duración: {servicio.duracion}</li>
                        <li>Modalidad: {servicio.modalidad}</li>
                        <li>Valor: {formatearPrecio(servicio.precio)}</li>
                    </ul>

                    <Boton texto="Agregar a mi reserva" color="success" alHacerClick={() => alAgregar(servicio)} />
                    <BotonEnlace ruta="/servicios" texto="Volver" color="secondary" />
                </div>

            </div>
        </div>
    );
}

export default DetalleServicio;
