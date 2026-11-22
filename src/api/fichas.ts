import { peticion } from "./cliente";

export interface Ficha {
    id: number;
    fecha: string;
    peso: number;
    talla: number;
    cintura: number | null;
    imc: number;
    observaciones: string;
    planAlimenticio: string;
    pacienteId: number;
    paciente: string;
    nutricionistaId: number;
    nutricionista: string;
}

export interface DatosFicha {
    pacienteId: number;
    fecha: string;
    peso: number;
    talla: number;
    cintura: number | null;
    observaciones: string;
    planAlimenticio: string;
}

export function listarMisFichas(): Promise<Ficha[]> {
    return peticion<Ficha[]>("/fichas/mis-fichas");
}

export function crearFicha(datos: DatosFicha): Promise<Ficha> {
    return peticion<Ficha>("/fichas", "POST", datos);
}

export function actualizarFicha(id: number, datos: DatosFicha): Promise<Ficha> {
    return peticion<Ficha>("/fichas/" + id, "PUT", datos);
}

export function eliminarFicha(id: number): Promise<null> {
    return peticion<null>("/fichas/" + id, "DELETE");
}
