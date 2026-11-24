import { test, expect } from "vitest";
import { filtrarServicios, formatearFecha, formatearPrecio, imagenPorTipo } from "../datos/servicios";
import { serviciosPrueba } from "./datosPrueba";

test("filtrarServicios encuentra servicios por nombre sin importar mayúsculas", () => {
    const resultado = filtrarServicios(serviciosPrueba, "PLAN", "");

    expect(resultado.length).toBe(2);
});

test("filtrarServicios combina el texto con el tipo elegido", () => {
    const resultado = filtrarServicios(serviciosPrueba, "consulta", "Consulta");

    expect(resultado.length).toBe(1);
    expect(resultado[0].codigo).toBe("CN001");
});

test("formatearPrecio y formatearFecha muestran los datos al estilo chileno", () => {
    expect(formatearPrecio(85000)).toBe("$85.000");
    expect(formatearFecha("2026-11-20")).toBe("20-11-2026");
});

test("imagenPorTipo entrega la foto que corresponde al tipo", () => {
    expect(imagenPorTipo("Taller")).toBe("/img/servicio-taller.png");
    expect(imagenPorTipo("Consulta")).toBe("/img/servicio-consulta.png");
});
