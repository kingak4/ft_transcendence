import createClient from "openapi-fetch";
import { paths } from "../types/api"; // Ścieżka do wygenerowanego pliku

export const client = createClient<paths>({

    baseUrl: process.env.BACKEND_URL || 'http://localhost:8080',
    // Tutaj możesz dodać domyślne nagłówki, np. Auth
    headers: {
        Accept: "application/json",
    },
});