import { useEffect, useState } from "react";
import TablaServicios from "../componentes/organismos/TablaServicios";
import FormularioServicio from "../componentes/organismos/FormularioServicio";
import Boton from "../componentes/atomos/Boton";
import BotonEnlace from "../componentes/atomos/BotonEnlace";
import MensajeError from "../componentes/atomos/MensajeError";
import { actualizarServicio, crearServicio, eliminarServicio, listarServicios } from "../api/catalogo";
import type { DatosServicio } from "../api/catalogo";
import type { Servicio } from "../datos/servicios";

function AdminServicios() {
    const [servicios, setServicios] = useState<Servicio[]>([]);
    const [mostrarFormulario, setMostrarFormulario] = useState(false);
    const [servicioEditar, setServicioEditar] = useState<Servicio | null>(null);
    const [error, setError] = useState("");
    const [recargar, setRecargar] = useState(0);

    useEffect(() => {
        listarServicios().then(lista => setServicios(lista)).catch(err => setError(err.message));
    }, [recargar]);

    const nuevo = () => {
        setServicioEditar(null);
        setMostrarFormulario(true);
    };

    const editar = (codigo: string) => {
        const servicio = servicios.find(s => s.codigo === codigo);

        if (servicio !== undefined) {
            setServicioEditar(servicio);
            setMostrarFormulario(true);
        }
    };

    const guardar = async (datos: DatosServicio) => {
        try {
            if (servicioEditar === null) {
                await crearServicio(datos);
                alert("Servicio creado correctamente");
            } else {
                await actualizarServicio(servicioEditar.id, datos);
                alert("Servicio actualizado correctamente");
            }
            setError("");
            setMostrarFormulario(false);
            setRecargar(recargar + 1);
        } catch (err) {
            setError((err as Error).message);
        }
    };

    const eliminar = async (codigo: string) => {
        const servicio = servicios.find(s => s.codigo === codigo);

        if (servicio !== undefined && confirm("¿Seguro que desea eliminar el servicio " + codigo + "?")) {
            try {
                await eliminarServicio(servicio.id);
                setRecargar(recargar + 1);
            } catch (err) {
                setError((err as Error).message);
            }
        }
    };

    return (
        <div className="container">

            <h1>Servicios</h1>

            <div className="mb-3">
                <Boton texto="Nuevo servicio" color="success" alHacerClick={nuevo} />
                <BotonEnlace ruta="/admin" texto="Volver al panel" color="secondary" />
            </div>

            <MensajeError mensaje={error} />

            <div className="row g-4">
                <div className={mostrarFormulario ? "col-12 col-lg-7" : "col-12"}>
                    <TablaServicios servicios={servicios} alEditar={editar} alEliminar={eliminar} />
                </div>

                {mostrarFormulario && (
                    <div className="col-12 col-lg-5">
                        <div className="card p-3">
                            <FormularioServicio
                                key={servicioEditar ? servicioEditar.codigo : "nuevo"}
                                servicioEditar={servicioEditar}
                                alGuardar={guardar}
                                alCancelar={() => setMostrarFormulario(false)}
                            />
                        </div>
                    </div>
                )}
            </div>

        </div>
    );
}

export default AdminServicios;
