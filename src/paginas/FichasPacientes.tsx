import { useEffect, useState } from "react";
import FormularioFicha from "../componentes/organismos/FormularioFicha";
import TablaFichas from "../componentes/organismos/TablaFichas";
import Boton from "../componentes/atomos/Boton";
import MensajeError from "../componentes/atomos/MensajeError";
import { listarMisCitas } from "../api/citas";
import { actualizarFicha, crearFicha, eliminarFicha, listarMisFichas } from "../api/fichas";
import type { DatosFicha, Ficha } from "../api/fichas";

function FichasPacientes() {
    const [fichas, setFichas] = useState<Ficha[]>([]);
    const [pacientes, setPacientes] = useState<{ id: number; nombre: string }[]>([]);
    const [mostrarFormulario, setMostrarFormulario] = useState(false);
    const [fichaEditar, setFichaEditar] = useState<Ficha | null>(null);
    const [error, setError] = useState("");
    const [recargar, setRecargar] = useState(0);

    useEffect(() => {
        listarMisCitas()
            .then(citas => {
                const lista: { id: number; nombre: string }[] = [];
                citas.forEach(cita => {
                    if (!lista.some(p => p.id === cita.pacienteId)) {
                        lista.push({ id: cita.pacienteId, nombre: cita.paciente });
                    }
                });
                setPacientes(lista);
            })
            .catch(err => setError(err.message));
    }, []);

    useEffect(() => {
        listarMisFichas().then(lista => setFichas(lista)).catch(err => setError(err.message));
    }, [recargar]);

    const nueva = () => {
        setFichaEditar(null);
        setMostrarFormulario(true);
    };

    const editar = (ficha: Ficha) => {
        setFichaEditar(ficha);
        setMostrarFormulario(true);
    };

    const guardar = async (datos: DatosFicha) => {
        try {
            if (fichaEditar === null) {
                await crearFicha(datos);
            } else {
                await actualizarFicha(fichaEditar.id, datos);
            }
            alert("Ficha guardada correctamente");
            setMostrarFormulario(false);
            setError("");
            setRecargar(recargar + 1);
        } catch (err) {
            setError((err as Error).message);
        }
    };

    const eliminar = async (id: number) => {
        if (!confirm("¿Seguro que deseas eliminar esta medición?")) {
            return;
        }
        try {
            await eliminarFicha(id);
            setRecargar(recargar + 1);
        } catch (err) {
            setError((err as Error).message);
        }
    };

    return (
        <div className="container">

            <h1>Fichas de mis pacientes</h1>
            <p>Solo ves las mediciones que tú registraste. Los pacientes aparecen cuando tienen una cita contigo.</p>

            <div className="mb-3">
                <Boton texto="Nueva medición" color="success" alHacerClick={nueva} />
            </div>

            <MensajeError mensaje={error} />

            <div className="row g-4">
                <div className={mostrarFormulario ? "col-12 col-lg-7" : "col-12"}>
                    <TablaFichas fichas={fichas} mostrarPaciente={true} alEditar={editar} alEliminar={eliminar} />
                </div>

                {mostrarFormulario && (
                    <div className="col-12 col-lg-5">
                        <div className="card p-3">
                            <FormularioFicha
                                key={fichaEditar ? fichaEditar.id : "nueva"}
                                pacientes={pacientes}
                                fichaEditar={fichaEditar}
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

export default FichasPacientes;
