import { useEffect, useState } from "react";
import { listarNutricionistas } from "../api/catalogo";
import type { Nutricionista } from "../api/catalogo";

function Nosotros() {
    const [nutricionistas, setNutricionistas] = useState<Nutricionista[]>([]);
    const [error, setError] = useState("");

    useEffect(() => {
        listarNutricionistas()
            .then(lista => setNutricionistas(lista))
            .catch(() => setError("No se pudo cargar el equipo de nutricionistas."));
    }, []);

    return (
        <div className="container">

            <h1>Nosotros</h1>

            <p>
                NutriVida abrió en 2016 en Temuco. Hoy somos cuatro nutricionistas y atendemos
                alrededor de sesenta pacientes por semana.
            </p>

            <div className="row g-4">
                <div className="col-12 col-lg-6">
                    <h2 className="h4">Nuestros nutricionistas</h2>

                    {error !== "" && <p className="text-muted">{error}</p>}

                    <ul className="list-group">
                        {nutricionistas.map(n => (
                            <li className="list-group-item" key={n.id}>
                                <strong>Nut. {n.nombre}</strong> - {n.especialidad}
                                <br />
                                <small className="text-muted">{n.diasAtencion}, {n.horario}</small>
                            </li>
                        ))}
                    </ul>
                </div>

                <div className="col-12 col-lg-6">
                    <h2 className="h4">Video de la clínica</h2>
                    <video className="w-100 rounded" controls poster="/video/portada-video.jpg">
                        <source src="/video/nutrivida-presentacion.mp4" type="video/mp4" />
                    </video>
                </div>
            </div>

            <p className="text-muted small mt-3 mb-0">
                Sitio desarrollado por Lucas Valderrama, Bastián González y Pedro Cid.
            </p>

        </div>
    );
}

export default Nosotros;
