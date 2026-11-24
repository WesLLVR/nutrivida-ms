// @vitest-environment jsdom
import '@testing-library/jest-dom/vitest';
import { test, expect, vi } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { serviciosPrueba } from "./datosPrueba";

vi.mock("../api/catalogo", () => ({
    listarServicios: vi.fn().mockResolvedValue(serviciosPrueba)
}));

import Servicios from "../paginas/Servicios";

async function mostrarServicios() {
    render(
        <MemoryRouter>
            <Servicios alAgregar={() => {}} />
        </MemoryRouter>
    );
    await screen.findByText("Control nutricional");
}

test("al escribir en el buscador solo quedan los servicios que coinciden", async () => {
    await mostrarServicios();

    expect(screen.getAllByText("Ver detalle").length).toBe(6);

    fireEvent.change(screen.getByLabelText("Buscar por nombre"), { target: { value: "taller" } });

    expect(screen.getAllByText("Ver detalle").length).toBe(1);
    expect(screen.getByText("Taller de alimentación saludable")).toBeInTheDocument();
});

test("al elegir un tipo solo se muestran servicios de ese tipo", async () => {
    await mostrarServicios();

    fireEvent.change(screen.getByLabelText("Tipo"), { target: { value: "Plan" } });

    expect(screen.getAllByText("Ver detalle").length).toBe(2);
    expect(screen.queryByText("Control nutricional")).toBeNull();
});
