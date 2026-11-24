import { useEffect, useState } from "react";
import ListaServicios from "../componentes/organismos/ListaServicios";
import BotonEnlace from "../componentes/atomos/BotonEnlace";
import MensajeError from "../componentes/atomos/MensajeError";
import { listarServicios } from "../api/catalogo";
import type { Servicio } from "../datos/servicios";

interface InicioProps {
    alAgregar: (servicio: Servicio) => void;
}

function Inicio({ alAgregar }: InicioProps) {
    const [servicios, setServicios] = useState<Servicio[]>([]);
    const [error, setError] = useState("");

    useEffect(() => {
        listarServicios()
            .then(lista => setServicios(lista))
            .catch(err => setError(err.message));
    }, []);

    const destacados = servicios.filter(servicio => servicio.destacado);

    return (
        <div className="container">

            <div className="row align-items-center g-4 mb-4">
                <div className="col-12 col-md-6">
                    <h1>Bienvenido a NutriVida</h1>
                    <p>
                        Somos una clínica de nutrición y dietética de Temuco.
                        Cuatro nutricionistas atienden con planes hechos a la medida de cada persona.
                    </p>
                    <BotonEnlace ruta="/nosotros" texto="Conoce la clínica" color="success" />
                    <BotonEnlace ruta="/servicios" texto="Ver servicios" color="outline-success" />
                </div>

                <div className="col-12 col-md-6">
                    <img src="/img/nosotros.png" className="img-portada rounded" alt="Consulta nutricional" />
                </div>
            </div>

            <h2 className="h4 mb-3">Servicios destacados</h2>

            <MensajeError mensaje={error} />

            <ListaServicios servicios={destacados} alAgregar={alAgregar} />

        </div>
    );
}

export default Inicio;
