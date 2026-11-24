// @vitest-environment jsdom
import '@testing-library/jest-dom/vitest';
import { test, expect, vi } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

import ListaServicios from "../componentes/organismos/ListaServicios";
import { serviciosPrueba } from "./datosPrueba";

test("muestra una tarjeta por cada servicio y al agregar entrega el servicio completo", () => {
    const alAgregar = vi.fn();
    render(
        <MemoryRouter>
            <ListaServicios servicios={serviciosPrueba} alAgregar={alAgregar} />
        </MemoryRouter>
    );

    expect(screen.getAllByText("Ver detalle").length).toBe(serviciosPrueba.length);

    fireEvent.click(screen.getAllByText("Agregar")[0]);
    expect(alAgregar).toHaveBeenCalledWith(serviciosPrueba[0]);
});

test("muestra un mensaje cuando la lista está vacía", () => {
    render(
        <MemoryRouter>
            <ListaServicios servicios={[]} alAgregar={() => {}} />
        </MemoryRouter>
    );

    expect(screen.getByText("No se encontraron servicios.")).toBeInTheDocument();
    expect(screen.queryByText("Ver detalle")).toBeNull();
});
