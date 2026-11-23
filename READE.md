# Calculadora – Control de versiones (comandos)

Actividad evaluable de Entornos de Desarrollo (0487): **Control de versiones con Git y GitHub**.

Aplicación en **Java + HTML** que resuelve exclusivamente **suma, resta, multiplicación y potencias** de números enteros de 5 dígitos como máximo, en el rango **[-99999 … 99999]**.

## Estructura

| Archivo | Rama | Descripción |
|---|---|---|
| `README.md` | `main` | Presentación del proyecto |
| `READE.md` | `main` | Comandos utilizados en orden cronológico |
| `Calculadora.java` | `backend` | Servidor HTTP con las operaciones y la validación |
| `index.html` | `frontend` | Interfaz web que llama al backend |

## Ejecución

Requisito: Java 11 o superior. Desde la carpeta del repositorio:

```bash
java Calculadora.java
```

Abrir <http://localhost:8080>.

## Ramas

- **main**: solo el commit inicial, commits de actualización de comentarios/documentación y los merges.
- **frontend**: solo commits que modifican archivos `.html`.
- **backend**: solo commits que modifican archivos `.java`.

---

## Comandos utilizados (orden cronológico)

### 0. Configuración de Git (una sola vez por equipo)

```bash
git config --global user.name "byMestral05"
git config --global user.email "correo-de-la-cuenta@ejemplo.com"
```

Identifican al autor de los commits. El email debe ser el de la cuenta de GitHub para que los commits aparezcan asociados al perfil.

### 1. Crear el repositorio en GitHub (con README)

1. Entrar en <https://github.com> e iniciar sesión.
2. Botón **+** (arriba a la derecha) → **New repository**.
3. Nombre: `EDD-ControlVersiones`. Visibilidad: **Public**.
4. Activar **Add README** para que el repositorio nazca con un commit inicial en `main`.
5. **Create repository**.

### 2. Clonar el repositorio

```bash
git clone https://github.com/byMestral05/EDD-ControlVersiones.git
cd EDD-ControlVersiones
```

- `git clone`: descarga el repositorio con todo su historial y deja configurado el remoto `origin`.
- `cd`: entra en la carpeta del proyecto.

### 3. Crear las ramas de trabajo y subirlas

```bash
git branch backend
git branch frontend
git push -u origin backend
git push -u origin frontend
git branch -a
```

- `git branch <nombre>`: crea una rama a partir del commit actual de `main`.
- `git push -u origin <rama>`: publica la rama en GitHub y la enlaza para futuros `git push`.
- `git branch -a`: lista las ramas locales y remotas.

### 4. Backend (solo archivos `.java`)

```bash
git switch backend
# Se crea Calculadora.java con el servidor HTTP que sirve el frontend
git add Calculadora.java
git commit -m "Crea el servidor HTTP que sirve el frontend"
# Se añaden las operaciones
git add Calculadora.java
git commit -m "Añade suma, resta, multiplicación y potencia"
# Se añade la validación del rango de entrada
git add Calculadora.java
git commit -m "Valida que los operandos sean enteros entre -99999 y 99999"
git push
```

- `git switch <rama>`: cambia de rama.
- `git add <archivo>`: añade los cambios al área de preparación (*staging*).
- `git commit -m "mensaje"`: guarda una instantánea con un mensaje descriptivo.
- `git push`: sube los commits nuevos de la rama a GitHub.

### 5. Frontend (solo archivos `.html`)

```bash
git switch frontend
# Se crea index.html con el formulario
git add index.html
git commit -m "Crea el formulario de la calculadora"
# Se conecta el formulario con el backend
git add index.html
git commit -m "Conecta el formulario con el backend Java"
# Se añaden estilos
git add index.html
git commit -m "Añade estilos a la interfaz"
# Se añade la validación en el cliente
git add index.html
git commit -m "Valida en el cliente el rango de los operandos"
git push
```

### 6. Fusionar las ramas en `main`

Se hace mediante *Pull Requests* en GitHub (pestaña **Pull requests → New pull request**, base `main`, compare `backend` / `frontend`, **Create merge commit**). El equivalente por consola es:

```bash
git switch main
git pull
git merge --no-ff backend
git merge --no-ff frontend
git push
```

- `git pull`: trae a local los cambios del remoto.
- `git merge --no-ff <rama>`: integra la rama en `main` creando siempre un commit de merge, así en el historial se ve cada rama.

### 7. Actualizar la documentación en `main`

```bash
git switch main
git pull
# Se crea READE.md con los comandos utilizados y se actualiza README.md
git add READE.md README.md
git commit -m "Documenta los comandos utilizados en READE.md"
git push
```

### 8. Comprobación

```bash
git log --oneline --graph --all
git status
```

En GitHub: **Branches** muestra `main`, `frontend` y `backend`; **Insights → Network** muestra el gráfico de ramas y merges.

---

## Cómo publicar en GitHub un repositorio creado en local

Si en lugar de clonar se empieza con `git init` en local:

```bash
git init
git add .
git commit -m "Commit inicial"
git branch -M main
git remote add origin https://github.com/USUARIO/REPOSITORIO.git
git push -u origin main
```

- `git init`: crea un repositorio vacío en la carpeta actual.
- `git branch -M main`: renombra la rama actual a `main`.
- `git remote add origin <url>`: asocia el repositorio local al remoto de GitHub (que debe crearse vacío, **sin** README, para evitar conflictos).
- `git push -u origin main`: sube la rama `main` y la deja enlazada.
- Al hacer `push`, GitHub pide autenticación: usuario y un **Personal Access Token** (Settings → Developer settings → Personal access tokens) o inicio de sesión con Git Credential Manager.
