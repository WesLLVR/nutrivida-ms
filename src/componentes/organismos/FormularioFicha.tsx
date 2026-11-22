import { useState } from "react";
import CampoFormulario from "../moleculas/CampoFormulario";
import CampoSelect from "../moleculas/CampoSelect";
import CampoArea from "../moleculas/CampoArea";
import MensajeError from "../atomos/MensajeError";
import { fechaDeHoy } from "../../datos/citas";
import type { DatosFicha, Ficha } from "../../api/fichas";

interface FormularioFichaProps {
    pacientes: { id: number; nombre: string }[];
    fichaEditar: Ficha | null;
    alGuardar: (datos: DatosFicha) => void;
    alCancelar: () => void;
}

function FormularioFicha({ pacientes, fichaEditar, alGuardar, alCancelar }: FormularioFichaProps) {
    const esNueva = fichaEditar === null;

    const [paciente, setPaciente] = useState(fichaEditar ? fichaEditar.paciente : "");
    const [fecha, setFecha] = useState(fichaEditar ? fichaEditar.fecha : fechaDeHoy());
    const [peso, setPeso] = useState(fichaEditar ? String(fichaEditar.peso) : "");
    const [talla, setTalla] = useState(fichaEditar ? String(fichaEditar.talla) : "");
    const [cintura, setCintura] = useState(fichaEditar && fichaEditar.cintura !== null ? String(fichaEditar.cintura) : "");
    const [observaciones, setObservaciones] = useState(fichaEditar ? fichaEditar.observaciones : "");
    const [plan, setPlan] = useState(fichaEditar ? fichaEditar.planAlimenticio : "");
    const [error, setError] = useState("");

    const manejarEnvio = (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();

        const elegido = pacientes.find(p => p.nombre === paciente);

        if (esNueva && elegido === undefined) {
            setError("Seleccione un paciente");
            return;
        }
        if (fecha === "") {
            setError("Seleccione la fecha de la medición");
            return;
        }
        if (isNaN(Number(peso)) || Number(peso) < 2 || Number(peso) > 300) {
            setError("El peso debe estar entre 2 y 300 kg");
            return;
        }
        if (isNaN(Number(talla)) || Number(talla) < 0.4 || Number(talla) > 2.5) {
            setError("La talla debe estar en metros, entre 0.40 y 2.50");
            return;
        }
        if (cintura !== "" && isNaN(Number(cintura))) {
            setError("La cintura debe ser un número en centímetros");
            return;
        }
        if (plan.trim() === "") {
            setError("Escriba el plan alimenticio");
            return;
        }

        setError("");
        alGuardar({
            pacienteId: elegido ? elegido.id : fichaEditar!.pacienteId,
            fecha: fecha,
            peso: Number(peso),
            talla: Number(talla),
            cintura: cintura === "" ? null : Number(cintura),
            observaciones: observaciones.trim(),
            planAlimenticio: plan.trim()
        });
    };

    return (
        <form onSubmit={manejarEnvio} noValidate>
            <h2 className="h5">{esNueva ? "Nueva medición" : "Editar medición"}</h2>

            {esNueva ? (
                <CampoSelect etiqueta="Paciente" nombre="paciente" valor={paciente} opciones={pacientes.map(p => p.nombre)} alCambiar={setPaciente} />
            ) : (
                <p>Paciente: <strong>{paciente}</strong></p>
            )}

            <CampoFormulario etiqueta="Fecha" tipo="date" nombre="fecha" valor={fecha} alCambiar={setFecha} />

            <div className="row">
                <div className="col-4">
                    <CampoFormulario etiqueta="Peso (kg)" tipo="number" nombre="peso" valor={peso} alCambiar={setPeso} />
                </div>
                <div className="col-4">
                    <CampoFormulario etiqueta="Talla (m)" tipo="number" nombre="talla" valor={talla} alCambiar={setTalla} />
                </div>
                <div className="col-4">
                    <CampoFormulario etiqueta="Cintura (cm)" tipo="number" nombre="cintura" valor={cintura} alCambiar={setCintura} />
                </div>
            </div>

            <CampoArea etiqueta="Observaciones" nombre="observaciones" valor={observaciones} alCambiar={setObservaciones} />
            <CampoArea etiqueta="Plan alimenticio" nombre="plan" valor={plan} alCambiar={setPlan} />

            <MensajeError mensaje={error} />

            <button type="submit" className="btn btn-success me-2">
                Guardar
            </button>

            <button type="button" className="btn btn-secondary" onClick={alCancelar}>
                Cancelar
            </button>
        </form>
    );
}

export default FormularioFicha;
