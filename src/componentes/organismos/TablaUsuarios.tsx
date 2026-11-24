import Boton from "../atomos/Boton";
import Etiqueta from "../atomos/Etiqueta";
import type { Usuario } from "../../api/usuarios";

interface TablaProps {
    usuarios: Usuario[];
    alEditar: (usuario: Usuario) => void;
    alCambiarEstado: (id: number) => void;
    alEliminar: (id: number) => void;
}

function TablaUsuarios({ usuarios, alEditar, alCambiarEstado, alEliminar }: TablaProps) {
    return (
        <div className="table-responsive">
            <table className="table table-bordered align-middle">
                <thead>
                    <tr>
                        <th>RUN</th>
                        <th>Nombre</th>
                        <th>Correo</th>
                        <th>Rol</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {usuarios.map(usuario => (
                        <tr key={usuario.id}>
                            <td className="text-nowrap">{usuario.run.slice(0, -1) + "-" + usuario.run.slice(-1)}</td>
                            <td>{usuario.nombre} {usuario.apellidos}</td>
                            <td>{usuario.correo}</td>
                            <td>{usuario.rol.nombre}</td>
                            <td>
                                <Etiqueta texto={usuario.activo ? "Activo" : "Inactivo"} color={usuario.activo ? "success" : "secondary"} />
                            </td>
                            <td>
                                <Boton texto="Editar" color="outline-primary" alHacerClick={() => alEditar(usuario)} />
                                <Boton
                                    texto={usuario.activo ? "Desactivar" : "Activar"}
                                    color="outline-secondary"
                                    alHacerClick={() => alCambiarEstado(usuario.id)}
                                />
                                <Boton texto="Eliminar" color="outline-danger" alHacerClick={() => alEliminar(usuario.id)} />
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default TablaUsuarios;
