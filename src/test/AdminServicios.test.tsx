// @vitest-environment jsdom
import '@testing-library/jest-dom/vitest';
import { test, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { serviciosPrueba } from "./datosPrueba";

vi.mock("../api/catalogo", () => ({
    listarServicios: vi.fn(),
    crearServicio: vi.fn(),
    actualizarServicio: vi.fn(),
    eliminarServicio: vi.fn()
}));

import { actualizarServicio, crearServicio, eliminarServicio, listarServicios } from "../api/catalogo";
import AdminServicios from "../paginas/AdminServicios";

beforeEach(() => {
    vi.mocked(listarServicios).mockResolvedValue(serviciosPrueba);
});

async function mostrarAdmin() {
    render(
        <MemoryRouter>
            <AdminServicios />
        </MemoryRouter>
    );
    await screen.findByText("Antropometría completa");
}

test("CREATE: el formulario envía el servicio nuevo a la API", async () => {
    const alerta = vi.spyOn(window, "alert").mockImplementation(() => {});
    vi.mocked(crearServicio).mockResolvedValue(serviciosPrueba[0]);
    await mostrarAdmin();

    fireEvent.click(screen.getByText("Nuevo servicio"));
    fireEvent.change(screen.getByLabelText("Código"), { target: { value: "ON001" } });
    fireEvent.change(screen.getByLabelText("Nombre"), { target: { value: "Consulta online" } });
    fireEvent.change(screen.getByLabelText("Tipo"), { target: { value: "Consulta" } });
    fireEvent.change(screen.getByLabelText("Duración"), { target: { value: "40 min" } });
    fireEvent.change(screen.getByLabelText("Modalidad"), { target: { value: "Online" } });
    fireEvent.change(screen.getByLabelText("Precio"), { target: { value: "30000" } });
    fireEvent.change(screen.getByLabelText("Descripción"), { target: { value: "Atención por videollamada." } });
    fireEvent.click(screen.getByText("Guardar"));

    await waitFor(() => expect(alerta).toHaveBeenCalledWith("Servicio creado correctamente"));
    expect(vi.mocked(crearServicio).mock.calls[0][0].codigo).toBe("ON001");
    expect(vi.mocked(crearServicio).mock.calls[0][0].tipo).toEqual({ nombre: "Consulta" });

    alerta.mockRestore();
});

test("UPDATE: al editar se envía el nuevo precio con el id del servicio", async () => {
    const alerta = vi.spyOn(window, "alert").mockImplementation(() => {});
    vi.mocked(actualizarServicio).mockResolvedValue(serviciosPrueba[0]);
    await mostrarAdmin();

    fireEvent.click(screen.getAllByText("Editar")[0]);
    expect(screen.getByLabelText("Precio")).toHaveValue(35000);

    fireEvent.change(screen.getByLabelText("Precio"), { target: { value: "40000" } });
    fireEvent.click(screen.getByText("Guardar"));

    await waitFor(() => expect(alerta).toHaveBeenCalledWith("Servicio actualizado correctamente"));
    expect(vi.mocked(actualizarServicio).mock.calls[0][0]).toBe(1);
    expect(vi.mocked(actualizarServicio).mock.calls[0][1].precio).toBe(40000);

    alerta.mockRestore();
});

test("DELETE: al confirmar se pide a la API eliminar el servicio", async () => {
    const confirmar = vi.spyOn(window, "confirm").mockReturnValue(true);
    vi.mocked(eliminarServicio).mockResolvedValue(null);
    await mostrarAdmin();

    fireEvent.click(screen.getAllByText("Eliminar")[4]);

    expect(confirmar).toHaveBeenCalled();
    await waitFor(() => expect(eliminarServicio).toHaveBeenCalledWith(5));

    confirmar.mockRestore();
});
