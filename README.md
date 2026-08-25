# 📦 Sistema de Gestión Comercial y Kardex (TecPro)

Este es un proyecto de aplicación de escritorio desarrollado en Java. Lo construí con el objetivo principal de **aprender de manera constante**, buscando siempre aplicar e interiorizar las **buenas prácticas de desarrollo de software** conforme avanzo en mi carrera de Ingeniería de Sistemas Computacionales.

Es un sistema funcional que utilizo como base para identificar oportunidades de mejora, refactorizar código y acercarme a estándares más profesionales.

## 🛠️ Tecnologías y Arquitectura

* **Lenguaje:** Java
* **Interfaz Gráfica:** Java Swing
* **Base de Datos:** SQL Server
* **Patrón de Diseño:** DAO (Data Access Object) para separar la capa de persistencia de la lógica de negocio y la vista.

## ⚙️ Funcionalidades

* Gestión de inventario mediante Kardex (control de entradas, salidas y cálculo de costo promedio).
* Módulo de ventas y compras con validación de stock en tiempo real.
* Directorio de registro para clientes y proveedores.

## 🌱 Puntos de Mejora Identificados (Roadmap)

Al revisar y analizar el código actual con una visión más madura, he identificado los siguientes aspectos arquitectónicos que planeo optimizar:

1. **Migración a una API REST:** Actualmente, el sistema realiza la conexión a la base de datos de manera directa desde la aplicación de escritorio. El siguiente paso es desacoplar esta lógica construyendo un Backend (API REST) que gestione las peticiones, lo cual mejorará drásticamente la seguridad y escalabilidad del proyecto.
2. **Refactorización con Herencia (OOP):** He notado que en las clases relacionadas a los Pedidos y sus Detalles existe la oportunidad de optimizar el código. Planeo implementar una clase padre para abstraer los atributos y métodos compartidos, ahorrando espacio, evitando redundancias y aplicando correctamente los principios de la Programación Orientada a Objetos.

## 🚀 Cómo ejecutar el proyecto localmente

Si deseas descargar el código y probar el sistema en tu entorno local:

### 1. Clonar el repositorio
```bash
git clone https://github.com/AndradeOlver/TecPro.git
```

### 2. Importar las Librerías (.jar)
Dado que el proyecto utiliza dependencias locales, es necesario que descargues y añadas manualmente los siguientes archivos `.jar` al *Build Path* o *Libraries* de tu IDE (recomendado: Apache NetBeans):
* **Microsoft JDBC Driver para SQL Server:** Necesario para establecer la conexión con la base de datos.
* **JCalendar (com.toedter.calendar):** Requerido para renderizar los componentes visuales de selección de fechas en los formularios.

### 3. Configuración Segura de la Base de Datos
El proyecto está configurado para leer las credenciales de conexión desde un archivo externo, evitando exponer contraseñas en el código fuente:
1. En la raíz del proyecto encontrarás el archivo de plantilla `config.properties.example`.
2. Crea una copia exacta de ese archivo y renómbrala a **`config.properties`** (este archivo ya está configurado en el `.gitignore` para no subirse al repositorio).
3. Abre `config.properties` y coloca tus credenciales reales:
```properties
db.servidor=localhost
db.puerto=1433
db.nombre=nombre_de_tu_base_de_datos
db.usuario=tu_usuario_sql
db.clave=tu_password_sql
```

### 4. Ejecución
Una vez importadas las librerías y configurado el archivo de propiedades, compila y ejecuta el proyecto. El punto de entrada principal es la clase `Vista.fmrLogin` o `Vista.FrmMenu`.
