# JTileEngine

**Java 2D Game Engine**

_Petr Chalupa, PJV, 2025_

Project vision (CP1), object design (CP2) and manual are in the `docs` folder.

## Installation

### Prerequisites

- **Java Development Kit (JDK) 21**
- **Apache Maven**

Maven handles JavaFX automatically, no separate installation is required.

### Building the project

1. Clone the repository and navigate into the root directory
2. Then build the entire project using: ```mvn clean install```

This will compile both modules: `engine` and `ui`.

### Running the application

From the root folder, navigate to the `ui` module:

```bash
cd ui
mvn javafx:run
```

This launches the application with the main class `ui.App`.