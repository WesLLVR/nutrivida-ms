import { useEffect, useState } from "react";
import TablaUsuarios from "../componentes/organismos/TablaUsuarios";
import FormularioUsuario from "../componentes/organismos/FormularioUsuario";
import Boton from "../componentes/atomos/Boton";
import BotonEnlace from "../componentes/atomos/BotonEnlace";
import MensajeError from "../componentes/atomos/MensajeError";
import {
    actualizarUsuario,
    cambiarEstadoUsuario,
    crearUsuario,
    eliminarUsuario,
    listarUsuarios
} from "../api/usuarios";
import type { DatosUsuario, Usuario } from "../api/usuarios";

function AdminUsuarios() {
    const [usuarios, setUsuarios] = useState<Usuario[]>([]);
    const [mostrarFormulario, setMostrarFormulario] = useState(false);
    const [usuarioEditar, setUsuarioEditar] = useState<Usuario | null>(null);
    const [error, setError] = useState("");
    const [recargar, setRecargar] = useState(0);

    useEffect(() => {
        listarUsuarios().then(lista => setUsuarios(lista)).catch(err => setError(err.message));
    }, [recargar]);

    const nuevo = () => {
        setUsuarioEditar(null);
        setMostrarFormulario(true);
    };

    const editar = (usuario: Usuario) => {
        setUsuarioEditar(usuario);
        setMostrarFormulario(true);
    };

    const ejecutar = async (accion: () => Promise<unknown>) => {
        try {
            await accion();
            setError("");
            setRecargar(recargar + 1);
            return true;
        } catch (err) {
            setError((err as Error).message);
            return false;
        }
    };

    const guardar = async (datos: DatosUsuario) => {
        const exito = await ejecutar(() => usuarioEditar === null ? crearUsuario(datos) : actualizarUsuario(usuarioEditar.id, datos));
        if (exito) {
            alert("Usuario guardado correctamente");
            setMostrarFormulario(false);
        }
    };

    const eliminar = (id: number) => {
        if (confirm("¿Seguro que desea eliminar este usuario?")) {
            ejecutar(() => eliminarUsuario(id));
        }
    };

    return (
        <div className="container">

            <h1>Usuarios</h1>

            <div className="mb-3">
                <Boton texto="Nuevo usuario" color="success" alHacerClick={nuevo} />
                <BotonEnlace ruta="/admin" texto="Volver al panel" color="secondary" />
            </div>

            <MensajeError mensaje={error} />

            <div className="row g-4">
                <div className={mostrarFormulario ? "col-12 col-lg-8" : "col-12"}>
                    <TablaUsuarios
                        usuarios={usuarios}
                        alEditar={editar}
                        alCambiarEstado={(id) => ejecutar(() => cambiarEstadoUsuario(id))}
                        alEliminar={eliminar}
                    />
                </div>

                {mostrarFormulario && (
                    <div className="col-12 col-lg-4">
                        <div className="card p-3">
                            <FormularioUsuario
                                key={usuarioEditar ? usuarioEditar.id : "nuevo"}
                                usuarioEditar={usuarioEditar}
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

export default AdminUsuarios;
