export interface TipoServicio {
    id: number;
    nombre: string;
}

export interface Servicio {
    id: number;
    codigo: string;
    nombre: string;
    tipo: TipoServicio;
    duracion: string;
    modalidad: string;
    precio: number;
    descripcion: string;
    imagen: string;
    destacado: boolean;
}

export const tiposServicio = ["Consulta", "Plan", "Evaluación", "Taller"];

export function filtrarServicios(lista: Servicio[], texto: string, tipo: string): Servicio[] {
    return lista.filter(servicio => {
        const coincideNombre = servicio.nombre.toLowerCase().includes(texto.trim().toLowerCase());
        const coincideTipo = tipo === "" || servicio.tipo.nombre === tipo;
        return coincideNombre && coincideTipo;
    });
}

export function formatearPrecio(precio: number): string {
    return "$" + precio.toLocaleString("es-CL");
}

export function formatearFecha(fecha: string): string {
    const partes = fecha.substring(0, 10).split("-");
    return partes[2] + "-" + partes[1] + "-" + partes[0];
}

export function imagenPorTipo(tipo: string): string {
    if (tipo === "Plan") {
        return "/img/servicio-plan.png";
    }
    if (tipo === "Evaluación") {
        return "/img/servicio-evaluacion.png";
    }
    if (tipo === "Taller") {
        return "/img/servicio-taller.png";
    }
    return "/img/servicio-consulta.png";
}
