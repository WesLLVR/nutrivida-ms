import { peticion } from "./cliente";
import type { Servicio } from "../datos/servicios";

export interface Nutricionista {
    id: number;
    codigo: string;
    nombre: string;
    especialidad: string;
    diasAtencion: string;
    horario: string;
    correo: string;
}

export interface DatosServicio {
    codigo: string;
    nombre: string;
    tipo: { nombre: string };
    duracion: string;
    modalidad: string;
    precio: number;
    descripcion: string;
    imagen: string;
    destacado: boolean;
}

export function listarServicios(): Promise<Servicio[]> {
    return peticion<Servicio[]>("/servicios");
}

export function buscarServicioPorCodigo(codigo: string): Promise<Servicio> {
    return peticion<Servicio>("/servicios/codigo/" + codigo);
}

export function crearServicio(datos: DatosServicio): Promise<Servicio> {
    return peticion<Servicio>("/servicios", "POST", datos);
}

export function actualizarServicio(id: number, datos: DatosServicio): Promise<Servicio> {
    return peticion<Servicio>("/servicios/" + id, "PUT", datos);
}

export function eliminarServicio(id: number): Promise<null> {
    return peticion<null>("/servicios/" + id, "DELETE");
}

export function listarNutricionistas(): Promise<Nutricionista[]> {
    return peticion<Nutricionista[]>("/nutricionistas");
}
