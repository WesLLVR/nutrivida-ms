// @vitest-environment jsdom
import '@testing-library/jest-dom/vitest';
import { test, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import { serviciosPrueba } from "./datosPrueba";

vi.mock("../api/catalogo", () => ({
    buscarServicioPorCodigo: vi.fn((codigo: string) => Promise.resolve(serviciosPrueba.find(s => s.codigo === codigo)))
}));

import DetalleServicio from "../paginas/DetalleServicio";

test("muestra el servicio que corresponde al código de la URL", async () => {
    render(
        <MemoryRouter initialEntries={["/servicio/PL003"]}>
            <Routes>
                <Route path="/servicio/:codigo" element={<DetalleServicio alAgregar={() => {}} />} />
            </Routes>
        </MemoryRouter>
    );

    expect(await screen.findByText("Plan nutrición deportiva")).toBeInTheDocument();
    expect(screen.getByText("Valor: $70.000")).toBeInTheDocument();
});
