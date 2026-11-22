import { useState } from "react";
import CampoFormulario from "../moleculas/CampoFormulario";
import CampoSelect from "../moleculas/CampoSelect";
import MensajeError from "../atomos/MensajeError";
import { correoValido, runSinPuntos, runValido } from "../../validaciones/validaciones";
import { regiones, obtenerComunas, roles } from "../../datos/usuarios";
import type { DatosUsuario, Usuario } from "../../api/usuarios";

interface FormularioUsuarioProps {
    usuarioEditar: Usuario | null;
    alGuardar: (datos: DatosUsuario) => void;
    alCancelar: () => void;
}

function FormularioUsuario({ usuarioEditar, alGuardar, alCancelar }: FormularioUsuarioProps) {
    const esNuevo = usuarioEditar === null;

    const [run, setRun] = useState(usuarioEditar ? usuarioEditar.run : "");
    const [nombre, setNombre] = useState(usuarioEditar ? usuarioEditar.nombre : "");
    const [apellidos, setApellidos] = useState(usuarioEditar ? usuarioEditar.apellidos : "");
    const [correo, setCorreo] = useState(usuarioEditar ? usuarioEditar.correo : "");
    const [region, setRegion] = useState(usuarioEditar ? usuarioEditar.comuna.region.nombre : "");
    const [comuna, setComuna] = useState(usuarioEditar ? usuarioEditar.comuna.nombre : "");
    const [direccion, setDireccion] = useState(usuarioEditar ? usuarioEditar.direccion : "");
    const [rol, setRol] = useState(usuarioEditar ? usuarioEditar.rol.nombre : "");
    const [clave, setClave] = useState("");
    const [error, setError] = useState("");

    const manejarEnvio = (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (!runSinPuntos(run) || !runValido(run)) {
            setError("El RUN no es válido");
            return;
        }
        if (nombre.trim() === "" || apellidos.trim() === "") {
            setError("El nombre y los apellidos son obligatorios");
            return;
        }
        if (!correoValido(correo)) {
            setError("Ingrese un correo válido");
            return;
        }
        if (comuna === "" || direccion.trim() === "") {
            setError("Seleccione la comuna e ingrese la dirección");
            return;
        }
        if (rol === "") {
            setError("Seleccione el rol del usuario");
            return;
        }
        if (esNuevo && (clave.length < 4 || clave.length > 10)) {
            setError("La contraseña debe tener entre 4 y 10 caracteres");
            return;
        }

        setError("");
        alGuardar({
            run: run.trim().toUpperCase(),
            nombre: nombre.trim(),
            apellidos: apellidos.trim(),
            correo: correo.trim(),
            clave: clave,
            telefono: usuarioEditar ? usuarioEditar.telefono : "",
            comuna: comuna,
            direccion: direccion.trim(),
            rol: rol
        });
    };

    return (
        <form onSubmit={manejarEnvio} noValidate>
            <h2 className="h5">{esNuevo ? "Nuevo usuario" : "Editar usuario"}</h2>

            <CampoFormulario etiqueta="RUN" tipo="text" nombre="run" valor={run} alCambiar={setRun} />
            <CampoFormulario etiqueta="Nombre" tipo="text" nombre="nombre" valor={nombre} alCambiar={setNombre} />
            <CampoFormulario etiqueta="Apellidos" tipo="text" nombre="apellidos" valor={apellidos} alCambiar={setApellidos} />
            <CampoFormulario etiqueta="Correo" tipo="email" nombre="correo" valor={correo} alCambiar={setCorreo} />
            <CampoSelect
                etiqueta="Región"
                nombre="region"
                valor={region}
                opciones={regiones.map(r => r.nombre)}
                alCambiar={(valor) => { setRegion(valor); setComuna(""); }}
            />
            <CampoSelect etiqueta="Comuna" nombre="comuna" valor={comuna} opciones={obtenerComunas(region)} alCambiar={setComuna} />
            <CampoFormulario etiqueta="Dirección" tipo="text" nombre="direccion" valor={direccion} alCambiar={setDireccion} />
            <CampoSelect etiqueta="Rol" nombre="rol" valor={rol} opciones={roles} alCambiar={setRol} />
            <CampoFormulario
                etiqueta={esNuevo ? "Contraseña" : "Nueva contraseña (opcional)"}
                tipo="password"
                nombre="clave"
                valor={clave}
                alCambiar={setClave}
            />

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

export default FormularioUsuario;
