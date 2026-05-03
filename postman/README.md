# Colección Postman — Hospedate

## Archivos

- `Hospedate.postman_collection.json` — colección con todos los endpoints REST agrupados por recurso (Auth, Users, Hotels, Reserves, Reviews, Images, Admin).
- `Hospedate.postman_environment.json` — environment con variables: `baseUrl`, credenciales de admin/user, e IDs de prueba (`hotelId`, `userId`, `reserveId`, `reviewId`, `imageId`).

Todas las URLs de la colección usan `{{baseUrl}}` — nunca hay rutas hardcodeadas.

## Cómo importar en Postman

1. Postman → **Import** → arrastra los dos `.json` de esta carpeta.
2. Esquina superior derecha → selecciona el environment **"Hospedate Local"**.
3. Arranca la app (`https://localhost:8443`). Acepta el certificado autofirmado en el navegador la primera vez.
4. En Postman: Settings → General → desactivar **"SSL certificate verification"** (o aceptar el cert).

## Flujo recomendado de pruebas

1. **Auth → Login (admin)** — autentica como `admin / admin123`. La cookie/sesión se guarda automáticamente.
2. **Hotels → Create hotel (admin)** — crea un hotel; copia el `id` de la respuesta a la variable `hotelId`.
3. **Hotels → List / Get / Update / Delete** — usa `{{hotelId}}` en las que requieran ID.
4. **Auth → Login (user)** — cambia a `user1 / user123` para probar endpoints de usuario y los 401 en `/api/v1/admin/**`.
5. **Reserves / Reviews** — crear/listar/borrar usando `{{hotelId}}`.
6. **Admin → List/Get/Update/Delete users y reserves** — solo accesibles con sesión admin.

## Cambiar entorno

Para apuntar a otra URL (deploy remoto, otro puerto), edita la variable `baseUrl` del environment. No hace falta tocar la colección.
