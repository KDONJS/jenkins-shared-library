>[!NOTE]
>esta es una nota de aviso
# Jenkins shared library

Este proyecto esta pensado como practica de implementacion de librearias compartidas en Jenkins usando groovy




## License

[MIT](https://choosealicense.com/licenses/mit/)


## Tech Stack

**Libreria:** Groovy, yml y java


## 🔗 Links

[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/yorlin-quispe-ygnacio-5761a1190/)


## Usage/Examples

```groovy
@Library('jenkins-shared-library') _

pipeline {
    // ...
    steps {
        script {
            MyLibrary library = new MyLibrary()
            library.myLibraryMethod()
        }
    }
    // ...
}

```

